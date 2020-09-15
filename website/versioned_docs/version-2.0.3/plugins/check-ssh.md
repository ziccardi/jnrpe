---
id: version-2.0.3-plugin-checkssh
title: CHECK_SSH
original_id: plugin-checkssh
---

This plugin checks connections on a remote (or local) ssh host.

Supported parameters are :

* **---hostname/-h <HOSTNAME\>**: IP or hostname address of ssh server.
* **---username/-u <USERNAME\>**: Username
* **---port/-p <PORT\>**: Port, default is 22
* **---password/-P <PASSWORD\>**: Password
* **---timeout/-t <TIMEOUT\>**: Timeout in seconds
## Usage Example
We will check the connection to an ssh server. We use the following INI configuration style:

### Configuration
<!--DOCUSAURUS_CODE_TABS-->
<!-- INI -->
```ini
check_ssh : CHECK_SSH --username $ARG1$ --hostname $ARG2$ --port $ARG3$ --password $ARG4$
```
<!-- XML -->
```xml
<command name="check_ssh" plugin_name="CHECK_SSH">
   <arg name="username" value="$ARG1$" />  
   <arg name="hostname" value="$ARG2$" />  
   <arg name="port" value="$ARG3$" />  
   <arg name="password" value="$ARG4$" />  
</command>
```
<!--END_DOCUSAURUS_CODE_TABS-->

### Invocation
The following will throw a critical if the connection fails, OK otherwise.
```bash
$ check_nrpe -n -H my.jnrpe.server -c CHECK_SSH -a  -u ssh_username -h myhostname.com -p 22 -P ssh_password
```