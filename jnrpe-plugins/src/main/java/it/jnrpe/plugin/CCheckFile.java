/*
 * Copyright (c) 2008 Massimiliano Ziccardi
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
package it.jnrpe.plugin;

import it.jnrpe.ICommandLine;
import it.jnrpe.ReturnValue;
import it.jnrpe.Status;
import it.jnrpe.events.LogEvent;
import it.jnrpe.plugins.PluginBase;
import it.jnrpe.plugins.annotations.Option;
import it.jnrpe.plugins.annotations.Plugin;
import it.jnrpe.plugins.annotations.PluginOptions;
import it.jnrpe.utils.BadThresholdException;
import it.jnrpe.utils.StreamManager;
import it.jnrpe.utils.ThresholdUtil;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.math.BigDecimal;

/**
 * Performs the following checks:
 * <ul>
 * <li>Checks that a file do not exist (param -F)
 * <li>Checks that a file exist (param -f)
 * <li>Checks file age (params -w and -c). Requires param -f
 * <li>Checks file size (params -W and -C). Requires param -f
 * <li>Checks how many time a string is repeatend inside a file (params -O).
 * Requires param -f. The string cannot contain ','. Optionally, you can specify
 * WARNING and CRITICAL range. To specify the ranges, use a syntax like:
 * <blockquote> -f path/to/your/file -O *YOURSTRING*,*WARNING*,*CRITICAL*
 * </blockquote> If you do not specify the ranges, it's the same as:
 * <blockquote> -f path/to/your/file -O *YOURSTRING*,:0,:0 </blockquote> This
 * means that a CRITICAL state will be raised if the string is not present.
 * <B>Be careful! The whole file will be read. This can be quite slow with very
 * large files</B>
 * <li>Check that a file do not contains a string (params -N). Strings cannot
 * contains ','. Strings are separated by comma. Requires param -f
 * </ul>
 *
 * The params -w, -c, -W and -C requires as argument a range in the standard
 * nagios format.
 *
 * @author Massimiliano Ziccardi
 */
@Plugin(
        name = "CHECK_FILE",
        description = "This plugin is used to perform various check against files:\n\n" +
        "  * checks that a file exists (-f)\n" +
        "  * checks that a file does not exists (-F)\n" +
        "  * check file age (requires -f)\n" +
        "  * check file size (requires -f)\n" +
        "  * check how many lines of a file contains the given string. You can specify the warning and the critical range. (requires -f)\n" +
        "     EXAMPLE: -f /path/to/your/file --contains MyString,0:10,11:\n" +
        "  * check that a string is not inside the file (requires -f)\n"
                      )
@PluginOptions({
    @Option(shortName="F", longName="FILE", description="The path of the file the must not exist",
            required=false, hasArgs=true, argName="path", optionalArgs=false, option="FILE"),
    @Option(shortName="f", longName="file", description="The path to the file to check",
            required=false, hasArgs=true, argName="path", optionalArgs=false, option="file"),
    @Option(shortName="w", longName="warning", description="The max age (in seconds) before a warning is raised",
            required=false, hasArgs=true, argName="age threshold", optionalArgs=false, option="warning"),
    @Option(shortName="c", longName="critical", description="The max age (in seconds) before a critical is raisedk",
            required=false, hasArgs=true, argName="age threshold", optionalArgs=false, option="critical"),
    @Option(shortName="W", longName="sizewarning", description="The min file size (in bytes) before a warning is raised",
            required=false, hasArgs=true, argName="size threshold", optionalArgs=false, option="sizewarning"),
    @Option(shortName="C", longName="sizecritical", description="The min file size (in bytes) before a critical is raised",
            required=false, hasArgs=true, argName="size threshold", optionalArgs=false, option="sizecritical"),
    @Option(shortName="O", longName="contains", 
        description="The string that must be found inside the file in the format STRING,WARNING_RANGE,CRITICAL_RANGE.",
            required=false, hasArgs=true, argName="string to check", optionalArgs=false, option="contains"),
    @Option(shortName="N", longName="notcontains", description="The path to the file to check",
            required=false, hasArgs=true, argName="string to check", optionalArgs=false, option="notcontains"),
              })
public class CCheckFile extends PluginBase {

    /**
     * Updates the checkfile plugin result.
     *
     * @param res
     *            The current return value
     * @param newVal
     *            The new return value
     * @return The updated return value
     */
    private ReturnValue updateRes(final ReturnValue res,
            final ReturnValue newVal) {

        if (res == null) {
            return newVal;
        }

        switch (res.getStatus()) {
        case CRITICAL:
            return res;
        case WARNING:
            if (newVal.getStatus() != Status.CRITICAL) {
                return res;
            }
            return newVal;
        case OK:
            if (newVal.getStatus() == Status.OK) {
                return res;
            }
            return newVal;
        default:
            return res;
        }
    }

