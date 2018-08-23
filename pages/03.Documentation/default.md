---
title: 'JNRPE Documentation'
---

JNRPE is composed by 3 different piece of software that will interact together to return you the expected output:

* The **[JNRPE Server](./administration)**: it is the main component. Listens for requests from Nagios, executes the received command and returns the result
* The **JNRPE Plugins** : this package contains all the plugin already bundled with JNRPE. It is optional, however bear in mind that JNRPE needs plugins to be useful.
* The **JNRPE Client** : it is a Java implementation of `check_nrpe` and it can be used to invoke commands on `JNRPE` or `NRPE`. It is optional and the native `check_nrpe` provided by `NRPE` can be used as well.