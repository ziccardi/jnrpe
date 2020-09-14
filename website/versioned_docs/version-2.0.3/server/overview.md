---
id: version-2.0.3-server-overview
title: Overview
original_id: server-overview
---

JNRPE has been implemented to resemble as much as possible the behaviour of NRPE so that if you have experience
in using the latter, you won’t have any difficulty using JNRPE.

The main concepts you must be aware when dealing with JNRPE are:

* **PLUGINS** : plugins are piece of code that are able to perform some kind of work. They need some parameter that 
instructs them about what to do.
* **COMMANDS** : commands are configurations that tells JNRPE how a plugin must be executed to perform a precise work.

Let’s give a look to a simple example.

We want to monitor the disk space on a windows machine that has a **C** and a **E** disk and have Nagios raise 
the following alerts:

* **WARNING** if less than 20% of space is available on disk C
* **WARNING** if less than 10% of space is available on disk E
* **CRITICAL** if less than 10% of space is available on disk C
* **CRITICAL** if less than 5% of space is available on disk E

The plugin we must use to perform a check like that will be the **CHECK_DISK** plugin. 
To realize our example, we will use the following parameters:

```
-p path -w WARNING_RANGE -c CRITICAL_RANGE
```

The range syntax is as follows:

    [@]start:end
    
    Notes:
    start <= end
    start and ":" is not required if start=0
    if range is of format "start:" and end is not specified, assume end is infinity
    to specify negative infinity, use "~"
    if range starts with "@", then the range is negated (inclusive of endpoints)
 
That means that to perform the checks we described before, we must configure the following commands:

    check_disk_C : CHECK_DISK --path C: --warning :20 --critical :10
    check_disk_E : CHECK_DISK --path E: --warning :10 --critical :5

To make the command a bit more dynamic so that can be configured directly in Nagios we can use argument substitution:

    check_disk_C : CHECK_DISK --path C: --warning $ARG1$ --critical $ARG2$
    check_disk_E : CHECK_DISK --path E: --warning $ARG1$ --critical $ARG2$

The configuration has the following meaning :

* **check_disk_C** : means we are creating a command called **check_disk_C**
* **CHECK_DISK** : when the **check_disk_C** is called, the **CHECK_DISK** plugin is invoked
* **–path C**: : pass the parameter ‘**–path C**:’ to the plugin
* **–warning $ARG1$** : accept a parameter from **check_nrpe** and replace **$ARG1$** with such parameter
* **–critical $ARG2$** : accept a parameter from **check_nrpe** and replace **$ARG2$** with the second parameter received.

To invoke the command, we could use:

```bash
check_nrpe -h 127.0.0.1 -c check_disk_C -a ':20!:10'
```

As a result, JNRPE will invoke the CHECK_DISK plugin with the following parameters:

```bash
--path C: --warning :20 --critical :10
```