    /**
     * Checks if the file exists and updates the return value object.
     *
     * @param f
     *            The file to be checked
     * @param res
     *            The return value to be updated
     * @return The updated return value
     */
    private ReturnValue checkFileExists(final File f, final ReturnValue res) {
        if (f.exists()) {
            return updateRes(res, new ReturnValue(Status.OK, "FILE OK"));
        }
        return updateRes(res, new ReturnValue(Status.CRITICAL,
                "FILE CRITICAL: File '" + f.getAbsolutePath()
                        + "' do not exists"));
    }

    /**
     * Checks the file age and updates the return value.
     *
     * @param cl
     *            The passed in command line
     * @param f
     *            The file to be checked
     * @param res
     *            The result value to be updated
     * @return The updated return value
     * @throws BadThresholdException
     *             -
     */
    private ReturnValue checkAge(final ICommandLine cl, final File f,
            final ReturnValue res)
            throws BadThresholdException {
        if (cl.hasOption("critical")) {
            long lLastAccess = f.lastModified();
            long lNow = System.currentTimeMillis();
            BigDecimal lAge =
                    new BigDecimal("" + ((lNow - lLastAccess) / 1000));
            String sCriticalThreshold = cl.getOptionValue("critical");

            if (ThresholdUtil.isValueInRange(sCriticalThreshold, lAge)) {
                return updateRes(res, new ReturnValue(Status.CRITICAL,
                        "FILE CRITICAL - File age : " + lAge + " seconds"));
            }
        }

        if (cl.hasOption("warning")) {
            long lLastAccess = f.lastModified();
            long lNow = System.currentTimeMillis();
            BigDecimal lAge =
                    new BigDecimal("" + ((lNow - lLastAccess) / 1000));
            String sWarningThreshold = cl.getOptionValue("warning");

            if (ThresholdUtil.isValueInRange(sWarningThreshold, lAge)) {
                return updateRes(res, new ReturnValue(Status.WARNING,
                        "FILE WARNING - File age : " + lAge + " seconds"));
            }
        }

        return updateRes(res, new ReturnValue(Status.OK, "FILE OK"));
    }

    /**
     * Checks the file size.
     *
     * @param cl
     *            The passed in command line
     * @param f
     *            The file to be checked
     * @param res
     *            The return value to be updated
     * @return The updated return value
     * @throws BadThresholdException
     *             -
     */
    private ReturnValue checkSize(final ICommandLine cl, final File f,
            final ReturnValue res)
            throws BadThresholdException {
        if (cl.hasOption("sizecritical")) {
            String sCriticalThreshold = cl.getOptionValue("sizecritical");
            BigDecimal bdSize = new BigDecimal("" + f.length());

            if (ThresholdUtil.isValueInRange(sCriticalThreshold, bdSize)) {
                return updateRes(res, new ReturnValue(Status.CRITICAL,
                        "FILE CRITICAL - File size : " + bdSize + " bytes"));
            }
        }

        if (cl.hasOption("sizewarning")) {
            String sWarningThreshold = cl.getOptionValue("sizewarning");
            BigDecimal bdSize = new BigDecimal("" + f.length());

            if (ThresholdUtil.isValueInRange(sWarningThreshold, bdSize)) {
                return updateRes(res, new ReturnValue(Status.WARNING,
                        "FILE WARNING  - File size : " + bdSize + " bytes"));
            }
        }

        return updateRes(res, new ReturnValue(Status.OK, "FILE OK"));
    }

