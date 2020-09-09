> :warning: **This branch is maintenance only** I'm doing a full rewrite of JNRPE using Java 9 modules that can be found in the [VERSION_300](https://github.com/ziccardi/jnrpe/tree/VERSION_300) branch

# JNRPE - Java Nagios Remote Plugin Executor

JNRPE is a java porting of the [Nagios NRPE addon](https://github.com/NagiosEnterprises/nrpe).

The project is divided into a set of modules:
* The JNRPE Server: the java equivalent of the NRPE server
* The jcheck_nrpe: the java implementation of the `check_nrpe` command
* The JNRPE Library: the core of the implementation as a java library. 
This library can be embedded into your own application to give that the ability
to be queried by Nagios.
* The JNRPE Plugins: a library containing all the JNRPE provided plugins.
Other plugins can be easily added.
* The JNRPE install: a graphical installer package powered by [IzPack](http://izpack.org).

## Where can I get the latest release?

### JNRPE Libraries
The easiest way is to pull it from the central maven repositories

#### jnrpe-lib
```xml
<dependency>
  <groupId>net.sf.jnrpe</groupId>
  <artifactId>jnrpe-lib</artifactId>
  <version>2.0.5</version>
</dependency>

```

### jcheck_nrpe
```xml
<dependency>
  <groupId>net.sf.jnrpe</groupId>
  <artifactId>jcheck_nrpe</artifactId>
  <version>2.0.5</version>
</dependency>
```
