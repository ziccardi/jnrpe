---
title: 'The CheckFile Plugin'
---

This plugin is able to perform simple checks against files :

* Checks that a file exists (-f parameter )
* Checks that a file do not exists (-F parameter )
* Checks if file age falls inside a range
* Checks if file size falls inside a range
* Checks that a file contains a string
* Checks that a file do not contain a string

Supported parameters are :

* `--file/-f [FILENAME]`:  
! Incompatible with `--FILE`
Tells the plugin to return a critical status if the file FILENAME do not exists
* `--FILE/-F [FILENAME]`:  
! Incompatible with `--file`
Tells the plugin to return a critical status if the file FILENAME exists
* `--critical/-c [AGE-RANGE]`:  
! Requires the `--file/f` parameter  
! Incompatible with `--sizecritical
Tells the plugin to return a critical status if the file age (in seconds) falls inside the AGE-RANGE range.
`  
```
Example ranges :
300: - Critical status if file is older than 300 seconds
 :300 - Critical status if the file is younger that 301 seconds.
50:200 - Critical status if file age is between 50 and 200 seconds.
```
* `--warning/-w` :  
! Requires the `--file/f` parameter  
! Incompatible with `--sizewarning/-W`
same as `--critical/-c`, but returns a WARNING status.  
* `--sizecritical/-C [SIZE-RANGE]`: 
! Requires the `--file/f` parameter  
! Incompatible with `--critical/-c`
Tells the plugin to return a critical status if the file size falls inside the SIZE-RANGE range.
* `--sizewarning/-W`: 
! Requires the `--file/f` parameter  
! Incompatible with `--warning/-w`
same as sizecritical/-C, but returns a WARNING status
* `--contains/-O [STRING,WARNINGRANGE,CRITICALRANGE]`:  
Checks how many time STRING is found inside the file, than returns WARNING, OK or ERROR, according to the WARNINGRANGE and CRITICALRANGE.
* `--notcontains/-N [STRING]`:  
! Requires the `--file/f` parameter  
Tells the plugin to return a critical status if the file contains the string [STRING].

## Usage Example
In the following example, we will monitor the age of a generic file.

**Configuration**
[ui-tabs position="top-left" active="0" theme="badges"]
[ui-tab title="INI"]
```
  check_file : CHECK_FILE --file $ARG1$ --warning $ARG2$ --critical $ARG3$
```
[/ui-tab]
[ui-tab title="XML"]
```xml
<command name="check_file" plugin_name="CHECK_FILE">
   <arg name="file"  value="$ARG1$" />
   <arg name="warning"  value="$ARG2$" />
   <arg name="critical"  value="$ARG3$" />
</command>
```
[/ui-tab]
[/ui-tabs]

Any of the parameters ($ARG?$ macros) could be hardcoded inside the JNRPE configuration.

**Invocation**

The following invocation example will raise a warning if the file age is between 500 and 1000 seconds and a critical if the file is older than 1000 seconds

```
  check_nrpe -n -H my.jnrpe.server -c check_file -a /path/to/my/file 500:1000 1000:
```