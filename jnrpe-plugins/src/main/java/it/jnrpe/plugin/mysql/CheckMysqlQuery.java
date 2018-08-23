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
package it.jnrpe.plugin.mysql;

import it.jnrpe.ICommandLine;
import it.jnrpe.Status;
import it.jnrpe.plugin.utils.DBUtils;
import it.jnrpe.plugins.Metric;
import it.jnrpe.plugins.MetricGatheringException;
import it.jnrpe.plugins.PluginBase;
import it.jnrpe.utils.BadThresholdException;
import it.jnrpe.utils.thresholds.ThresholdsEvaluatorBuilder;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
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
    public final void configureThresholdEvaluatorBuilder(final ThresholdsEvaluatorBuilder thrb, final ICommandLine cl) throws BadThresholdException {

        if (cl.hasOption("th")) {
            super.configureThresholdEvaluatorBuilder(thrb, cl);
        } else {
            thrb.withLegacyThreshold("rows", null, cl.getOptionValue("warning"), cl.getOptionValue("critical"));
        }

    }

    /**
     * Execute and gather metrics.
     * 
     * @param cl
     *            the command line
     * @throws MetricGatheringException
     *             on any error gathering metrics
     * @return the metrics
     */
    public final Collection<Metric> gatherMetrics(final ICommandLine cl) throws MetricGatheringException {
        LOG.debug(getContext(), "check_mysql_query gather metrics");
        List<Metric> metrics = new ArrayList<Metric>();
        Mysql mysql = new Mysql(cl);
        Connection conn = null;
        try {
            conn = mysql.getConnection();
        } catch (ClassNotFoundException e) {
            LOG.error(getContext(), "Mysql driver library not found into the classpath" + ": download and put it in the same directory " + "of this plugin");
            throw new MetricGatheringException("CHECK_MYSQL_QUERY - CRITICAL: Error accessing the " + "MySQL server - JDBC driver not installed",
                    Status.CRITICAL, e);
        } catch (Exception e) {
            LOG.error(getContext(), "Error accessing the MySQL server", e);
            throw new MetricGatheringException("CHECK_MYSQL_QUERY - CRITICAL: Error accessing " + "the MySQL server - " + e.getMessage(),
                    Status.CRITICAL, e);
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

            metrics.add(new Metric("rows", "CHECK_MYSQL_QUERY - Returned value is " + (value != null ? value.longValue() : null), value, null, null));

        } catch (SQLException e) {
            
            String message = e.getMessage();
            LOG.warn(getContext(), "Error executing plugin CheckMysqlQuery : " + message, e);
            throw new MetricGatheringException("CHECK_MYSQL_QUERY - CRITICAL: " + message, Status.CRITICAL, e);
        } finally {
            DBUtils.closeQuietly(set);
            DBUtils.closeQuietly(st);
            DBUtils.closeQuietly(conn);
        }

        return metrics;
    }

    @Override
    protected final String getPluginName() {
        return "CHECK_MYSQL_QUERY";
    }
}
