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
import it.jnrpe.ReturnValue;
import it.jnrpe.Status;
import it.jnrpe.plugin.utils.ShellUtils;
import it.jnrpe.plugins.PluginBase;
import it.jnrpe.plugins.annotations.Option;
import it.jnrpe.plugins.annotations.Plugin;
import it.jnrpe.plugins.annotations.PluginOptions;
import it.jnrpe.utils.BadThresholdException;
import it.jnrpe.utils.ThresholdUtil;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Pattern;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;

/**
 * Checks system processes and does check against threshold metrics.
 * 
 * @author Frederico Campos
 * 
 */
@Plugin(name = "CHECK_PROCS", description = "Checks system processes and does check against metrics. Default metrics is number of processes.")
@PluginOptions({

		@Option(shortName = "w", longName = "warning", description = "Warning value if metric is out of range", required = false, hasArgs = true, argName = "warning", optionalArgs = false, option = "warning"),
		@Option(shortName = "c", longName = "critical", description = "Critical value if metric is out of range", required = false, hasArgs = true, argName = "critical", optionalArgs = false, option = "critical"),

		@Option(shortName = "m", longName = "metric", description = "Metric type. Valid values are: PROCS - number of processes"
				+ "; VSZ - virtual memory size (unix only); RSS - resident set memory size (unix only); MEM - memory usage in KB (Windows only); CPU - CPU percentage; "
				+ "ELAPSED - elapsed time in seconds (unix only)", required = false, hasArgs = true, argName = "metric", optionalArgs = false, option = "metric"),

		@Option(shortName = "a", longName = "argument-array", description = "Only scan for processes with args that contain STRING. (unix only). Use instead of ereg-argument-array.", required = false, hasArgs = true, argName = "argument-array", optionalArgs = false, option = "argument-array"),
		@Option(shortName = "e", longName = "ereg-argument-array", description = "Only scan for processes with args that contain the regex. (unix only). Use instead of argument-array.", required = false, hasArgs = true, argName = "ereg-argument-array", optionalArgs = false, option = "ereg-argument-array"),

		@Option(shortName = "p", longName = "ppid", description = "Only scan for children of the parent process ID indicated (unix only).", required = false, hasArgs = true, argName = "ppid", optionalArgs = false, option = "ppid"),

		@Option(shortName = "z", longName = "vsz", description = "Only scan for processes with VSZ higher than indicated (unix only).", required = false, hasArgs = true, argName = "vsz", optionalArgs = false, option = "vsz"),
		@Option(shortName = "r", longName = "rss", description = "Only scan for processes with RSS higher than indicated (unix only).", required = false, hasArgs = true, argName = "rss", optionalArgs = false, option = "rss"),
		@Option(shortName = "M", longName = "memory", description = "Only scan for processes with memory usage higher than indicated (windows only).", required = false, hasArgs = true, argName = "memory", optionalArgs = false, option = "memory"),

		@Option(shortName = "C", longName = "command", description = "Only scan for exact matches of COMMAND (without path).", required = false, hasArgs = true, argName = "command", optionalArgs = false, option = "command"),
		@Option(shortName = "u", longName = "user", description = "Only scan for exact matches of USER", required = false, hasArgs = true, argName = "user", optionalArgs = false, option = "user") })
public class CheckProcs extends PluginBase {

	private final static int SECONDS_IN_MINUTE = 60;
	private final static int SECONDS_IN_HOUR = SECONDS_IN_MINUTE * 60;
	private final static int SECONDS_IN_DAY = SECONDS_IN_HOUR * 24;

	private final static String[] DEFAULT_WINDOWS_CMD = new String[] { "cmd",
			"/C", "tasklist /FO CSV /V" };

	private final static String[] DEFAULT_UNIX_CMD = new String[] { "/bin/ps",
			"-eo", "comm,pid,ppid,user,c,rss,vsz,time,args" };

	private final static String METRIC_PROCS = "PROCS";

	private final static String METRIC_RSS = "RSS";

	private final static String METRIC_VSZ = "VSZ";

	private final static String METRIC_CPU = "CPU";

	private final static String METRIC_ELAPSED = "ELAPSED";

	private final static String METRIC_MEMORY = "MEMORY";

	private final static String FILTER_COMMAND = "command";

	private final static String FILTER_PPID = "ppid";

	private final static String FILTER_VSZ = "vsz";

	private final static String FILTER_RSS = "rss";

	private final static String FILTER_USER = "user";

	private final static String FILTER_ARG_ARRAY = "argument-array";

	private final static String FILTER_EREG_ARG_ARRAY = "ereg-argument-array";

	private final static String FILTER_MEMORY = "memory";

