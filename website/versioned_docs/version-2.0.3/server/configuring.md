---
id: version-2.0.3-configure-jnrpe
title: Configuring JNRPE
original_id: configure-jnrpe
---

The first operation to perform as soon as JNRPE has been installed, is a proper configuration. 
As of JNRPE 2.0, 2 different configuration format are supported: **ini** (must have _ini_ extension) and **XML** 
(must have _xml_ extension).

<!--DOCUSAURUS_CODE_TABS-->

<!-- INI File -->
The INI configuration file is splitted into two main sections:

* **server configuration** : contains the service configuration (binding address/port, accepted client ip, plugin 
installation directory, etc). This section is identified by the label [server]
* **command configuration** : contains the configuration of each command the server can execute. 
A command definition is very simple : just associates a command name and a list of arguments to a plugin name, 
so that you can refer to the plugin with the command name. Moreover, the command definition, configures the 
parameters to pass to the plugin. Many commands with different names can refer to the same plugin. 
This section is identified by the label [commands]

To better understand what that means, let's analyze the configuration file below:

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

* [server] - this means we are starting the server section
* accept-params : true - means we want JNRPE to expand the $ARG?$ macros. If you set this to false, 
then arguments passed by check_nrpe (-a flag) will be ignored.
* bind-address : 127.0.0.1:5666 - means we want JNRPE to listen on the 127.0.0.1 IP at port 5666. 
You can repeat this setting to instruct JNRPE to listen to many addresses/ports. If you want the 
comunication between check_nrpe and JNRPE to be encrypted (check_nrpe default), write the address 
as ‘SSL/IPADDRESS:PORT’:
* bind-address : SSL/127.0.0.1:5666
* plugin-path : /usr/local/jnrpe/plugins - means that in ‘/usr/local/jnrpe/plugins’ JNRPE will 
find all the installed plugins.
* allow-address : 127.0.0.1 - means that JNRPE must accept request coming from 127.0.0.1. You can 
repeat this line to accept requests from many addresses.
* backlog-size : 256 - means that the maximum number of concurrent connection must be limited to 256.
* read-timeout : 5 - means that the connection will be closed if the client do not writes any data in 5 seconds.
* write-timeout : 60 - means that the connection will be closed if the server do not writes any data for 
more than 60 seconds.
* [commands] - means that the command section is starting Follows the command definition. The format is very simple:
    ```    
    [COMMAND_NAME] : [PLUGIN_NAME] [COMMAND_LINE]
    ```
  Where:
  * [COMMAND_NAME] is the name you want to associate to this command definition
  * [PLUGIN_NAME] is the plugin that must is execute the command
  * COMMAND_LINE is the command line to be passed to the plugin. Any $ARG?$ macro will be replaced with the 
  arguments received by check_nrpe (-a flag) if accept-params is true.
<!-- XML File -->
The XML configuration file iss composed of two main sections:

* **server section** : contains the service configuration (binding address/port, accepted client ip, plugin 
installation directory, etc)
* **commands section** : contains the configuration of each command the server can execute. A command definition 
is very simple : just associates a command name and a list of arguments to a plugin name, so that you can refer 
to the plugin with the command name. Moreover, the command definition, configures the parameters to pass to the 
plugin. Many commands with different names can refer to the same plugin.

Let's explain with an example:
```xml
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
```

* \<server> - means we are starting the server section
* accept-params : true - means we want JNRPE to expand the $ARG?$ macros. If you set this to false, then parameters 
passed by check_nrpe will be ignored.
* backlog-size : 256 - means that the maximum number of concurrent connection must be limited to 256.
* read-timeout : 5 - means that the connection will be closed if the client do not writes any data in 5 seconds.
* write-timeout : 60 - means that the connection will be closed if the server do not writes any data for more than 
60 seconds.
* \<bind address=“127.0.0.1:5666” SSL=“false”/> - means we want JNRPE to listen on the 127.0.0.1 IP at port 5666. 
You can repeat this setting more than one time to instruct JNRPE to listen to many address. * If you want the 
communication between check_nrpe and JNRPE to be encrypted (check_nrpe default) set SSL to ‘true’.
* \<plugin path=“/usr/local/jnrpe/plugins”/> - means that in ‘/usr/local/jnrpe/plugins’ JNRPE will find all the 
installed plugins.
* \<allow ip=“127.0.0.1”/> - means that JNRPE must accept request coming from 127.0.0.1. You can repeat this line 
to accept requests from many addresses.
* \<commands> - means that the command section is starting Follows the command definition. The format is very simple:
    ```xml
    <command name="[COMMAND_NAME]" plugin_name="[PLUGIN_NAME]">
       <arg name="[ARG_NAME]"  value="[ARG_VALUE]" />
       ...
    <command/>
    ```
  Where:
  * [COMMAND_NAME] is the name you want to associate to this command definition
  * [PLUGIN_NAME] is the plugin that must is execute the command
  * [ARG_NAME] is an argument to pass to the plugin. The line must be repeated for every argument you want to pass to the plugin.
  * [ARG_VALUE] is the value of the argument. It can be a constant of an $ARG?$ variable. $ARG?$ variables 
  will be replaced with the arguments received by check_nrpe if accept-params is true. All the [ARG_NAME] 
  and all the [ARG_VALUE] will compose the command line to pass to the plugin.
<!--END_DOCUSAURUS_CODE_TABS-->

The two configured command could be invoked like this:
```bash
check_nrpe -n -H 127.0.0.1 -c check_disk_E -a ':10!:5'
check_nrpe -n -H 127.0.0.1 -c check_disk_C -a ':20!:10'
```