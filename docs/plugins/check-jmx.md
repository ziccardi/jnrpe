---
id: plugin-checkjmx
title: CHECK_JMX
---

The CHECK_JMX plugin, is a powerful tool that allows the execution of JMX queries. It supports the following parameters:

* **-U/--url**: JMX URL, for example: `service:jmx:rmi:///jndi/rmi://localhost:1616/jmxrmi`
* **-O/--object**: Object name to be checked, for example, `java.lang:type=Memory`
* **-A/--attribute**: Attribute of the object to be checked, for example, `NonHeapMemoryUsage`
* **-K/--attributeKey**: Attribute key for -A attribute compound data, for example, `used`
* **-I/--infoattribute**: Attribute of the object containing information for text output
* **-J/--infokey**: Attribute key for -I attribute compound data, for example, `used`
* **-w/--warning**: Warning range
* **-c/--critical**: Critical range
* **-u/--username**: monitorRole jmx username
* **-p/--password**: monitorRole jmx password

## Example Usage

The following example shows how to monitor the memory usage of a JBOSS application server.

:::warning
To work with JBOSS version 7.1 or later, the jboss-client.jar must be inside the same directory of the CHECK_JMX plugin JAR file.
:::

### Configuration

<!--DOCUSAURUS_CODE_TABS-->
<!-- INI -->
```bash
CHECK_JBOSS_MEM : CHECK_JMX --url service:jmx:remoting-jmx://$ARG1$ --object java.lang:type=Memory --attribute HeapMemoryUsage -K used --warning $ARG2$ --critical $ARG3$
```
<!-- XML -->
```xml
<command name="CHECK_JBOSS_MEM" plugin_name="CHECK_JMX">
   <arg name="url"  value="service:jmx:remoting-jmx://$ARG1$" />
   <arg name="object"  value="java.lang:type=Memory" />
   <arg name="attribute"  value="HeapMemoryUsage" />
   <arg name="warning"  value="$ARG2$" />
   <arg name="critical"  value="$ARG3$" />
</command>
```
<!--END_DOCUSAURUS_CODE_TABS-->

:::warning
The `url` value used in this example works only with JBOSS version 7.1 or newer. For older versions, please refer to the jboss manual.
:::

Any of the parameters ($ARG?$ macros) could be hardcoded inside the JNRPE configuration.

### Invocation

With the following invocation example we will rise a warning if the memory is beyond 50.000.000 bytes and a critical if it is beyond 70.000.000 bytes.

```bash
check_nrpe -n -H 127.0.0.1 -c CHECK_JBOSS_MEM -a localhost:9999 50000000 70000000
```