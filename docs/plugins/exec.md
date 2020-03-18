---
id: plugin-exec
title: EXEC
---

The EXEC plugin can be used when your check is implemented with an external executable (for example when you want to execute an already existing Nagios native plugin).

Its use is very simple and is composed of just two parameters:

* **-e/--executable**: The full path to the command to execute
* **-a/--args**: The arguments to pass to the executable

## Example

The following example shows how to monitor the disk usage using the native 'check_disk' plugin (for details about the 
'check_disk' plugin, please give a look at its man page).

### Configuring the command

<!--DOCUSAURUS_CODE_TABS-->

<!-- INI -->
```bash
CHECK_DISK : exec --executable /path/to/check_disk --args "-w $ARG1$ -c $ARG2$ -p /tmp -p /var -C -w $ARG3$ -c $ARG4$ -p /"
```

<!-- XML -->
```xml
<command name="CHECK_DISK" plugin_name="EXEC">
   <arg name="executable" value="/path/to/check_disk" />  
   <arg name="args" value="-w $ARG1$ -c $ARG2$ -p /tmp -p /var -C -w $ARG3$ -c $ARG4$ -p /" /> 
</command>
```
<!--END_DOCUSAURUS_CODE_TABS-->

### Invoking the command

With the following invocation example we want to monitor _/tmp_ and _/var_ at _10%_ and _5%_, and _/_ at _100MB_ and _50MB_.

```bash
check_nrpe -n -H {JNRPE_SERVER_IP} -c CHECK_DISK -a  10% 5% 100000 50000
```

**$ARG1$** will be replaced with **10%**, **$ARG2$** will be replaced with **5%**, 
**$ARG3$** will be replaced with **100000** and **$ARG4$** will be replaced with **50000**.

:::note
Replace **{JNRPE_SERVER_IP}** with the IP address of your JNRPE server.
:::