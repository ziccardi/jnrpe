#JNRPE Server
 
   JNRPE originally started as a java implementation of the NRPE [Nagios](http://www.nagios.com/#ref=maxzjnrpe) Addon. 
   
   As of version 2.0, it has been splitted into several projects to add more
   features and modularity and is now embeddable, giving you the ability to 
   enable your application to be called from check_nrpe and easily
   returns values that can be monitored by [Nagios](http://www.nagios.com/#ref=maxzjnrpe).
   
## Why JNRPE ?
   
   But why another implementation of NRPE? Wasn't NRPE enough? The answer is : maybe.
   It would be enough if you don't need to write JAVA plugin, if you don't care 
   compiling it again and again on any machine you have to install it and so on.
   
   With JNRPE you can easily write new plugins : you already have out of the box the 
   protocol implementation, the [Nagios](http://www.nagios.com/#ref=maxzjnrpe) threshold syntax parsing, the argument replacing and
   the command line parsing and validation.
   
   Your plugin must simply return a string (the one that will be visualized in the [Nagios](http://www.nagios.com/#ref=maxzjnrpe) console)
   and a status code (ERROR, WARNING, OK or UNKNOWN).
   
   JNRPE will take care of loading your plugin inside its own JVM instance and execute it, 
   resolving the problem of the JVM overhead (with NRPE a new JVM would be launched for every check).
   
## JNRPE Concepts
 
   JNRPE has been implemented to resemble as much as possible the behaviour of NRPE so that if you have
   experience in using the latter, you won't have any difficulty using JNRPE.
   
   The main concepts you must be aware when dealing with JNRPE are:
   
   * PLUGINS : plugins are piece of code that are able to perform some kind of work. They need some 
       parameter that instructs them about what to do.
   * COMMANDS : commands are configurations that tells JNRPE how a plugin must be executed to perform
       a precise work.
       
   Let's give a look to a simple example.
   
   Suppose you want to monitor the disk space on a windows machine that has a C and a E disk.
   You want [Nagios](http://www.nagios.com/#ref=maxzjnrpe) to raise the following alerts:
   
   * WARNING if less than 20% of space is available on disk C
   * WARNING if less than 10% of space is available on disk E
   * CRITICAL if less than 10% of space is available on disk C
   * CRITICAL if less than 5% of space is available on disk E
     
   The plugin you must use to perform such check is the CHECK_DISK plugin,
   In our example, we will use the following parameters:
   
        -p path -w WARNING_RANGE -c CRITICAL_RANGE

   The range syntax is as follows:
   
        [@]start:end

        Notes:
        start <= end
        start and ":" is not required if start=0
        if range is of format "start:" and end is not specified, assume end is infinity
        to specify negative infinity, use "~"
        if range starts with "@", then the range is negated (inclusive of endpoints)

  That means that to perform the checks we described before, we must configure the following
  commands:
  
        check_disk_C : CHECK_DISK --path C: --warning :20 --critical :10
        check_disk_E : CHECK_DISK --path E: --warning :10 --critical :5

   If you want to configure the value of some parameter directly inside [Nagios](http://www.nagios.com/#ref=maxzjnrpe), you can use
   argument substitution:
   
        check_disk_C : CHECK_DISK --path C: --warning $ARG1$ --critical $ARG2$
        check_disk_E : CHECK_DISK --path E: --warning $ARG1$ --critical $ARG2$

   The configuration has the following meaning :
     
   * check_disk_C : means we are creating a command called check_disk_C
   * CHECK_DISK : when the check_disk_C is called, the CHECK_DISK plugin is invoked
   * --path C: : pass the parameter '--path C:' to the plugin
   * --warning $ARG1$ : accept a parameter from check_nrpe and replace $ARG1$ with such parameter
   * --critical $ARG2$ : accept a parameter from check_nrpe and replace $ARG2$ with the second parameter received.
   
   To invoke the command, you could use:
   
        check_nrpe -h 127.0.0.1 -c check_disk_C -a ':20!:10'
   
   As a result, JNRPE will invoke the CHECK_DISK plugin with the following command line:
   
        --path C: --warning :20 --critical :10
