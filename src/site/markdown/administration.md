#JNRPE Administration

JNRPE software comprises three different components:

 * The JNRPE Server : this is the piece of software that listens for requests from Nagios to execute a given command. 
 * The JNRPE Plugins : this package contains all the plugin already bundled with JNRPE. It is optional, however take in mind that JNRPE needs plugins to be useful.
 * The JNRPE Client : it is a Java implementation of check_nrpe and it can be used to invoke commands on JNRPE or [NRPE](http://exchange.nagios.org/directory/Addons/Monitoring-Agents/NRPE--2D-Nagios-Remote-Plugin-Executor/details).