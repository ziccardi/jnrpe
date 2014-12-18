#Running the server

  When you explode the JNRPE server archive, you'll find the following directory structure:
  
        jnrpe-server-xxx
             |-bin
             |-etc
             |-lib
             |-docs
             |-logs
             |-plugins
  
  Inside the bin directory you'll find a couple of scripts:
  
  * jnrpe : the script to run on a unix system
  * jnrpe.bat : the script to run on a windows system
  
  The etc directory contains the jnrpe configurations: one jnrpe.ini and a log4j configuration file.
  
  The libs directory contains all the jar needed by JNRPE server to run.
  
  The logs directory will contain all the JNRPE server logs
  
  The plugins directory will contain all the JNRPE plugin package you will want to install.
  
  The JNRPE server command line is very simple, and accepts the following parameters:
  
  * -c,--conf \<file\> : tells to JNRPE where is the JNRPE configuration file. This option is REQUIRED.
  * -l,--list : lists all the plugin installed inside the JNRPE server
  * -v,--version : shows the server version
  
  As soon as you have exploded the archive, you are ready to run JNRPE:
  
        cd $JNRPE_HOME/bin
        ./jnrpe -c ../etc/jnrpe.ini

  Since you still didn't install any plugin, you won't be able to do much...

#Configuring the server

  The first operation to perform as soon as JNRPE has been installed, is a propert configuration.
  While with the previous versions of JNRPE you were forced to write
  an XML file, starting with JNRPE 2.0 you can now choose between two format:
  
  * The new INI configuration file
  * The old style XML configuration file
  
## The new INI configuration file

 This file is splitted into two different sections:
 
  * *The server configuration section* : contains the service configuration (binding address/port, 
    accepted client ip, plugin installation directory, etc). This section is identified by the label [server]
  * *The command configuration section* : contains the configuration of each command the server can execute. 
    A command definition is very simple : just associates a command name and a list of arguments to a 
    plugin name, so that you can refer to the plugin with the command name. Moreover, the command 
    definition, configures the parameters to pass to the plugin. 
    Many commands with different names can refer to the same plugin. This section is identified by the label [commands]
 
 Let's analyze the following configuration:
 
        [server]
        accept-params : true
        bind-address : 127.0.0.1:5666
        plugin-path : /usr/local/jnrpe/plugins
        allow-address : 127.0.0.1
        backlog-size : 256
        read-timeout : 5
        write-timeout : 60
        
        [commands]
        check_disk_C : CHECK_DISK --path C: --warning $ARG1$ --critical $ARG2$
        check_disk_E : CHECK_DISK --path E: --warning $ARG1$ --critical $ARG2$
 
  * **[server]** - this means we are starting the server section
  * **accept-params** : true - means we want JNRPE to expand the $ARG?$ macros. 
   If you set this to false, than arguments passed by check_nrpe (-a flag) will be ignored.
  * **bind-address** : 127.0.0.1:5666 - means we want JNRPE to listen on the 127.0.0.1 IP 
   at port 5666. You can repeat this setting to instruct JNRPE to 
   listen to many addresses/ports. If you want the comunication between check_nrpe and JNRPE to be
   encrypted (check_nrpe default), write the address as 'SSL/IPADDRESS:PORT':
   
      bind-address : SSL/127.0.0.1:5666
 
  * **plugin-path** : /usr/local/jnrpe/plugins - means that in '/usr/local/jnrpe/plugins' 
   JNRPE will find all the installed plugins.
  * **allow-address** : 127.0.0.1 - means that JNRPE must accept request coming from 
   127.0.0.1. You can repeat this line to accept requests from many addresses.
  * **backlog-size** : 256 - means that the maximum number of concurrent connection must be 
   limited to 256.
  * **read-timeout** : 5 - means that the connection will be closed if the client do not writes
    any data in 5 seconds.
  * **write-timeout** : 60 - means that the connection will be closed if the server do not 
    writes any data for more than 60 seconds. 
  * **[commands]** - means that the command section is starting
    Follows the command definition. The format is very simple:
    

        [COMMAND_NAME] : [PLUGIN_NAME] [COMMAND_LINE]