	private final static String[] FILTERS = new String[] { FILTER_COMMAND,
			FILTER_PPID, FILTER_VSZ, FILTER_RSS, FILTER_USER, FILTER_ARG_ARRAY,
			FILTER_EREG_ARG_ARRAY };

	private final static String[] UNIX_ONLY = new String[] { FILTER_ARG_ARRAY,
			FILTER_RSS, FILTER_PPID, FILTER_VSZ, FILTER_EREG_ARG_ARRAY,

	};

	// private final static String UNIX_TMP_FILE = "/tmp/checkprocs.out";

	@Override
	protected String getPluginName() {
		return "CHECK_PROCS";
	}

	/**
	 * Execute the plugin
	 * 
	 * @param cl
	 * @return
	 * @throws BadThresholdException
	 */
	@Override
	public ReturnValue execute(final ICommandLine cl)
			throws BadThresholdException {
		boolean windows = ShellUtils.isWindows();
		try {
			String metric = cl.getOptionValue("metric");
			if (metric == null) {
				metric = METRIC_PROCS;
			}
			metric = metric.toUpperCase();

			validateArguments(cl, windows, metric);

			String output = exec(!windows);

			List<Map<String, String>> result = windows ? parseWindowsOutput(output)
					: parseUnixOutput(output);
			return analyze(result, cl, metric);
		} catch (Exception e) {
			e.printStackTrace();
			throw new BadThresholdException(e);
		}
	}

	/**
	 * Checks command line arguments for operating system specific filters and
	 * metrics
	 * 
	 * @param output
	 * @param cl
	 * @param metric
	 * @return
	 */
	private void validateArguments(ICommandLine cl, boolean windows,
			String metric) throws Exception {
		if (windows) {
			if (metric.equals(METRIC_VSZ) || metric.equals(METRIC_RSS)
					|| metric.endsWith(METRIC_ELAPSED)) {
				throw new Exception("Metric " + metric
						+ " not supported in Wndows.");
			} else {
				for (String opt : UNIX_ONLY) {
					if (cl.getOptionValue("opt") != null) {
						throw new Exception("Option " + opt
								+ " is not supported in Windows.");
					}
				}
			}
		} else {
			if (metric.equals(METRIC_MEMORY)) {
				throw new Exception("Metric " + metric
						+ " not supported in unix.");
			}
		}
	}

