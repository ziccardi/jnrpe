---
title: 'The CHECK_TOMCAT plugin'
---

Checks the tomcat /manager/status page for excessive memory usage or an excessive number of threads in use. This plugin does a HTTP GET of the tomcat status page:
```
/manager/status?XML=true
```
It checks the resulting XML for:

* Low free memory
* Excessive thread usage
In order to use this, you must provide a username and password that has a ‘manager-gui’ role in the `$CATALINA_HOME/conf/tomcat-users.xml` file.

Supported parameters are:

* **---hostname/-H <HOSTNAME\>**: Host name or IP Address
* **---port/-p <PORT\>**: Port number; default is 8080
* **---username/-l <USERNAME\>**: Username for authentication
* **---password/-a <PASSWORD\>**: Password for authentication
* **--threads/-t** : Check threads. Used instead of the –memory option.
* **--memory/-m** : Check memory. Used instead of the –threads option.
* **---warning/-w <WARNING\>**: Warning threshold value for threads or memory (in MB or %). Must be used with either the ‘memory’ or ‘threads’ option.
* **--critical/-c <CRITICAL\>**: Critical threshold value for threads or memory (in MB or %). Must be used with either the ‘memory’ or ‘threads’ option.
* **--percent** : indicates that we want to use evaluate percent values instead of raw values.
* **–-th/-r <THRESHOLDS\>**: configure the ranges using the new threshold format. The format is: `metric={metric},ok={range},warn={range},crit={range},unit={unit},prefix={SI prefix}`
The `–-threads, –-memory, –-warning, –-critical` and `–-percent` parameters can’t be used together with the `–-th` parameters, since this one is able to supply all those informations in a more concise and precise way.

The plugin is able to gather the following metrics:

* **memory** : the memory usage in bytes
* **memory%** : the memory usage as percent of the total memory available for tomcat
* **{connector name}-threadInfo** : the usage of threads for the connector {connector name}. You will have one metric for each connector.
* **{connector name}-threadInfo%** : the thread usages for connector {connector name} as percent of the total available threads. You will have one metric for each connector
* **{MemoryPool name}-memoryPool** : the memory usage for the memory pool named {MemoryPool name}. You will have one metric for each connector.
* **{MemoryPool name}-memoryPool%** : the memory usage for the memory pool named {MemoryPool name} as percent of the total of the available memory. You will have one metric for each connector.

## Legacy thresholds vs new thresholds
When using the legacy thresholds parameters (–warning and/or –critical), you have to specify if you want to check threads (–threads) or memory (–memory). Moreover, you won’t be able to specify the connector you are interested in: the plugin will report you the worst case The same is for the memory: you won’t be able to specify the ‘memory pool’, so only the JVM memory usage will be checked.

The new threshold format is much more powerful.

Suppose you have a connector named `myconnector` and you want to check its thread usage. You can check that simply invoking the plugin as:

```ini
CHECK_TOMCAT --hostname $ARG1$ --port $ARG2$ --user $ARG3$ --password $ARG4$ --th metric=myconnector-threadInfo,warn=80..inf,crit=90..inf
```
If you want to check the usage as percentage:
```ini
CHECK_TOMCAT --hostname $ARG1$ --port $ARG2$ --user $ARG3$ --password $ARG4$ --th metric=myconnector-threadInfo%,warn=80..inf,crit=90..inf
```
## Usage Examples
### Check memory
In the following example we will check for the amount of jvm memory in use.

