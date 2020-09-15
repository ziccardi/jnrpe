---
id: version-2.0.3-overview
title: Overview
original_id: overview
---

**JNRPE** provides a Java and a Javascript (Node.js) implementation of the [NRPE](https://exchange.nagios.org/directory/Addons/Monitoring-Agents/NRPE--2D-Nagios-Remote-Plugin-Executor/details) server. The main features of JNRPE are:

* Execute existing plugins: by using JNRPE you won't lose the huge amount of already existing Nagios plugins
* Write plugins with JAVA or Javascript: while current plugins must be compiled for every platform you need to run them on, JNRPE plugins can be used everywhere without any need to recompile
* Execute the Java/Javascript plugins directly inside JNRPE: no new JVMs will be instantiated to execute java plugins.

## How it works

![JNRPE](assets/overview-203.png)

1. [**Nagios**](https://www.nagios.org/) will invoke a check defined as a check_nrpe command.
2. **check_nrpe** will invoke the command on the JNRPE server
3. The **JNRPE server** will invoke the plugin associated with the requested command
4. The plugin will perform the check. If the plugin is the **NATIVE** check plugin (former **EXEC**), the external executable will be invoked and its output will be returned. This plugin is useful if you need to use a mix of java/javascript and executable plugins since you don’t have to install both JNRPE and NRPE.
5. **JNRPE** receives the plugin output and returns it to **check_nrpe**
6. **check_nrpe** returns the check result to **Nagios**