	/**
	 * Analyze output and gather metrics
	 * 
	 * @param output
	 * @param cl
	 * @param metric
	 * @return
	 */
	private ReturnValue analyze(List<Map<String, String>> output,
			ICommandLine cl, String metric) {
		Map<String, String> filterAndValue = getFilterAndValue(cl);
		output = applyFilters(output, filterAndValue);
		String message = getMessage(filterAndValue);
		String critical = cl.getOptionValue("critical");
		String warning = cl.getOptionValue("warning");

		ReturnValue retVal = null;
		try {
			if (metric.equals(METRIC_PROCS)) {
				retVal = analyzeProcMetrics(output, cl, critical, warning,
						message);
			} else {
				retVal = analyzeMetrics(output, cl, critical, warning, message,
						metric);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return retVal;
	}

	private ReturnValue analyzeProcMetrics(List<Map<String, String>> output,
			ICommandLine cl, String critical, String warning, String message)
			throws Exception {
		int size = output.size();
		if (critical != null) {
			if (ThresholdUtil.isValueInRange(critical, new BigDecimal(size))) {
				return new ReturnValue(Status.CRITICAL, "PROCS CRITICAL: "
						+ message + " " + size + " processes.");
			}
		}
		if (warning != null) {
			if (ThresholdUtil.isValueInRange(warning, new BigDecimal(size))) {
				return new ReturnValue(Status.WARNING, "PROCS WARNING: "
						+ message + " " + size + " processes.");
			}
		}
		return new ReturnValue(Status.OK, "PROCS OK: " + message + " " + size
				+ " processes.");
	}

	/**
	 * Analyze process cpu thresholds
	 * 
	 * @param output
	 * @param cl
	 * @param critical
	 * @param warning
	 * @param message
	 * @return
	 * @throws Exception
	 */
	private ReturnValue analyzeMetrics(List<Map<String, String>> output,
			ICommandLine cl, String critical, String warning, String message,
			String metric) throws Exception {

		if (critical != null) {
			int checkCritical = compareMetric(output, critical,
					metric.toUpperCase());
			if (checkCritical > 0) {
				return new ReturnValue(Status.CRITICAL, metric.toUpperCase()
						+ " CRITCAL: " + message + " "
						+ (output.size() - checkCritical) + " critical out of "
						+ output.size() + " processes.");
			}
		}
		if (warning != null) {
			int checkWarning = compareMetric(output, warning, metric);
			if (checkWarning > 0) {
				return new ReturnValue(Status.WARNING, metric.toUpperCase()
						+ " WARNING: " + message + " "
						+ (output.size() - checkWarning) + " warning out of "
						+ output.size() + " processes.");
			}
		}
		return new ReturnValue(Status.OK, metric.toUpperCase() + " OK: "
				+ message + " " + output.size() + " processes.");
	}

	@SuppressWarnings("deprecation")
	private int compareMetric(List<Map<String, String>> output, String value,
			String metric) throws BadThresholdException {
		List<Map<String, String>> list = new ArrayList<Map<String, String>>();
		for (Map<String, String> values : output) {
			int procValue = Integer.parseInt(values.get(metric.toLowerCase()));
			if (ThresholdUtil.isValueInRange(value, procValue)) {
				list.add(values);
			}
		}
		return list.size();
	}

	/**
	 * Get parameter list in return message
	 * 
	 * @param filterAndValue
	 * @return
	 */
	private String getMessage(Map<String, String> filterAndValue) {

		String msgString = "";
		if (!filterAndValue.isEmpty()) {
			StringBuilder msg = new StringBuilder();
			msg.append("with ");

			for (Entry<String, String> entry : filterAndValue.entrySet()) {
				msg.append(entry.getKey()).append(" = ")
						.append(entry.getValue()).append(", ");
			}

			msgString = msg.toString().trim();
			if (msgString.endsWith(", ")) {
				msgString = msgString.substring(0, msgString.length() - 2);
			}
		}
		return msgString;
	}

	/**
	 * Execute a system command and return the output.
	 * 
	 * @param command
	 * @return String
	 */
	private String exec(boolean unix) throws Exception {
		String output = null;
		InputStream input = null;
		String[] command = null;
		if (unix) {
			// write output to tmp file
			command = DEFAULT_UNIX_CMD;
			Process p = Runtime.getRuntime().exec(command);
			input = p.getInputStream();

			ByteArrayOutputStream bout = new ByteArrayOutputStream();
			IOUtils.copy(input, bout);
			output = getFormattedOutput(new String(bout.toByteArray(), "UTF-8"));
			input.close();

		} else {
			command = DEFAULT_WINDOWS_CMD;
			output = ShellUtils.executeSystemCommandAndGetOutput(command,
					"CP437");
		}

		return output;
	}

	/**
	 * Comma separate the command output
	 */
	private String getFormattedOutput(String output) {
		String out = "";
		StringBuffer lines = new StringBuffer();
		String[] splittedLines = output.split("\n");
		for (int i = 0; i < splittedLines.length; i++) {
			if (i == 0) {
				continue;
			}
			String splittedLine = splittedLines[i];
			if (splittedLine.contains("<defunct>")) {
				continue;
			}
			String[] splitted = splittedLine.split("\\s+", 9);
			String line = "";
			for (String str : splitted) {
				line += str + ",";
			}
			if (line.endsWith(",")) {
				line = line.substring(0, line.length() - 1);
			}
			lines.append(line).append("\n");
		}
		out = lines.toString();
		return out;
	}

	/**
	 * Parse command output for windows environment
	 * 
	 * @param output
	 * @return List<Map<String,String>>
	 */

	private List<Map<String, String>> parseWindowsOutput(String output) {
		List<Map<String, String>> info = new ArrayList<Map<String, String>>();
		String[] lines = output.split("\n");

		int totalRunTime = 0;
		String cpu = METRIC_CPU.toLowerCase();
		int count = 0;
		for (String l : lines) {
			if (count == 0) {
				count++;
				continue;
			}

			Map<String, String> values = new HashMap<String, String>();
			String[] line = l.replaceAll("\"", "").split(",");
			values.put(FILTER_COMMAND, line[0]);
			values.put("pid", line[1]);
			values.put(FILTER_MEMORY, "" + convertToMemoryInt(line[4]));
			values.put(FILTER_USER, line[6]);
			int seconds = 0;
			if (!ShellUtils.isWindowsIdleProc(line[0])){
				seconds = convertToSeconds(line[7].trim()); 
			}
			totalRunTime += seconds;
			values.put(cpu, seconds + "");
			info.add(values);
		}
		
		for (Map<String, String> map : info) {
			int secs = Integer.parseInt(map.get(cpu));
			log.debug("secs " + secs + "");
			double perc = ((double) secs / (double) totalRunTime) * 100;
			map.put(cpu, (int) perc + "");
		}

		log.debug(info + "");
		return info;
	}

	/**
	 * Parse command output for unix environment
	 * 
	 * @param output
	 * @return List<Map<String,String>>
	 */
	private List<Map<String, String>> parseUnixOutput(String output) {
		List<Map<String, String>> info = new ArrayList<Map<String, String>>();
		output = output.replaceAll("\"", "");
		String[] lines = output.split("\n");
		for (String l : lines) {
			if (l.startsWith("PID")) {
				continue;
			}

			String[] line = l.split(",", 9);

			// FIXME : the ps command itself should be skipped
			// if (line[8].contains(DEFAULT_UNIX_CMD[2])) {
			// // continue;
			// }

			Map<String, String> values = new HashMap<String, String>();
			values.put(FILTER_COMMAND, line[0].trim());
			values.put("pid", line[1].trim());
			values.put("ppid", line[2].trim());
			values.put(FILTER_USER, line[3].trim());
			values.put(METRIC_CPU.toLowerCase(), line[4].trim());
			values.put(METRIC_RSS.toLowerCase(), line[5].trim());
			values.put(METRIC_VSZ.toLowerCase(), line[6].trim());
			values.put(METRIC_ELAPSED.toLowerCase(),
					convertToSeconds(line[7].trim()) + "");
			values.put(FILTER_ARG_ARRAY, line[8]);

			info.add(values);
		}
		return info;
	}

	private int convertToMemoryInt(String mem) {
		mem = mem.replaceAll("[^0-9]", "");

		return Integer.parseInt(mem);
	}

	/**
	 * Apply filters to processes output
	 * 
	 * @param values
	 * @param filterAndValue
	 * @return
	 */
	private List<Map<String, String>> applyFilters(
			List<Map<String, String>> values, Map<String, String> filterAndValue) {
		if (filterAndValue == null || filterAndValue.size() == 0) {
			return values;
		}
		List<Map<String, String>> filtered = new ArrayList<Map<String, String>>();
		for (Map<String, String> map : values) {
			boolean matchesAll = true;
			for (Entry<String, String> entry : filterAndValue.entrySet()) {
				String filter = entry.getKey();
				String filterValue = entry.getValue();
				if (filter.contains(FILTER_COMMAND)
						|| filter.contains(FILTER_USER)
						|| filter.equals(FILTER_ARG_ARRAY)
						|| filter.contains(FILTER_PPID)) {
					if (!map.get(filter).contains(filterValue)) {
						matchesAll = false;
						break;
					}
				} else if (filter.contains(FILTER_EREG_ARG_ARRAY)
						&& !patternMatches(filterValue,
								map.get(FILTER_ARG_ARRAY))) {
					matchesAll = false;
					break;
				} else if (filter.contains(FILTER_VSZ)
						|| filter.contains(FILTER_RSS)
						|| filter.contains(FILTER_MEMORY)) {
					int filterval = Integer.parseInt(filterValue);
					int value = Integer.parseInt(map.get(filter));
					if (value < filterval) {
						matchesAll = false;
						break;
					}
				}
			}
			if (matchesAll) {
				filtered.add(map);
			}
		}
		return filtered;
	}

	/**
	 * Apply regex
	 * 
	 * @param regex
	 * @param input
	 * @return
	 */
	private boolean patternMatches(String regex, String input) {
		// Pattern p = Pattern.compile(regex);
		return Pattern.matches(regex, input);
	}

	private Map<String, String> getFilterAndValue(ICommandLine cl) {
		Map<String, String> map = new HashMap<String, String>();
		for (String filter : FILTERS) {
			if (cl.getOptionValue(filter) != null) {
				map.put(filter, cl.getOptionValue(filter));
			}
		}
		return map;
	}

	/**
	 * Convert date in format DD-HH:MM:SS to seconds.
	 * 
	 * @param input
	 * @return
	 */
	private int convertToSeconds(String input) {
		int days = 0;
		int hours = 0;
		int minutes = 0;
		int seconds = 0;
		int hyphenCount = StringUtils.countMatches(input, "-");
		int colonCount = StringUtils.countMatches(input, ":");

		if (hyphenCount > 0) {
			int hyphenIndex = input.indexOf("-");
			days = Integer.parseInt(input.substring(0, hyphenIndex));
			String[] time = input.substring(hyphenIndex + 1).split(":");
			hours = Integer.parseInt(time[0]);
			minutes = Integer.parseInt(time[1]);
			seconds = Integer.parseInt(time[2]);
		} else {
			String[] split = input.split(":");
			if (colonCount == 2) {
				hours = Integer.parseInt(split[0]);
				minutes = Integer.parseInt(split[1]);
				seconds = Integer.parseInt(split[2]);
			} else if (colonCount == 1) {
				minutes = Integer.parseInt(split[0]);
				seconds = Integer.parseInt(split[1]);
			}
		}

		return (days * SECONDS_IN_DAY) + (hours * SECONDS_IN_HOUR)
				+ (minutes * SECONDS_IN_MINUTE) + seconds;
	}
}