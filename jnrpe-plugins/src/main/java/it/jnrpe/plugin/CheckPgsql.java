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
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Properties;

/**
 * Tests connections to a PostgreSQL Database.
 *
 * @author Frederico Campos
 */
public class CheckPgsql extends PluginBase {

    /*
     * default settings
     */

    /**
     * Default hostname.
     */
    private static final String DEFAULT_HOSTNAME = "localhost";

    /**
     * Default server port.
     */
    private static final String DEFAULT_PORT = "5432";

    /**
     * Default server table.
     */
    private static final String DEFAULT_TABLE = "template1";

    /**
     * Default timeout.
     */
    private static final String DEFAULT_TIMEOUT = "10";

    @Override
    public void configureThresholdEvaluatorBuilder(final ThresholdsEvaluatorBuilder thrb, final ICommandLine cl) throws BadThresholdException {
        if (cl.hasOption("th")) {
            super.configureThresholdEvaluatorBuilder(thrb, cl);
        } else {
            thrb.withLegacyThreshold("conn", null, cl.getOptionValue("warning"), cl.getOptionValue("critical"));
        }
    }

    @Override
    public Collection<Metric> gatherMetrics(ICommandLine cl) throws MetricGatheringException {

        List<Metric> metricList = new ArrayList<Metric>();

        Connection conn = null;
        Long start = System.currentTimeMillis();

        try {
            conn = getConnection(cl);
        } catch (ClassNotFoundException e) {
            log.error("PostgreSQL driver library not found into the classpath: " + "download and put it in the same directory of " + "this plugin");
            throw new MetricGatheringException("Error accessing the PostgreSQL " + "server - JDBC driver not installed", Status.CRITICAL, e);
        } catch (Exception e) {
            log.error("Error accessing the PostgreSQL server", e);
            throw new MetricGatheringException("Error accessing the PostgreSQL " + "server - ", Status.CRITICAL, e);
        } finally {
            closeConnection(conn);
        }

        Long end = System.currentTimeMillis();
        Long elapsed = new Long((end - start) / 1000);

        metricList.add(new Metric("conn", "Connection time : " + elapsed + "s", new BigDecimal(elapsed), new BigDecimal(0), null));

        return metricList;
    }

    /**
     * Connect to the server.
     *
     * @param cl
     *            The command line
     * @return The connection
     * @throws SQLException
     *             -
     * @throws InstantiationException
     *             -
     * @throws IllegalAccessException
     *             -
     * @throws ClassNotFoundException
     *             -
     */
    private Connection getConnection(final ICommandLine cl) throws SQLException, InstantiationException, IllegalAccessException,
            ClassNotFoundException {
        String database = DEFAULT_TABLE;
        if (cl.hasOption("database")) {
            database = cl.getOptionValue("database");
        }
        String hostname = DEFAULT_HOSTNAME;
        if (cl.hasOption("hostname") && !"".equals(cl.getOptionValue("hostname"))) {
            hostname = cl.getOptionValue("hostname");
        }
        String port = DEFAULT_PORT;
        if (cl.hasOption("port") && !"".equals(cl.getOptionValue("port"))) {
            port = cl.getOptionValue("port");
        }
        String password = "";
        if (cl.hasOption("password")) {
            password = cl.getOptionValue("password");
        }
        String username = "";
        if (cl.hasOption("logname")) {
            username = cl.getOptionValue("logname");
        }
        String timeout = DEFAULT_TIMEOUT;
        if (cl.getOptionValue("timeout") != null) {
            timeout = cl.getOptionValue("timeout");
        }
        Properties props = new Properties();
        props.setProperty("user", username);
        props.setProperty("password", password);
        props.setProperty("timeout", timeout);
        String url = "jdbc:postgresql://" + hostname + ":" + port + "/" + database;
        DriverManager.registerDriver((Driver) Class.forName("org.postgresql.Driver").newInstance());
        Connection conn = DriverManager.getConnection(url, props);
        return conn;

    }

    /**
     * Closes the connection.
     *
     * @param conn
     *            The connectiont o be closed
     */
    private void closeConnection(final Connection conn) {
        try {
            if (conn != null) {
                conn.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected String getPluginName() {
        return "CHECK_PGSQL";
    }
}
