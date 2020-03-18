---
id: plugin-checkfile
title: CHECK_FILE
---

This plugin is able to perform simple checks against files :

* Checks that a file exists (-f parameter )
* Checks that a file do not exists (-F parameter )
* Checks if file age falls inside a range
* Checks if file size falls inside a range
* Checks that a file contains a string
* Checks that a file do not contain a string

Supported parameters are :
* --file/-f [FILENAME] Tells the plugin to return a critical status if the file FILENAME do not exists.
Can't be used together with **--FILE**
* --FILE/-F [FILENAME] Tells the plugin to return a critical status if the file FILENAME exists
Can't be used together with **--file**
* --critical/-c [AGE-RANGE] Tells the plugin to return a critical status if the file age (in seconds) falls inside the AGE-RANGE range.
   
   **Requires**:  --file/f <br>
   **Can't be used together with**: --sizecritical
   
   Example:
    Example ranges :
    ```
    300: - Critical status if file is older than 300 seconds
    :300 - Critical status if the file is younger that 301 seconds.
    50:200 - Critical status if file age is between 50 and 200 seconds.
   ```
* --warning/w [AGE-RANGE] : same as **--critical/-c**, but returns a **WARNING** status.
   
   **Requires**:  --file/f <br>
   **Can't be used together with**: --sizewarning
* --sizecritical/-C [SIZE-RANGE] : Tells the plugin to return a critical status if the file size falls inside the SIZE-RANGE range.
   
   **Requires**:  --file/f <br>
   **Can't be used together with**: --critical/-c
* --sizewarning/-W [SIZE-RANGE] : same as **sizecritical/-C**, but returns a **WARNING** status.
   
   **Requires**:  --file/f <br>
   **Can't be used together with**: --warning/-w
* --contains/-O [STRING,WARNINGRANGE,CRITICALRANGE] : Checks how many time **STRING** is found inside the file, then returns 
**WARNING**, **OK** or **ERROR**, according to the **WARNINGRANGE** and **CRITICALRANGE**.
   
   **Requires**:  --file/f
* --notcontains/-N [STRING] : Tells the plugin to return a critical status if the file contains the string [STRING].
   
   **Requires**:  --file/f

## Example Usage
In the following example, we will monitor the age of a generic file.

### Configuration
<!--DOCUSAURUS_CODE_TABS-->

<!-- INI -->
```bash
check_file : CHECK_FILE --file $ARG1$ --warning $ARG2$ --critical $ARG3$
```

<!-- XML -->
```bash
<command name="check_file" plugin_name="CHECK_FILE">
   <arg name="file"  value="$ARG1$" />
   <arg name="warning"  value="$ARG2$" />
   <arg name="critical"  value="$ARG3$" />
</command>
```
<!--END_DOCUSAURUS_CODE_TABS-->

Any of the parameters (_$ARG?$_ macros) could be hardcoded inside the **JNRPE** configuration.

### Invocation

The following invocation example will raise a warning if the file age is between 500 and 1000 seconds and a critical if the file is older than 1000 seconds

```bash
check_nrpe -n -H my.jnrpe.server -c check_file -a /path/to/my/file 500:1000 1000:
```