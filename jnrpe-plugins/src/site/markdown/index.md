#JNRPE Plugin package
 
This package contains all the core plugin packaged with the JNRPE Server.
As of current version, the following plugins are included:
   
* **[CheckFile](plugins/check_file.html)** plugin : is a plugin that is able to perform simple controls about files.
It could for example check for file existence, file size, file content, etc.
* **[CheckDisk](plugins/check_disk.html)** plugin : can check the amount of free space of any passed in path.
* **[NativePlugin](plugins/excec.html)** : can execute external executable plugins (i.e. native [Nagios](http://www.nagios.com/#ref=maxzjnrpe) plugins)
* **[CheckOracle](plugins/check_oracle.html)** plugin : can perform various checks against oracle databases (aliveness,tablespace usage, etc.)
* **[CheckJMX](plugins/check_jmx.html)** : can perform JMX queries so that JMX result can be monitored with [Nagios](http://www.nagios.com/#ref=maxzjnrpe).
* **TestPlugin** : a simple test plugin that can be used to check the JNRPE is correctly installed.
* **[CheckUsers](plugins/check_users.html)** : checks the the number of users currently logged in on the local system and generates an error 
if the number exceeds the thresholds specified.
* **[CheckMysql](plugins/check_mysql.html)** : tests MySQL database connection status
* **[CheckMysqlQuery](plugins/check_mysql_query.html)** : tests a Msyql query result against threshold levels.
* **[CheckPgsql](plugins/check_pgsql.html)** : tests PostgreSQL database connection status
* **[CheckProcs](plugins/check_procs.html)** : checks system processes and does check against metrics.
* **[CheckTime](plugins/check_time.html)** :  Checks the time on a specified host.
* **[CheckTomcat](plugins/check_tomcat.html)** : checks status of a tomcat instance
* **[CheckHttp](plugins/check_http.html)** : tests the HTTP service on a specified host
* **[CheckSsh](plugins/check_ssh.html)** : tests ssh connections on a remote host.
* **[CheckPing](plugins/check_pinh.html)** : use ping to check connection statistics for a remote host