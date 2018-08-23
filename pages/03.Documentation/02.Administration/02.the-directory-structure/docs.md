---
title: 'The Directory Structure'
taxonomy:
    category: docs
---

When you explode the JNRPE server archive (or right after you have installed it with the installer), you’ll find the following directory structure:

```
  JNRPE
    |- bin              ----> Contains the script to be used to run JNRPE
    |- etc              ----> Contains the JNRPE configuration files
    |- lib              ----> Contains the libraries required by JNRPE
    |- plugins          ----> Contains all the plugin packages
    |     |- base       ----> Contains the plugin package bundled with JNRPE
    |- Uninstaller      ----> Contains the generated uninstaller
    |- wrapper          ----> Contains the YAJSW binaries
          |- etc        ----> Contains the YAJSW configuration
          |- lib        ----> Contains the YAJSW dependencies
```

* `bin` will contain the following scripts:
    * `jnrpe` : the script to run on a unix system
    * `jnrpe.bat` : the script to run on a windows system
* `etc` will contains the jnrpe configurations: 
    * `jnrpe.ini` : configuration of the JNRPE server
    * `log4j.properties`: configuration for the logger
* `libs` contains all the jar needed by JNRPE server to run.
* `logs` will contain all the JNRPE server logs
* `plugins` will contain all the JNRPE plugin package you will want to install.

The JNRPE server command line is very simple, and accepts the following parameters:

* `-c,–conf <file>` : tells to JNRPE where is the JNRPE configuration file. This option is REQUIRED.
* `-l,–list` : lists all the plugin installed inside the JNRPE server
* `-v,–version` : shows the server version

The simplest command line to run JNRPE is:

````
cd $JNRPE_HOME/bin
./jnrpe -c ../etc/jnrpe.ini
````
