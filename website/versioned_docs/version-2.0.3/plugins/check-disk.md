---
id: version-2.0.3-plugin-checkdisk
title: CHECK_DISK
original_id: plugin-checkdisk
---

This plugin monitors the disk usage of a specified disk.

Supported parameters are :
* **--path/-p** [path]: Tells the plugin what filesystem to monitor. On windows this will be in form of 'C:' , 'D:', etc., 
while on unix it will be a directory.
* **--warning/-w** [RANGE]: Instructs the plugin to return a warning state if the percent of free space falls inside 
the specified RANGE
* **--critical/-c** [RANGE]: Instructs the plugin to return a critical state if the percent of free space falls inside 
the specified RANGE
    
  Example ranges:
  * **10:** - Free space more than 10% 
  * **:30** - Free space less than 30%
  * **15:20** - Free space between 15% and 20%.

## Example Usage
The following example configures the check so that all the parameters will be passed with the check_nrpe command. 
This way, you'll be able to use this definition to monitor all the disks.

### Configuration
<!--DOCUSAURUS_CODE_TABS-->

<!-- INI -->
```bash
check_disk : CHECK_DISK --path $ARG1$ --warning $ARG2$ --critical $ARG3$
```

<!-- XML -->
```xml
<command name="check_disk" plugin_name="CHECK_DISK">
  <arg name="path"  value="$ARG1$" />
  <arg name="warning"  value="$ARG2$" />
  <arg name="critical"  value="$ARG3$" />
</command>
```
<!--END_DOCUSAURUS_CODE_TABS-->

Any of the parameters ($ARG?$ macros) could be hardcoded inside the JNRPE configuration.

### Invocation
With the following invocation example we will rise a warning if the free space on the specified folder is less that 20% and a critical if it is less that 5%

<!--DOCUSAURUS_CODE_TABS-->

<!-- UNIX -->
```bash
check_nrpe -n -H my.jnrpe.server -c check_disk -a / :20 :5
```

<!-- WINDOWS -->
```xml
check_nrpe -n -H my.jnrpe.server -c check_disk -a C: :20 :5
```
<!--END_DOCUSAURUS_CODE_TABS-->
