---
id: plugin-checkmysqlquery
title: CHECK_MYSQL_QUERY
---

This plugin checks a Mysql query result against threshold levels.

* **--hostname/-H <HOSTNAME>**: Host name, IP Address, or unix socket (must be an absolute path)
* **--port/-P <PORT>**: Port number (default: 3306)
* **--database/-d <DATABASE>**: Check database with indicated name
* **--username/-u <USERNAME>**: Connect using the indicated username
* **--password/-p <PASSWORD>**: Use the indicated password to authenticate the connection
* **--query/-q <QUERY>**: SQL query to run. Only first column in first row will be read
* **--warning/-w <WARNING>**: WARNING range
* **--critical/-c <CRITICAL>**: CRITICAL range You need a mysql connector jar file in the same directory that this 
plugin is installed.

## Example Usage
In the following example, we will check the result of a simple query (`select count(*) from users_table`).

### Configuration

<!--DOCUSAURUS_CODE_TABS-->
<!-- INI -->
```bash
check_mysql_query:CHECK_MYSQL_QUERY --check-slave --hostname $ARG1$ --port $ARG2$ --database $ARG3$ --user $ARG4$ --password $ARG5$ --query $ARG6$ --warning $ARG7$ --critical $ARG8$
```
<!-- XML -->
```xml
<command name="check_mysql_query" plugin_name="CHECK_MYSQL_QUERY">
  <arg name="hostname"  value="$ARG1$" />
  <arg name="port"  value="$ARG2$" />
  <arg name="database"  value="$ARG3$" />
  <arg name="user"  value="$ARG4$" />
  <arg name="password"  value="$ARG5$" />
  <arg name="query"  value="$ARG6$" />
  <arg name="warning"  value="$ARG7$" />
  <arg name="critical"  value="$ARG8$" />
</command>
```
<!--END_DOCUSAURUS_CODE_TABS-->

### Invocation
The following will query the number of rows in a the table "users_table". It will send a warning if it takes 10 seconds 
or more to execute the query and a critical if it takes 30 seconds or more:

```bash
check_nrpe -H 127.0.0.1 -c check_mysql_query  -n -a localhost 3306 database username password "select count(*) from users_table" 30 10
```