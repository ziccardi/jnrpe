/*******************************************************************************
 * Copyright (c) 2007, 2014 Massimiliano Ziccardi
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *******************************************************************************/
package it.jnrpe.plugin;

import it.jnrpe.ICommandLine;
import it.jnrpe.Status;
import it.jnrpe.plugins.Metric;
import it.jnrpe.plugins.MetricGatheringException;
import it.jnrpe.plugins.PluginBase;
import it.jnrpe.utils.BadThresholdException;
import it.jnrpe.utils.thresholds.ThresholdsEvaluatorBuilder;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Performs standard checks against an oracle database server.
 * 
 * @author Massimiliano Ziccardi
 * 
 */
public class CCheckOracle extends PluginBase {

	/**
	 * Plugin name constant.
	 */
	private static final String PLUGIN_NAME = "CHECK_ORACLE";

	/**
	 * Connects to the database.
	 * 
	 * @param cl
	 *            The plugin command line as received by JNRPE
	 * @return The connection to the database
	 * @throws SQLException
	 *             -
	 * @throws InstantiationException
	 *             -
	 * @throws IllegalAccessException
	 *             -
	 * @throws ClassNotFoundException
	 *             -
	 */
	private Connection getConnection(final ICommandLine cl)
			throws SQLException, InstantiationException,
			IllegalAccessException, ClassNotFoundException {
		DriverManager.registerDriver((Driver) Class.forName(
				"oracle.jdbc.driver.OracleDriver").newInstance());

		log.debug("Connecting to " + cl.getOptionValue("db") + "@"
				+ cl.getOptionValue("server"));

		Connection conn = DriverManager.getConnection(
				"jdbc:oracle:thin:@" + cl.getOptionValue("server") + ":"
						+ cl.getOptionValue("port", "1521") + ":"
						+ cl.getOptionValue("db"),
				cl.getOptionValue("username"), cl.getOptionValue("password"));

		return conn;
	}

	/**
	 * Checks if the database is reacheble.
	 * 
	 * @param c
	 *            The connection to the database
	 * @param cl
	 *            The command line as received from JNRPE
	 * @return The plugin result
	 * @throws BadThresholdException
	 *             -
	 * @throws SQLException
	 */
	private List<Metric> checkAlive(final Connection c, final ICommandLine cl)
			throws BadThresholdException, SQLException {

		List<Metric> metricList = new ArrayList<Metric>();
		Statement stmt = null;

		long lStart = System.currentTimeMillis();

		try {
			stmt = c.createStatement();
			stmt.executeQuery("SELECT SYSDATE FROM DUAL");

			long elapsed = (System.currentTimeMillis() - lStart) / 1000L;

			metricList.add(new Metric("conn", "Connection time : " + elapsed
					+ "s", new BigDecimal(elapsed), new BigDecimal(0), null));

			return metricList;

		} finally {
			try {
				if (stmt != null) {
					stmt.close();
				}
			} catch (Exception e) {
				// Intentionally ignored...
			}
		}
	}

	/**
	 * Checks database usage.
	 * 
	 * @param c
	 *            The connection to the database
	 * @param cl
	 *            The command line as received from JNRPE
	 * @return The plugin result
	 * @throws BadThresholdException
	 *             -
	 */
	private List<Metric> checkTablespace(final Connection c,
			final ICommandLine cl) throws BadThresholdException, SQLException {

		// Metric : tblspace_usage

		List<Metric> metricList = new ArrayList<Metric>();

		String sTablespace = cl.getOptionValue("tablespace").toUpperCase();

		final String sQry = "select NVL(b.free,0.0),a.total,100 "
				+ "- trunc(NVL(b.free,0.0)/a.total * 1000) / 10 prc"
				+ " from ("
				+ " select tablespace_name,sum(bytes)/1024/1024 total"
				+ " from dba_data_files group by tablespace_name) A"
				+ " LEFT OUTER JOIN"
				+ " ( select tablespace_name,sum(bytes)/1024/1024 free"
				+ " from dba_free_space group by tablespace_name) B"
				+ " ON a.tablespace_name=b.tablespace_name "
				+ "WHERE a.tablespace_name='" + sTablespace + "'";

		Statement stmt = null;
		ResultSet rs = null;

		try {
			stmt = c.createStatement();

			rs = stmt.executeQuery(sQry);

			boolean bFound = rs.next();

			if (!bFound) {
				throw new SQLException("Tablespace "
						+ cl.getOptionValue("tablespace") + " not found.");
			}

			BigDecimal tsFree = rs.getBigDecimal(1);
			BigDecimal tsTotal = rs.getBigDecimal(2);
			BigDecimal tsPct = rs.getBigDecimal(3);
			//
			metricList.add(new Metric("tblspace_freepct", cl
					.getOptionValue("tablespace") + " : " + tsPct + "% free",
					tsPct, new BigDecimal(0), new BigDecimal(100)));

			metricList.add(new Metric("tblspace_free", cl
					.getOptionValue("tablespace") + " : " + tsFree + "MB free",
					tsPct, new BigDecimal(0), tsTotal));

			return metricList;

		} finally {
			try {
				if (stmt != null) {
					stmt.close();
				}
			} catch (Exception e) {
				// Intentionally ignored...
			}
		}

	}

