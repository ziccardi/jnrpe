---
id: plugin-checkprocs
title: CHECK_PROCS
---

Checks system processes and does check against metrics. Default metrics is number of processes.

Supported parameters are :

* **--metric/-m <METRIC>** Metric type. Valid values are: 
  * PROCS - number of processes
  * VSZ - virtual memory size (unix only)
  * RSS - resident set memory size (unix only)
  * MEM - memory usage in KB (Windows only)
  * CPU - CPU percentage
  * ELAPSED - elapsed time in seconds (unix only)
* **--warning/-w <THRESHOLD>** warning threshold pair.
* **--critical/-c <THRESHOLD>** Critical value if metric is out of range.
* **--argument-array/-a <STRING>** Only scan for processes with args that contain STRING (unix only). Use instead of ereg-argument-array.
* **--ereg-argument-array <STRING>** Only scan for processes with args that contain the regex (unix only). Use instead of ereg-argument-array.
* **--command/-C <STRING>** Only scan for exact matches of COMMAND (without path).
* **--ppid/-p <PPID>** Only scan for children of the parent process ID indicated (unix only).
* **--vsz/-v <VSZ>** Only scan for processes with VSZ (Virtual Memory Size)s higher than indicated (unix only).
* **--rss/-r <RSS>** Only scan for processes with RSS (Resident Set Size) higher than indicated (unix only).
* **--memory/-M <MEMORY>** Only scan for Windows processes with memory usage higher than indicated (Windows only).

## Example Usage

### Configuration

<!--DOCUSAURUS_CODE_TABS-->
<!-- INI -->
```bash
check_procs :  CHECK_PROCS --warning $ARG1$ --critical $ARG2$ --metric $ARG3$
```
<!-- XML -->
```xml
<command name="check_procs" plugin_name="CHECK_PROCS">
   <arg name="warning" value="$ARG1$" />  
   <arg name="critical" value="$ARG2$" />  
   <arg name="metric" value="$ARG3$" />  
</command>
```
<!--END_DOCUSAURUS_CODE_TABS-->

### Invocation
The following invocation will raise alerts if VSZ of any processes over 50K or 100K

```bash
check_nrpe -n -H my.jnrpe.server -c check_procs -a 50000: 100000: VSZ
```