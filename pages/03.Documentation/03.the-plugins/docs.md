---
title: 'The Plugins'
---

The JNRPE plugin package provides a number of plugin.
As of the current version, the provided plugins are:

* [CheckFile plugin](./the-checkfile-plugin) : is a plugin that is able to perform simple controls about files. It could for example check for file existence, file size, file content, etc.
* [CheckDisk](./the-check_disk-plugin) plugin : can check the amount of free space of any passed in path.
* [Native Plugin](./the-exec-plugin) : can execute external executable plugins (i.e. native Nagios plugins)
* [CheckOracle plugin](./the-check_oracle-plugin) : can perform various checks against oracle databases (aliveness,tablespace usage, etc.)
* [CheckJMX](./the-check_jmx-plugin) : can perform JMX queries so that JMX result can be monitored with Nagios.
* [TestPlugin](./the-testplugin) : a simple test plugin that can be used to check the JNRPE is correctly installed.
* [CheckUsers](./the-check_users-plugin) : checks the the number of users currently logged in on the local system and generates an error if the number exceeds the thresholds specified.
* [CheckMysql](./the-check_mysql-plugin) : tests MySQL database connection status
* [CheckMysqlQuery](./the-check_mysql_query-plugin) : tests a Msyql query result against threshold levels.
* [CheckPgsql](./the-check_pgsql-plugin) : tests PostgreSQL database connection status
* [CheckProcs](./the-check_procs-plugin) : checks system processes and does check against metrics.
* [CheckTime](./the-check_time-plugin) : Checks the time on a specified host.
* [CheckTomcat](./the-check_tomcat-plugin) : checks status of a tomcat instance
* [CheckHttp](./the-check_http-plugin) : tests the HTTP service on a specified host
* [CheckSsh](./the-check_ssh-plugin) : tests ssh connections on a remote host.
* [CheckPing](./the-check_ping-plugin) : use ping to check connection statistics for a remote host