Where:
  
  * **[COMMAND_NAME]** is the name you want to associate to this command definition
  * **[PLUGIN_NAME]** is the plugin that must is execute the command
  * **COMMAND_LINE** is the command line to be passed to the plugin. Any $ARG?$ macro will be replaced with the
     arguments received by check_nrpe (-a flag) if accept-params is true.
    
 The two configured command could be invoked like this:
 
        check_nrpe -n -H 127.0.0.1 -c check_disk_E -a ':10!:5'
        check_nrpe -n -H 127.0.0.1 -c check_disk_C -a ':20!:10'

## The old style XML configuration file

  This file is composed of two different sections:
 
  * **The server configuration section** : contains the service configuration (binding address/port, 
    accepted client ip, plugin installation directory, etc)
  * **The command configuration section** : contains the configuration of each command the server can execute. 
    A command definition is very simple : just associates a command name and a list of arguments to a 
    plugin name, so that you can refer to the plugin with the command name. Moreover, the command 
    definition, configures the parameters to pass to the plugin. 
    Many commands with different names can refer to the same plugin.
 
 Follows an example:

       <?xml version="1.0" encoding="UTF-8"?>
       <config>
          <server accept-params="true" backlog-size="256" readTimeout="5" writeTimeout="60">
             <bind address="127.0.0.1:5666" SSL="false"/>
             <allow ip="127.0.0.1"/>
             <plugin path="/usr/local/jnrpe/plugins"/>
          </server>
          <commands>
             <command name="check_disk_C" plugin_name="CHECK_DISK">
                <arg name="path"  value="C:" />
                <arg name="warning"  value="$ARG1$" />
                <arg name="critical"  value="$ARG2$" />
             </command>
             <command name="check_disk_E" plugin_name="CHECK_DISK">
                <arg name="path"  value="E:" />
                <arg name="warning"  value="$ARG1$" />
                <arg name="critical"  value="$ARG2$" />
             </command>
          </commands>
       </config>

  * **\<server\>** - means we are starting the server section
  * **accept-params** : true - means we want JNRPE to expand the $ARG?$ macros. 
   If you set this to false, than parameters passed by check_nrpe will be ignored.
  * **backlog-size** : 256 - means that the maximum number of concurrent connection must be 
    limited to 256.
  * **read-timeout** : 5 - means that the connection will be closed if the client do not writes
    any data in 5 seconds.
  * **write-timeout** : 60 - means that the connection will be closed if the server do not 
    writes any data for more than 60 seconds.
  * **\<bind address="127.0.0.1:5666" SSL="false"/\>** - means we want JNRPE to listen on the 127.0.0.1 IP 
   at port 5666. You can repeat this setting more than one time to instruct JNRPE to 
   listen to many address.
   If you want the communication between check_nrpe and JNRPE to be encrypted (check_nrpe default) set
   SSL to 'true'.
  * **\<plugin path="/usr/local/jnrpe/plugins"/\>** - means that in '/usr/local/jnrpe/plugins' 
   JNRPE will find all the installed plugins.
  * **\<allow ip="127.0.0.1"/\>** - means that JNRPE must accept request coming from 
   127.0.0.1. You can repeat this line to accept requests from many addresses.
  * **\<commands\>** - means that the command section is starting
   Follows the command definition. The format is very simple:

        <command name="[COMMAND_NAME]" plugin_name="[PLUGIN_NAME]">
           <arg name="[ARG_NAME]"  value="[ARG_VALUE]" />
           ...
        <command/>
   
 Where:
  
  * **[COMMAND_NAME]** is the name you want to associate to this command definition
  * **[PLUGIN_NAME]** is the plugin that must is execute the command
  * **[ARG_NAME]** is an argument to pass to the plugin. The line must be repeated for every 
  argument you want to pass to the plugin.
  * **[ARG_VALUE]** is the value of the argument. It can be a constant of an $ARG?$ variable.
  $ARG?$ variables will be replaced with the arguments received by check_nrpe if accept-params is true.
  All the [ARG_NAME] and all the [ARG_VALUE] will compose the command line to pass to the plugin.
   
 The two configured command could be invoked like this:
 
        check_nrpe -n -H 127.0.0.1 -c check_disk_E -a ':10!:5'
        check_nrpe -n -H 127.0.0.1 -c check_disk_C -a ':20!:10'

