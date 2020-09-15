---
id: version-2.0.3-get-jnrpe
title: Getting JNRPE
original_id: get-jnrpe
---

JNRPE can be downloaded both as an installer and as source code:

* [JNRPE Installer](https://sourceforge.net/projects/jnrpe/files/install/jnrpe-server-install-2.0.5/jnrpe-server-install-2.0.5.jar/download): 
a graphical multiplatform interface that will guide during the whole installation.
* [Source Code](https://github.com/ziccardi/jnrpe/tree/2.0.3)

## Compiling the sources

1. Unzip the source archive somewhere
2. Move inside the root of the extracted archive
3. Run `mvn clean install`

:::important
To compile the source code you need to have a JVM 1.5+ and maven already installed.
:::
