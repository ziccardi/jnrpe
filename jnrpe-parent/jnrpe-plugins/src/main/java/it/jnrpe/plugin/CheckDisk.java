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
import it.jnrpe.plugins.Metric;
import it.jnrpe.plugins.MetricGatheringException;
import it.jnrpe.plugins.PluginBase;
import it.jnrpe.utils.BadThresholdException;
import it.jnrpe.utils.thresholds.ThresholdsEvaluatorBuilder;

import java.io.File;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Checks the disk space.
 *
 * @author Massimiliano Ziccardi
 *
 */
public class CheckDisk extends PluginBase {

    /**
     * Number of bytes in a Kilobyte.
     */
    private static final long KB = 1024;

    /**
     * Number of kilobytes in a Megabyte.
     */
    private static final long MB = KB << 10;

    /**
     * Compute the percent values.
     *
     * @param val
     *            The value to be represented in percent
     * @param total
     *            The total value
     * @return The percent of val/total
     */
    private int percent(final long val, final long total) {
        if (total == 0) {
            return 100;
        }

        if (val == 0) {
            return 0;
        }

        double dVal = (double) val;
        double dTotal = (double) total;

        return (int) (dVal / dTotal * 100);
    }

    @Override
    public final void configureThresholdEvaluatorBuilder(
            final ThresholdsEvaluatorBuilder thrb,
            final ICommandLine cl)
            throws BadThresholdException {
        if (cl.hasOption("th")) {
            super.configureThresholdEvaluatorBuilder(thrb, cl);
        } else {
            thrb.withLegacyThreshold("freepct", null,
                    cl.getOptionValue("warning"),
                    cl.getOptionValue("critical"));
        }
    }

    @Override
    public final Collection<Metric> gatherMetrics(final ICommandLine cl)
            throws MetricGatheringException {
        String sPath = cl.getOptionValue("path");

        File f = new File(sPath);

        long lBytes = f.getFreeSpace();
        long lTotalSpace = f.getTotalSpace();

        String sFreeSpace = format(lBytes);
        String sUsedSpace = format(lTotalSpace - lBytes);

        int iFreePercent = percent(lBytes, lTotalSpace);

        String sFreePercent = "" + iFreePercent + "%";
        String sUsedPercent =
                "" + percent(lTotalSpace - lBytes, lTotalSpace) + "%";

        List<Metric> res = new ArrayList<Metric>();

        String msg = "Used: " + sUsedSpace + "("
                + sUsedPercent + ") Free: " + sFreeSpace + "("
                + sFreePercent + ")";

        res.add(new Metric("freepct", msg, new BigDecimal(iFreePercent),
                new BigDecimal(0), new BigDecimal(100)));
        res.add(new Metric("freespace", msg, new BigDecimal(iFreePercent),
                new BigDecimal(0), new BigDecimal(lTotalSpace)));

        return res;
    }

    /**
     * Format the size returning it as MB or KB.
     *
     * @param bytes
     *            The size to be formatted
     * @return The formatted size
     */
    private String format(final long bytes) {
        if (bytes > MB) {
            return "" + (bytes / MB) + " MB";
        }
        return "" + (bytes / KB) + " KB";
    }

    @Override
    protected String getPluginName() {
        return "CHECK_DISK";
    }

}
