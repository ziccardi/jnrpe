---
title: 'The CHECK_USERS plugin'
---

This plugin checks the number of users currently logged in on the local system and generates an error if the number exceeds the thresholds specified.

Supported parameters are:
* **--warning/-w <WARNING\>**: Set WARNING status if the number of logged in users falls inside the specified range
* **--critical/-c <CRITICAL\>:** Set CRITICAL status if the number of logged in users falls inside the specified range

Example ranges:
```
300: - More than 300 users logged in
:300 - Less than 300 users logged in
50:200 - Between 50 and 200 users logged in
```

## Usage Example

In the following example, we will check the number of logged in users.

**Configuration**
[ui-tabs position="top-left" active="0" theme="badges"]
[ui-tab title="INI"]
```
check_users : CHECK_USERS --warning $ARG1$ --critical $ARG2$
```
[/ui-tab]
[ui-tab title="XML"]
```xml
  <command name="check_users" plugin_name="CHECK_USERS">
     <arg name="warning"  value="$ARG1$" />
     <arg name="critical"  value="$ARG2$" />
  </command>
```
[/ui-tab]
[/ui-tabs]

Any of the parameters (`$ARG?$` macros) could be hardcoded inside the JNRPE configuration.

**Invocation**

The following invocation example will raise a warning if the number of logged in users exceeds 10 and a critical if the number of logged in users exceeds 1000.

```bash
check_nrpe -n -H my.jnrpe.server -c check_users -a  10: 1000:
```
