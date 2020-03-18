---
id: plugin-checkpgsql
title: CHECK_PGSQL
---
This plugin tests connections to a PostgreSQL database server

Supported parameters are:
* **--hostname/-H <HOSTNAME\>**: Host name, IP Address, or unix socket (must be an absolute path). Default is 'localhost'.
* **--port/-P <PORT\>**: Port number (default: 5432)
* **--database/-d <DATABASE\>**: Check database with indicated name
* **--logname/-l <LOGNAME\>**: Login name of user
* **--password/-p <PASSWORD\>**: Use the indicated password to authenticate the connection
* **--warning/-w <WARNING\>**: Response time to result in warning status (seconds). The syntax to be used is the Nagios threshold syntax
* **--critical/-c <CRITICAL\>**: Response time to result in critical status (seconds). The syntax to be used is the Nagios threshold syntax
* **--timeout/-t <TIMEOUT\>**: Seconds before connection times out (default: 10)
You need a the pgsql JDBC Driver jar file in the same directory that this plugin is installed.

## Usage Example
In the following example, we will check the connection to a PostgreSQL database.

<!--DOCUSAURUS_CODE_TABS-->
<!-- INI -->
```text
  check_pgsql : CHECK_PGSQL --hostname $ARG1$ --port $ARG2$ --database $ARG3$ --logname $ARG4$ --password $ARG5$ --warning $ARG6$ --critical $ARG7$
```
<!-- XML -->
```xml
<command name="check_pgsql" plugin_name="CHECK_PGSQL">
  <arg name="hostname"  value="$ARG1$" />
  <arg name="port"  value="$ARG2$" />
  <arg name="database"  value="$ARG3$" />
  <arg name="logname"  value="$ARG4$" />
  <arg name="password"  value="$ARG5$" />
  <arg name="warning"  value="$ARG6$" />
  <arg name="critical"  value="$ARG7$" />
</command>
```
<!--END_DOCUSAURUS_CODE_TABS-->

Any of the parameters (`$ARG?$` macros) could be hardcoded inside the JNRPE configuration.

**Invocation**
The following will check the connection to a pgsql database. There will be a warning if the time to connection is 2 seconds or more and a critical if 10 seconds or more:
```bash
$ check_nrpe -n -H 127.0.0.1 -c check_pgsql -a localhost 5432 database username password 2: 10:
```