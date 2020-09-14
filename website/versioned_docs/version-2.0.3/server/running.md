---
id: version-2.0.3-run-jnrpe
title: Running JNRPE
original_id: run-jnrpe
---

The simplest command to run JNRPE is:

<!--DOCUSAURUS_CODE_TABS-->

<!-- UNIX -->
```bash
cd $JNRPE_HOME/bin
./jnrpe -c ../etc/jnrpe.ini
```

<!-- WINDOWS -->
```bash
cd %JNRPE_HOME%\bin
jnrpe -c ..\etc\jnrpe.ini
```


<!--END_DOCUSAURUS_CODE_TABS-->

:::important
The `JNRPE_HOME` environment variable must contain the path where JNRPE has been installed
:::