	/**
	 * Checks cache hit rates.
	 * 
	 * @param c
	 *            The connection to the database
	 * @param cl
	 *            The command line as received from JNRPE
	 * @return The result of the plugin
	 * @throws BadThresholdException
	 *             -
	 */
	private List<Metric> checkCache(final Connection c, final ICommandLine cl)
			throws BadThresholdException, SQLException {

		List<Metric> metricList = new ArrayList<Metric>();
		// Metrics cache_buf, cache_lib

		String sQry1 = "select (1-(pr.value/(dbg.value+cg.value)))*100"
				+ " from v$sysstat pr, v$sysstat dbg, v$sysstat cg"
				+ " where pr.name='physical reads'"
				+ " and dbg.name='db block gets'"
				+ " and cg.name='consistent gets'";

		String sQry2 = "select sum(lc.pins)/(sum(lc.pins)"
				+ "+sum(lc.reloads))*100 from v$librarycache lc";

		Statement stmt = null;
		ResultSet rs = null;

		try {
			stmt = c.createStatement();

			rs = stmt.executeQuery(sQry1);
			rs.next();

			BigDecimal buf_hr = rs.getBigDecimal(1);

			rs = stmt.executeQuery(sQry2);
			rs.next();

			BigDecimal lib_hr = rs.getBigDecimal(1);

			String libHitRate = "Cache Hit Rate {1,number,0.#}% Lib";
			String buffHitRate = "Cache Hit Rate {1,number,0.#}% Buff";

			metricList.add(new Metric("cache_buf", MessageFormat.format(
					buffHitRate, buf_hr), buf_hr, new BigDecimal(0),
					new BigDecimal(100)));
			metricList.add(new Metric("cache_lib", MessageFormat.format(
					libHitRate, lib_hr), lib_hr, new BigDecimal(0),
					new BigDecimal(100)));

			return metricList;
		} finally {
			try {
				if (stmt != null) {
					stmt.close();
				}
			} catch (Exception e) {
				// Intentionally ignored...
			}
		}

	}

	@Override
	public void configureThresholdEvaluatorBuilder(
			final ThresholdsEvaluatorBuilder thrb, final ICommandLine cl)
			throws BadThresholdException {
		if (cl.hasOption("th")) {
			super.configureThresholdEvaluatorBuilder(thrb, cl);
		} else {
			if (cl.hasOption("alive")) {
				thrb.withLegacyThreshold("conn", null,
						cl.getOptionValue("warning"),
						cl.getOptionValue("critical"));
			}

			if (cl.hasOption("tablespace")) {
				thrb.withLegacyThreshold("tblspace_freepct", null,
						cl.getOptionValue("warning", "70"),
						cl.getOptionValue("critical", "80"));
			}

			if (cl.hasOption("cache")) {
				thrb.withLegacyThreshold("cache_buf", null,
						cl.getOptionValue("warning", "70"),
						cl.getOptionValue("critical", "80"));
			}
		}
	}

	@Override
	public Collection<Metric> gatherMetrics(final ICommandLine cl)
			throws MetricGatheringException {
		Connection conn = null;

		try {
			conn = getConnection(cl);

			List<Metric> metricList = new ArrayList<Metric>();
			metricList.addAll(checkAlive(conn, cl));

			if (cl.hasOption("tablespace")) {
				metricList.addAll(checkTablespace(conn, cl));
			}
			metricList.addAll(checkCache(conn, cl));

			return metricList;
		} catch (ClassNotFoundException cnfe) {
			log.error("Oracle driver library not found into the classpath: "
					+ "download and put it in the same directory "
					+ "of this plugin");

			throw new MetricGatheringException(cnfe.getMessage(),
					Status.UNKNOWN, cnfe);
		} catch (SQLException sqle) {
			log.error("Error communicating with database.", sqle);

			throw new MetricGatheringException(sqle.getMessage(),
					Status.CRITICAL, sqle);

		} catch (Exception e) {
			log.fatal("Error communicating with database.", e);

			throw new MetricGatheringException(e.getMessage(), Status.UNKNOWN,
					e);
		} finally {
			if (conn != null) {
				try {
					conn.close();
				} catch (Exception e) {
					log.warn("Error closing the DB connection.", e);
				}
			}
		}
	}

	@Override
	protected String getPluginName() {
		return PLUGIN_NAME;
	}
}
