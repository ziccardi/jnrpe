---
title: 'The CHECK_MYSQL plugin'
---

This plugin can be used to:

* test if a MySQL server is alive
* check how long needs to connect to a MySQL server
* check the MySQL server slaves status

Supported parameters are:

* **--hostname/-H <HOSTNAME\>**: Host name, IP Address, or unix socket (must be an absolute path)
* **--port/-P <PORT\>**: Port number (default: 3306)
* **--database/-d <DATABASE\>**: Check database with indicated name
* **--username/-u <USERNAME\>**: Connect using the indicated username
* **--password/-p <PASSWORD\>**: Use the indicated password to authenticate the connection
* **--check-slave/-S** : Check if the slave thread is running properly.
* **--warning/-w <WARNING\>**: Exit with WARNING status if slave server "seconds behind master" (if --check-slave is specified) or the number of seconds required to connect (if --check-slave is NOT specified) falls inside the given range.
* **--critical/-c <CRITICAL\>**: Exit with CRITICAL status if slave server "seconds behind master" (--check-slave is specified) or the number of seconds required to connect (--check-slave is NOT specified) falls inside the given range.
You need a mysql connector jar file in the same directory as this plugin is installed.

## Usage Example

### Checking connection status
In the following example, we will check the connection to a mysql database. We will give a CRITICAL if the connection can't be established or if it requires more than 8 seconds. A WARNING will be returned if the connection requires something between 3 and 8 seconds.

**Configuration**
[ui-tabs position="top-left" active="0" theme="badges"]
[ui-tab title="INI"]
```
check_mysql : CHECK_MYSQL --hostname $ARG1$ --port $ARG2$ --database $ARG3$ --user $ARG4$ --password $ARG5$ --warning $ARG6$ --critical $ARG7$
```
[/ui-tab]
[ui-tab title="XML"]
```xml
  <command name="check_mysql" plugin_name="CHECK_MYSQL">
        <arg name="hostname"  value="$ARG1$" />
        <arg name="port"  value="$ARG2$" />
        <arg name="database"  value="$ARG3$" />
        <arg name="user"  value="$ARG4$" />
        <arg name="password"  value="$ARG5$" />
        <arg name="warning"  value="$ARG6$" />
        <arg name="critical"  value="$ARG7$" />
  </command>
```
[/ui-tab]
[/ui-tabs]

Any of the parameters (`$ARG?$` macros) could be hardcoded inside the JNRPE configuration.

**Invocation**

The following will check the connection to a mysql database
```bash
  $ check_nrpe -n -H 127.0.0.1 -c check_mysql -a localhost 3306 database username password 3:8 8: 
```

### Checking the slave thread
In the following example we show how to check a slave thread.

**Configuration**
[ui-tabs position="top-left" active="0" theme="badges"]
[ui-tab title="INI"]
```ini
check_mysql: CHECK_MYSQL --check-slave --hostname $ARG1$ --port $ARG2$ --database $ARG3$ --user $ARG4$ --password $ARG5$ --warning $ARG6$ --critical $ARG7$
``` 
[/ui-tab]
[ui-tab title="XML"]
```xml
  <command name="check_users" plugin_name="CHECK_MYSQL">
        <arg name="check-slave" />
        <arg name="hostname"  value="$ARG1$" />
        <arg name="port"  value="$ARG2$" />
        <arg name="database"  value="$ARG3$" />
        <arg name="user"  value="$ARG4$" />
        <arg name="password"  value="$ARG5$" />
        <arg name="warning"  value="$ARG6$" />
        <arg name="critical"  value="$ARG7$" />     
  </command>
```
[/ui-tab]
[/ui-tabs]

**Invocation**

The following will check the connection a slave thread and will send a warning if it is more than 10 seconds behind the master and a critical if it is more than 30 seconds behind the master:
```bash
$ check_nrpe -n -H 127.0.0.1 -c check_mysql -a localhost 3306 database username password 10: 30:  
```