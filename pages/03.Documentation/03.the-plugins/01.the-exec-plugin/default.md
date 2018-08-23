---
title: 'The EXEC Plugin'
---

The EXEC plugin can be used when your check is implemented with an external executable (for example when you want to execute an already existing Nagios native plugin).

Its use is very simple and is composed of just two parameters:

* `-e/--executable`: The full path to the command to execute 
* `-a/--args`: The arguments to pass to the executable

## Usage example
The following example shows how to monitor the disk usage using the native 'check_disk' plugin (for details about the 'check_disk' plugin, please give a look at its man page).

**Adding the command to the configuration file**

[ui-tabs position="top-left" active="0" theme="badges"]
[ui-tab title="INI"]
```
CHECK_DISK : exec --executable /path/to/check_disk --args "-w $ARG1$ -c $ARG2$ -p /tmp -p /var -C -w $ARG3$ -c $ARG4$ -p /"
```
[/ui-tab]
[ui-tab title="XML"]
```xml
<command name="CHECK_DISK" plugin_name="EXEC">
   <arg name="executable" value="/path/to/check_disk" />  
   <arg name="args" value="-w $ARG1$ -c $ARG2$ -p /tmp -p /var -C -w $ARG3$ -c $ARG4$ -p /" /> 
</command>
```
[/ui-tab]
[ui-tab title="JSon"]
```json
"CHECK_DISK" : { "plugin": "CHECK_NATIVE", "args":["--command", "/path/to/check_disk", "--args", "-w $ARG1$ -c $ARG2$ -p /tmp -p /var -C -w $ARG3$ -c $ARG4$ -p /"] }
```
!!! Json configuration file is available only for the [JNRPE JavaScript version](https://www.npmjs.com/package/jnrpe).
[/ui-tab]
[/ui-tabs]

**Invoking the command**

With the following invocation example we want to monitor /tmp and /var at 10% and 5%, and / at 100MB and 50MB.

```
check_nrpe -n -H {JNRPE_SERVER_IP} -c CHECK_DISK -a  10% 5% 100000 50000
```

Of course, you must replace `{JNRPE_SERVER_IP}` with the IP address of your JNRPE server.`$ARG1$` will be replaced by 10%, `$ARG2$` will be replaced by 5%, `$ARG3$` will be replaced by '100000' and `$ARG4$` will be replaced by '50000'.  