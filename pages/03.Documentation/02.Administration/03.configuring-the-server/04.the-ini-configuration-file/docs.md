---
title: 'Configuring Using the INI file'
taxonomy:
    category: docs
---

The INI configuration file is splitted into two main sections:

* _server configuration_ : contains the service configuration (binding address/port, accepted client ip, plugin installation directory, etc). This section is identified by the label [server]
* _command configuration_ : contains the configuration of each command the server can execute. A command definition is very simple : just associates a command name and a list of arguments to a plugin name, so that you can refer to the plugin with the command name. Moreover, the command definition, configures the parameters to pass to the plugin. Many commands with different names can refer to the same plugin. This section is identified by the label [commands]

Let’s analyze the following configuration:

````
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
````

* `[server]` - this means we are starting the server section
* `accept-params : true` - means we want JNRPE to expand the $ARG?$ macros. If you set this to false, than arguments passed by check_nrpe (-a flag) will be ignored.
* `bind-address : 127.0.0.1:5666` - means we want JNRPE to listen on the 127.0.0.1 IP at port 5666. You can repeat this setting to instruct JNRPE to listen to many addresses/ports. If you want the comunication between check_nrpe and JNRPE to be encrypted (check_nrpe default), write the address as ‘SSL/IPADDRESS:PORT’:
* `bind-address` : SSL/127.0.0.1:5666
* `plugin-path` : /usr/local/jnrpe/plugins - means that in ‘/usr/local/jnrpe/plugins’ JNRPE will find all the installed plugins.
* `allow-address` : 127.0.0.1 - means that JNRPE must accept request coming from 127.0.0.1. You can repeat this line to accept requests from many addresses.
* `backlog-size` : 256 - means that the maximum number of concurrent connection must be limited to 256.
* `read-timeout` : 5 - means that the connection will be closed if the client do not writes any data in 5 seconds.
* `write-timeout` : 60 - means that the connection will be closed if the server do not writes any data for more than 60 seconds.
* `[commands]` - means that the command section is starting Follows the command definition. The format is very simple:
```
[COMMAND_NAME] : [PLUGIN_NAME] [COMMAND_LINE]
```
Where:
    * [COMMAND_NAME] is the name you want to associate to this command definition
    * [PLUGIN_NAME] is the plugin that must is execute the command
    * COMMAND_LINE is the command line to be passed to the plugin. Any `$ARG?$` macro will be replaced with the arguments received by `check_nrpe` (`-a` flag) if `accept-params` is _true_.

The two configured command could be invoked like this:

```
check_nrpe -n -H 127.0.0.1 -c check_disk_E -a ':10!:5'
check_nrpe -n -H 127.0.0.1 -c check_disk_C -a ':20!:10'
```