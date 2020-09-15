---
id: version-2.0.3-plugin-checkoracle
title: CHECK_ORACLE
original_id: plugin-checkoracle
---

With this plugin, you can perform various checks agains an Oracle database without the need of installing the oracle client software: you only need to download the appropriate JDBC driver from the oracle site (a jar file) and put it in the same directory as this plugin (if you prefer, you can put such jar inside the lib/ext directory of your JRE installation).

Follows the list of checks this plugin supports:

* **health**: checks if the database is up and running
* **tablespace**: checks how much tablespace space is available
* **cache**: checks the cache hit rate

Supported parameters are:

* --alive: Tells the plugin to check if the database is reachable. This parameter needs the following arguments too:
  * -u/--username: The Oracle username
  * -p/--password: The Oracle password
  * --db: The Oracle SID/SERVICE_ID
  * --server: The database server IP address
* --tablespace <tablespace name> : Instructs the plugin to check how much tablespace space is available. This parameter needs the following arguments too:
  * -u/--username: The Oracle username
  * -p/--password: The Oracle password
  * --db: The Oracle SID/SERVICE_ID
  * --server: The database server IP address
--cache: Tells the plugin to check the cache hit rate. This parameter also needs:
  * -u/--username: The Oracle username
  * -p/--password: The Oracle password
  * --db: The Oracle SID/SERVICE_ID
  * --server:The database server IP address
* -w/--warning: The warning range
* -c/--critical: The critical range

## Example Usage
The following example, shows how to check that an Oracle database is alive.

### Configuration

<!--DOCUSAURUS_CODE_TABS-->
<!-- INI -->
```bash
check_oracle_alive : CHECK_ORACLE --alive --username $ARG1$ --password $ARG2$ --db $ARG3$  --server $ARG4$
```
<!-- XML -->
```xml
<command name="check_oracle_alive" plugin_name="CHECK_ORACLE">  
  <arg name="alive" />
  <arg name="username" value="$ARG1$" />
  <arg name="password" value="$ARG2$" />
  <arg name="db" value="$ARG3$" />
  <arg name="server" value="$ARG4$" /> 
</command>
```
<!--END_DOCUSAURUS_CODE_TABS-->

### Invocation
With the following invocation example we want to monitor that the DATABASE with SID 'TESTSID' on the server 'DB_ADDRESS' 
is up and running. To perform the check we use the user SCOTT with password TIGER.

```bash
check_nrpe -n -H my.jnrpe.server -c check_oracle_alive -a  SCOTT TIGER TESTSID DB_ADDRESS
```
