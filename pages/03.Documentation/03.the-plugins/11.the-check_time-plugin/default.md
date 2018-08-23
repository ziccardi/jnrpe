---
title: 'The CHECK_TIME plugin'
---

Checks the time on a specified host.

Supported parameters are :

* **--hostname/-H [HOSTNAME]** Host name or IP Address.
* **--PORT/-p [PORT]** Port number (default is 37)
* **--warning-variance/-w [THRESHOLD]** Time difference (sec.) necessary to result in a warning status
* **--critical-variance/-c [THRESHOLD]** Time difference (sec.) necessary to result in a critical status
* **--warning-connect/-W [THRESHOLD]** Return warning if elapsed time exceeds value. Default off.
* **--critical-variance/-C [THRESHOLD]** Return critical if elapsed time exceeds value. Default off.
* **--timeout/-t [TIMEOUT]** Seconds before connection times out (default: 10)

## Usage Example
Create a configuration to check the time server

[ui-tabs position="top-left" active="0" theme="badges"]
[ui-tab title="INI"]
```ini
  check_time :  CHECK_TIME --warning-variance $ARG1$ --critical-variance $ARG2$ --hostname $ARG3$
```
[/ui-tab]
[ui-tab title="XML"]
<command name="check_time" plugin_name="CHECK_TIME">
   <arg name="warning-variance" value="$ARG1$" />  
   <arg name="critical-variance" value="$ARG2$" />  
   <arg name="hostname" value="$ARG3$" />  
</command>
[/ui-tab]
[/ui-tabs]

**Invocation**
The following invocation will raise alerts if the time difference is over 5 seconds or 10 seconds
```bash
$ check_nrpe -n -H my.jnrpe.server -c check_time -a 5: 10: nist1-ny.ustiming.org
```