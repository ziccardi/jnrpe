---
id: version-2.0.3-plugin-checkping
title: CHECK_PING
original_id: plugin-checkping
---

Use ping to check connection statistics for a remote host.

Supported parameters are :

* **---hostname/-h <HOST\>**: host to ping
* **---warning/-w <THRESHOLD\>**: warning threshold
* **---critical/-c <THRESHOLD\>**: critical threshold
* **---packets/-p <PACKETS\>**: number of ICMP ECHO packets to send (Default: 5)
* **---timeout/-t <SECONDS\>**: Seconds before connection times out (default: 10)
* **---use-ipv4/-4**: Use IPv4 connection
* **---use-ipv6/-6**: Use IPv6 connection

`THRESHOLD` is `[rta],[pl]%` where `[rta]` is the _round trip average travel time_ (ms) which triggers a `WARNING` or `CRITICAL` state, and `[pl]` is the _percentage of packet loss_ to trigger an alarm state.

## Usage Example
In the following example, we will ping a hostname and check some connection stats, using the following INI configuration style

### Configuration
<!--DOCUSAURUS_CODE_TABS-->
<!-- INI -->
```text
check_ping : CHECK_PING --hostname $ARG1$ --warning $ARG2$ 
``` 
<!-- XML -->
```xml
<command name="check_ping" plugin_name="CHECK_PING">
   <arg name="hostname" value="$ARG1$" />  
   <arg name="warning" value="$ARG2$" />  
</command>
```
<!--END_DOCUSAURUS_CODE_TABS-->

### Invocation

The following invocation example will ping a particular host and throw a warning if the percentage of packet loss is more than 50%
```bash
$ check_nrpe -n -H my.jnrpe.server -c check_ping -a myhostname ,50%:
```