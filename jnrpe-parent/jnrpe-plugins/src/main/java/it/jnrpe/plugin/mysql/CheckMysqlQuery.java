/*
 * Copyright (c) 2013 Massimiliano Ziccardi
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
 */
package it.jnrpe.plugin.mysql;

import it.jnrpe.ICommandLine;
import it.jnrpe.ReturnValue;
import it.jnrpe.ReturnValue.UnitOfMeasure;
import it.jnrpe.Status;
import it.jnrpe.plugins.Metric;
import it.jnrpe.plugins.MetricGatheringException;
import it.jnrpe.plugins.PluginBase;
import it.jnrpe.utils.BadThresholdException;
import it.jnrpe.utils.ThresholdUtil;
import it.jnrpe.utils.thresholds.ThresholdsEvaluatorBuilder;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Plugin that checks a mysql query result against threshold levels.
 *
 * @author Frederico Campos
 */
public class CheckMysqlQuery extends PluginBase {

	@Override
	public void configureThresholdEvaluatorBuilder(
			ThresholdsEvaluatorBuilder thrb, ICommandLine cl)
					throws BadThresholdException {

		if (cl.hasOption("th")) {
			super.configureThresholdEvaluatorBuilder(thrb, cl);
		} else {
			thrb.withLegacyThreshold("rows", null,
					cl.getOptionValue("warning"), cl.getOptionValue("critical"));
		}

	}

	/**
	 * Execute and gather metrics
	 */
	public Collection<Metric> gatherMetrics(ICommandLine cl) throws MetricGatheringException {
		log.debug("check_mysql_query gather metrics");
		List<Metric> metrics = new ArrayList<Metric>();
		Mysql mysql = new Mysql(cl);
		Connection conn = null;
		try {
			conn = mysql.getConnection();
		} catch (ClassNotFoundException e) {
			log.error("Mysql driver library not found into the classpath"
					+ ": download and put it in the same directory "
					+ "of this plugin");
			throw new MetricGatheringException("CHECK_MYSQL_QUERY - CRITICAL: Error accessing the "
					+ "MySQL server - JDBC driver not installed", Status.CRITICAL, e);
		} catch (Exception e) {
			log.error("Error accessing the MySQL server", e);
			throw new MetricGatheringException("CHECK_MYSQL_QUERY - CRITICAL: Error accessing "
					+ "the MySQL server - " + e.getMessage(), Status.CRITICAL,e);
		}

		String query = cl.getOptionValue("query");
		Statement st = null;
		ResultSet set = null;
		try {
			st = conn.createStatement();
			st.execute(query);
			set = st.getResultSet();
			BigDecimal value = null;
			if (set.first()) {
				value = set.getBigDecimal(1);
			}

			metrics.add(new Metric("rows", "CHECK_MYSQL_QUERY - Returned value is " + 
					value.longValue(), 
					value, 
					null, 
					null));

		} catch (SQLException e) {
			log.warn("Error executing plugin CheckMysqlQuery : " + e.getMessage(), e);
			throw new MetricGatheringException("CHECK_MYSQL_QUERY - CRITICAL: " + e.getMessage(), Status.CRITICAL, e);
		} finally {
			if (st != null) {
				try {
					st.close();
				} catch (SQLException e) {
					log.error("Error closing MySQL statement", e);
				}
			}
			if (set != null) {
				try {
					set.close();
				} catch (SQLException e) {
					log.error("Error closing MySQL ResultSet", e);
				}
			}
			mysql.closeConnection(conn);
		}

		return metrics;
	}


	@Override
	protected String getPluginName() {
		return "CHECK_MYSQL_QUERY";
	}
}
