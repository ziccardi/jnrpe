---
id: overview
title: Overview
---

**JNRPE** provides a Java and a Javascript (Node.js) implementation of the [NRPE](https://exchange.nagios.org/directory/Addons/Monitoring-Agents/NRPE--2D-Nagios-Remote-Plugin-Executor/details) server. The main features of JNRPE are:

* Execute existing plugins: by using JNRPE you won't lose the huge amount of already existing Nagios plugins
* Write plugins with JAVA or Javascript: while current plugins must be compiled for every platform you need to run them on, JNRPE plugins can be used everywhere without any need to recompile
* Execute the Java plugins directly inside JNRPE: no new JVMs will be instantiated to execute java plugins.

## How it works
![JNRPE Overview](assets/overview.png)
1. [**Nagios**](https://www.nagios.org/) will invoke a check defined as a _check_nrpe_ command.
2. **check_nrpe** will invoke the command on the JNRPE server
3. The **Socket Listener** (that implements the NRPE protocol) will parse and validate the received packet. If the packet is valid,
it will ask the **executor** to execute the received command.
4. The **executor** will ask the **Command Registry** to retrieve the command definition for the requested command
5. After receiving the command, the **executor** will ask the command definition to create a **COMMAND INSTANCE** passing 
to the command definition all the received parameters. A **COMMAND INSTANCE** will contain teh requested plugin and all the parameters (replacing the $ARGx$ macros if needed). 
6. When a **COMMAND INSTANCE** is received, the **executor** will execute the command instance and return the result to the **Socket Listener**.
7. The Socket Listener will then create a NRPE packet with the received result and will return that to **check_nrpe**.
8. **check_nrpe** returns the check result to **Nagios**

### Involved components
* [**Nagios**](https://www.nagios.org/): this is your Nagios server
* **check_nrpe**: this is a nagios plugin that implements the NRPE protocol
* JNRPE: the Java implementation of NRPE. This is composed by a number of submodules:
    * **SERVER**. This module is responsible of receive requests and return responses
    * **Config provider**. This module is responsible of returning the JNRPE configuration.
      Currently only file based `yaml` provider is supported, but other modules to read the configuration
      from other sources/format can be easily added.
    * **Network Listener**. This module listen on the network for NRPE requests and routes received
      requests to the JNRPE SERVER module. More than one Network Listener module can run at the same time.
      For example, we could have both `NRPE Socket` and `REST` listeners active at the same time.
      In the current version, only `NRPE Socket` is implemented.
    * **EXECUTOR**. This module is invoked to execute the request received. It relies on the Command Registry
      to retrieve all the details needed to satisfy the request (plugins, parameters, etc.)
