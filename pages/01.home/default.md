---
title: 'Welcome to JNRPE'
body_classes: 'title-center title-h1h2'
---

JNRPE provides a Java and a [Javascript](https://www.npmjs.com/package/jnrpe) (Node.js) implementation of the [NRPE](https://exchange.nagios.org/directory/Addons/Monitoring-Agents/NRPE--2D-Nagios-Remote-Plugin-Executor/details) server.
The main features of JNRPE are:
* Execute existing plugins: by using JNRPE you won't lose the huge amount of already existing [Nagios](https://www.nagios.org/) plugins
* Write plugins with JAVA or Javascript: while current plugins must be compiled for every platform you need to run them on, JNRPE plugins can be used everywhere without any need to recompile
* Execute the Java/Javascript plugins directly inside JNRPE: no new JVMs will be instantiated to execute java plugins.

## How JNRPE works
![](deployment.png)

By using JNRPE, the execution flow for Nagios plugins will be as follow:
1. Nagios will invoke a check defined as a check_nrpe command.
2. `check_nrpe` will invoke the command on the JNRPE server
3. The JNRPE server will invoke the plugin associated with the requested command
4. The plugin will perform the check. If the plugin is the `NATIVE` check plugin (former `EXEC`), the external executable will be invoked and its output will be returned. This plugin is useful if you need to use a mix of java/javascript and executable plugins since you don’t have to install both JNRPE and NRPE.
5. JNRPE receives the plugin output and returns it to `check_nrpe`
6. `check_nrpe` returns the check result to Nagios

## Getting support
There are several places to obtain support for JNRPE. They are presented here in the order in which you should probably consult them.

[ui-tabs position="top-left" active="0" theme="badges"]
[ui-tab title="Java Version"]

* The documentation is the source of informations about JNRPE.
* The [Help Forum](http://sourceforge.net/p/jnrpe/discussion/730635/) is the place where you can ask for help to the community

[/ui-tab]
[ui-tab title="JavaScript Version"]

* The documentation is the source of informations about JNRPE.
* The [JS-NRPE group](https://groups.google.com/forum/#!forum/js-jnrpe) is the place where you can ask for help to the community

[/ui-tab]
[/ui-tabs]

## How can I Help?
JNRPE is an open source community and welcomes contributions. If you’d like to get involved, please send me an e-mail.

## Donations
If you would like to support the continued development of JNRPE, you can make a donation like 5-10 Euros/US$ or whatever you feel that JNRPE is worth to you. Please note that this is not payment for JNRPE, but an optional donation to the project – JNRPE is always free to use.

Click [here](http://sourceforge.net/donate/index.php?group_id=204486) to make a donation.