    /**
     * Checks the file content.
     *
     * @param cl
     *            The passed in command line
     * @param f
     *            The file to be checked
     * @param res
     *            The return value to be updated
     * @return The updated return value
     * @throws BadThresholdException
     *             -
     */
    private ReturnValue checkContains(final ICommandLine cl, final File f,
            final ReturnValue res)
            throws BadThresholdException {
        if (!cl.hasOption("contains")) {
            return updateRes(res, new ReturnValue(Status.OK, "FILE OK"));
        }

        StreamManager sm = new StreamManager();

        try {
            BufferedReader r =
                    (BufferedReader) sm.handle(new BufferedReader(
                            new FileReader(f)));
            String sLine = null;

            String sWarningThreshold = ":0";
            String sCriticalThreshold = ":0";

            String sPattern = cl.getOptionValue("contains");
            if (sPattern.indexOf(',') != -1) {
                String[] vsParts = sPattern.split(",");
                sWarningThreshold = vsParts[1];
                if (vsParts.length > 1) {
                    sCriticalThreshold = vsParts[2];
                }
                sPattern = vsParts[0];
            }

            int iCount = 0;

            while ((sLine = r.readLine()) != null) {
                if (sLine.indexOf(sPattern) != -1) {
                    iCount++;
                }
                // return updateRes(res, new ReturnValue(Status.OK, "FILE OK"));
            }

            if (ThresholdUtil.isValueInRange(sCriticalThreshold, iCount)) {
                return updateRes(res, new ReturnValue(Status.CRITICAL,
                        "FILE CRITICAL - String '" + sPattern + "' found "
                                + iCount + " times"));
            }
            if (ThresholdUtil.isValueInRange(sWarningThreshold, iCount)) {
                return updateRes(res, new ReturnValue(Status.WARNING,
                        "FILE WARNING - String '" + sPattern + "' found "
                                + iCount + " times"));
            }

            return updateRes(res, new ReturnValue(Status.OK,
                    "FILE OK - String '" + sPattern + "' found " + iCount
                            + " times"));
        } catch (IOException e) {
            sendEvent(LogEvent.WARNING,
                    "Plugin Execution error : " + e.getMessage(), e);
            return updateRes(res, new ReturnValue(Status.UNKNOWN,
                    "FILE UNKNOWN - " + e.getMessage()));
        } finally {
            sm.closeAll();
        }

    }

    /**
     * Checks that the file do not contains a string.
     *
     * @param cl
     *            The passed in command line
     * @param f
     *            The file to be checked
     * @param res
     *            The result to be updated
     * @return The updated result
     */
    private ReturnValue checkNotContains(final ICommandLine cl, final File f,
            final ReturnValue res) {
        if (!cl.hasOption("notcontains")) {
            return updateRes(res, new ReturnValue(Status.OK, "FILE OK"));
        }

        StreamManager sm = new StreamManager();

        try {
            BufferedReader r =
                    (BufferedReader) sm.handle(new BufferedReader(
                            new FileReader(f)));
            String sLine = null;

            String[] vsPatterns = cl.getOptionValue("notcontains").split(",");

            while ((sLine = r.readLine()) != null) {
                for (int i = 0; i < vsPatterns.length; i++) {
                    if (sLine.indexOf(vsPatterns[i]) != -1) {
                        return updateRes(
                                res,
                                new ReturnValue(
                                        Status.CRITICAL,
                                        "FILE CRITICAL - String '"
                                            + cl.getOptionValue("notcontains")
                                            + "' found"));
                    }
                }
            }
            return updateRes(res, new ReturnValue(Status.OK,
                    "FILE OK: String '" + cl.getOptionValue("notcontains")
                            + "' not found"));
        } catch (IOException e) {
            sendEvent(LogEvent.WARNING,
                    "Plugin Execution error : " + e.getMessage(), e);
            return updateRes(res, new ReturnValue(Status.UNKNOWN,
                    "FILE UNKNOWN - " + e.getMessage()));
        } finally {
            sm.closeAll();
        }

    }

    /**
     * Executes the plugin.
     *
     * @param cl
     *            The command line
     * @return the return value
     * @throws BadThresholdException
     *             -
     */
    public final ReturnValue execute(final ICommandLine cl)
            throws BadThresholdException {
        if (cl.hasOption("FILE")) {
            File f = new File(cl.getOptionValue("FILE"));
            if (f.exists()) {
                return new ReturnValue(Status.CRITICAL, "File '" + f.getName()
                        + "' exists");
            } else {
                return new ReturnValue(Status.OK, "File '" + f.getName()
                        + "' is OK");
            }
        }

        // ReturnValue res = new ReturnValue(Status.OK, "CHECK_FILE: OK");

        File f = null;

        if (cl.hasOption("file")) {
            f = new File(cl.getOptionValue("file"));
        } else {
            return new ReturnValue(Status.UNKNOWN,
                    "Either param -f or -F must be specified");
        }

        // if (!f.exists())
        // return new ReturnValue(Status.CRITICAL, "File '" + f.getName() +
        // "' not found");

        // Check that the file exists...
        ReturnValue res = checkFileExists(f, null);

        if (res == null || res.getStatus() != Status.CRITICAL) {
            res = checkAge(cl, f, res);
        }
        if (res == null || res.getStatus() != Status.CRITICAL) {
            res = checkSize(cl, f, res);
        }
        if (res == null || res.getStatus() != Status.CRITICAL) {
            res = checkContains(cl, f, res);
        }
        if (res == null || res.getStatus() != Status.CRITICAL) {
            res = checkNotContains(cl, f, res);
        }

        return res;
    }

    @Override
    protected String getPluginName() {
        return "CHECK_FILE";
    }
}
