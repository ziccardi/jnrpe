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

    public CheckDisk() {
        super("DISK");
    }

    /**
     * Compute the percent values.
     *
     * @param val
     *            The value to be represented in percent
     * @param total
     *            The total value
     * @return The percent of val/total
     */
    private double percent(final long val, final long total) {
        if (total == 0) {
            return 100;
        }

        if (val == 0) {
            return 0;
        }

        double dVal = (double) val;
        double dTotal = (double) total;

        return dVal / dTotal * 100;
    }

    @Override
    public final void configureThresholdEvaluatorBuilder(final ThresholdsEvaluatorBuilder thrb, final ICommandLine cl) throws BadThresholdException {
        if (cl.hasOption("th")) {
            super.configureThresholdEvaluatorBuilder(thrb, cl);
        } else {
            thrb.withLegacyThreshold(cl.getOptionValue("path"), null, cl.getOptionValue("warning"), cl.getOptionValue("critical"));
        }
    }

    private long getFreeSpace(final String path) {
        File f = new File(path);
        return f.getFreeSpace();
    }

    private long getTotalSpace(final String path) {
        File f = new File(path);
        return f.getTotalSpace();
    }

    @Override
    public final Collection<Metric> gatherMetrics(final ICommandLine cl) {
        String sPath = cl.getOptionValue("path");

        long lBytes = getFreeSpace(sPath);
        long lTotalSpace = getTotalSpace(sPath);

        ReadableSize freeSpace = format(lBytes);
        ReadableSize readableUsedSpace = format(lTotalSpace - lBytes);
        ReadableSize readableTotalSpace = format(lTotalSpace);
        double freePercent = percent(lBytes, lTotalSpace);

        List<Metric> res = new ArrayList<>();

        String msg = String.format("free space: %s %.0f %s (%.2f%%)", sPath, Math.floor(freeSpace.size), freeSpace.UOM, freePercent);
        res.add(Metric
                .forMetric(sPath, Long.class)
                .withMessage(msg)
                .withValue(lBytes)
                .withOutputMetric((long) readableUsedSpace.size, readableUsedSpace.UOM)
                .withMinValue(0L)
                .withMaxValue((long)readableTotalSpace.size)
                .build());

        res.add(Metric
                .forMetric("freepct", Integer.class)
                .withMessage(msg)
                .withValue((int) percent(lBytes, lTotalSpace))
                .withMinValue(0)
                .withMaxValue(100)
                .build());
        return res;
    }

    private static class ReadableSize {
        private double size;
        private String UOM;

        private ReadableSize(double size, String uom) {
            this.size = size;
            this.UOM = uom;
        }
    }

    public static ReadableSize format(long size) {
        if(size <= 0) return new ReadableSize(0, "B");
        final String[] units = new String[] { "B", "kB", "MB", "GB", "TB" };
        int digitGroups = (int) (Math.log10(size)/Math.log10(1024));

        return new ReadableSize(size/Math.pow(1024, digitGroups), units[digitGroups]);
    }


    @Override
    protected String getPluginName() {
        return "CHECK_DISK";
    }

}