**Configuration**
[ui-tabs position="top-left" active="0" theme="badges"]
[ui-tab title="INI  (Old Threshold Syntax)"]
```ini
CHECK_TOMCAT : CHECK_TOMCAT --memory --percent --hostname $ARG1$ --port $ARG2$ --user $ARG3$ --password $ARG4$ --warning $ARG5$ --critical $ARG6$
```
[/ui-tab]
[ui-tab title="INI  (New Threshold Syntax)"]
```ini
CHECK_TOMCAT : CHECK_TOMCAT --hostname $ARG1$ --port $ARG2$ --user $ARG3$ --password $ARG4$ --th metric=memory%,warn=$ARG5$,crit=$ARG6$,prefix=M
```
!!!! The `prefix=M` means that we want the values passed to the range (critical or warning) to be interpreted as Megabytes. That can’t be done with the old syntax, which always defaults to megabytes.
[/ui-tab]
[ui-tab title="XML (Old Threshold Syntax)"]
```xml
<command name="CHECK_TOMCAT" plugin_name="CHECK_TOMCAT">
   <arg name="memory"/>
   <arg name="hostname"  value="$ARG1$" />
   <arg name="port"  value="$ARG2$" />
   <arg name="user"  value="$ARG3$" />
   <arg name="password"  value="$ARG4$" />
   <arg name="warning"  value="$ARG5$" />
   <arg name="critical"  value="$ARG6$" />
   <arg name="percent" />
</command>
```
[/ui-tab]
[ui-tab title="XML (New Threshold Syntax)"]
```xml
<command name="CHECK_TOMCAT" plugin_name="CHECK_TOMCAT">
   <arg name="hostname"  value="$ARG1$" />
   <arg name="port"  value="$ARG2$" />
   <arg name="user"  value="$ARG3$" />
   <arg name="password"  value="$ARG4$" />
   <arg name="th"  value="metric=memory%,warn=$ARG5$,crit=$ARG6$,prefix=M" />
</command>
```
!!!! The `prefix=M` means that we want the values passed to the range (critical or warning) to be interpreted as Megabytes. That can’t be done with the old syntax, which always defaults to megabytes.
[/ui-tab]
[/ui-tabs]

!!!! Any of the parameters (`$ARG?$` macros) could be hardcoded inside the JNRPE configuration.

**Invocation**

The following will throw a warning if 50% or more of the maximum memory less is used and a critical if 90% or more of the maximum memory is available with the old syntax.

[ui-tabs position="top-left" active="0" theme="badges"]
[ui-tab title="Old Syntax"]
```bash
$ check_nrpe -n -H 127.0.0.1 -c CHECK_TOMCAT -a localhost 8080 username password 50: 90: 
```
[/ui-tab]
[ui-tab title="New Syntax"]
```bash
$ check_nrpe -n -H 127.0.0.1 -c CHECK_TOMCAT -a localhost 8080 username password 50..inf 90..inf 
```
[/ui-tab]
[/ui-tabs]

### Check Threads usage
In the following example we will check for the amount of threads available.

**Configuration**
[ui-tabs position="top-left" active="0" theme="badges"]
[ui-tab title="INI"]
```ini
CHECK_TOMCAT : CHECK_TOMCAT --threads --hostname $ARG1$ --port $ARG2$ --user $ARG3$ --password $ARG4$ --warning $ARG5$ --critical $ARG6$
```
[/ui-tab]
[ui-tab title="XML"]
```xml
<command name="CHECK_TOMCAT" plugin_name="CHECK_TOMCAT">
  <arg name="threads"/>
  <arg name="hostname"  value="$ARG1$" />
  <arg name="port"  value="$ARG2$" />
  <arg name="user"  value="$ARG3$" />
  <arg name="password"  value="$ARG4$" />
  <arg name="warning"  value="$ARG5$" />
  <arg name="critical"  value="$ARG6$" />
</command>
```
[/ui-tab]
[/ui-tabs]

!!!! Any of the parameters (`$ARG?$` macros) could be hardcoded inside the JNRPE configuration.

**Invocation**
The following will throw a warning if there are 10 or less threads available and a critical if there are 5 or less availble threads.
```bash
$ check_nrpe -n -H 127.0.0.1 -c CHECK_TOMCAT -a localhost 8080 username password :10 :5  
```