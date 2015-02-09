#JNRPE Modules

The JNRPE project is composed of many modules that can be classified in three different macro groups:
  * Installation packages
  * Applications
  * Libraries
  * OSGI Bundles
  * Plugins
  
The installation packages, as the name implies, are meant to be used to install JNRPE :

  * jnrpe-debian
  * jnrpe-install
  
The applications are those components that has a graphical or command line interface and can be used as they are:

  * jnrpe-server
  * jcheck_nrpe

The libraries can be used to develop functionalities that uses JNRPE features:
  
  * jnrpe-lib
  * jcheck_nrpe

The OSGI bundles can be installed inside any standard OSGI bundle (as of today, karaf and felix has been tested):

  * jnrpe-osgi-core (jnrpe-osgi)
  * jnrpe-plugins-osgi (jnrpe-osgi) 

The plugins are pieces of software that JNRPE uses to execute the checks:

  * jnrpe-plugins



## The Installation packages
 
### The jnrpe-install project

![jnrpe-lib project](../images/install.png)

This project contains the source of the JNRPE installer. The installer is built using the
izPack library.
  
For more informations about jnrpe-install 
look at the [jnrpe-install documentation](../jnrpe-install/index.html)

## The jnrpe-debian project

This project bundles JNRPE as a debian file (.deb) installable on debian based systems.

## The Applications
 
###The jnrpe-server project

![jnrpe-lib project](../images/jnrpe-server.png)

This project is an implementation based on jnrpe-lib of a clone of the Nagios NRPE server.
The JNRPE Server, embeds the jnrpe-lib library and configures it through a configuration
file. It than attach itself to the event bus and sends all the log events to SLF4j that
in turn, writes them to the log file.
  
For more informations about jnrpe-server and how to use and configurate it, 
look at the {{{../jnrpe-server/index.html}jnrpe-server documentation}}

### The jcheck_nrpe project

This project contains the JAVA implementation of a clone of the check_nrpe Nagios command.
This code can be used as a standalone application (like check_nrpe) or can be embedded inside
your own application to be able to invoke services on JNRPE or NRPE.
  
For more informations about jcheck_nrpe, how to use the command line and how embed it, 
look at the [jcheck_nrpe documentation](../jcheck_nrpe/index.html)

## The libraries
  
### The jnrpe-lib project

![jnrpe-lib project](../images/jnrpe-lib.png)

The jnrpe-lib project is the most important module: it contains the core implementation
of the NRPE protocol and all the classes that allows the user to embed JNRPE funcionalities
inside his own application. jnrpe-server is an example of application that embeds jnrpe-lib.

When a requests arrives to the jnrpe-lib:

1. The network layer will parse the received bytes to create the command invocation 
request (the Command object).
2. The command object will instantiate the plugin needed to execute the command and than 
will execute it passing the configured parameters and the received arguments.
3. The plugin will perform the requested checks and return the result
4. The command will receive the result from the plugin and will return it to the network
layer
5. The network layer will send the answer to the client

In any of the five depicted steps, an event can be sent to the event bus. All the listening
listeners will be able to receive it and react as needed. This is necessary when you need
o receive informations from JNRPE plugins since their invocation is asynchronous. 
 
For more informations about jnrpe-lib and how to use it, look at 
the [jnrpe-lib documentation](../jnrpe-lib/index.html)

###The jcheck_nrpe project

This project contains the JAVA implementation of a clone of the check_nrpe Nagios command.
This code can be used as a standalone application (like check_nrpe) or can be embedded inside
your own application to be able to invoke services on JNRPE or NRPE.
  
For more informations about jcheck_nrpe, how to use the command line and how embed it, 
look at the [jcheck_nrpe documentation](../jcheck_nrpe/index.html)

##The OSGI bundles
   
###The jnrpe-osgi-core project

![jnrpe-osgi-core project](../images/osgi-core.png)

This project packages the jnrpe-lib as a bundle to be used inside OSGI containers
(JBoss, Apache Karaf, Apache Felix, etc.).
This module is a submodule of the jnrpe-osgi project.
  
The JNRPE is configured through the OSGI ConfigAdmin service.
All the plugins are automatically installed through the bundle tracker listener so 
that they can be automatically loaded and unloaded through the bundle loading
system.
All the received log events are logged through the use of the OSGI Log Service.
Asynchronous events can be intercepted by registering to the JNRPE event bus.
    
For more informations about jnrpe-osgi-core and how to use it, look at 
the [jnrpe-osgi-core documentation](../jnrpe-osgi/jnrpe-osgi-core/index.html)
    
###The jnrpe-plugins-osgi project

This project packages the jnrpe-plugins as a bundle to be used inside OSGI containers
(JBoss, Apache Karaf, Apache Felix, etc.)
This module is a submodule of the jnrpe-osgi project.
  
For more informations about jnrpe-plugins-osgi and how to use it, look at 
the [jnrpe-plugins-osgi documentation](../jnrpe-osgi/jnrpe-plugins-osgi/index.html)
  
##The plugins
###The jnrpe-plugins project

This project contains the implementation of all the plugins bundled with JNRPE.
  
For more informations about jnrpe-plugins, all contained plugins and how to use them, 
look at the [jnrpe-plugins documentation](../jnrpe-plugins/index.html)
     
  