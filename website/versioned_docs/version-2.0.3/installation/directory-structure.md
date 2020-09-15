---
id: version-2.0.3-directory-structure
title: The JNRPE Directory Structure
original_id: directory-structure
---

```
JNRPE
    |- bin              ----> Contains the script to be used to run JNRPE
    |- etc              ----> Contains the JNRPE configuration files
    |- lib              ----> Contains the libraries required by JNRPE
    |- plugins          ----> Contains all the plugin packages
    |     |- base       ----> Contains the plugin package bundled with JNRPE
    |- Uninstaller      ----> Contains the generated uninstaller
    |- wrapper          ----> Contains the YAJSW binaries
          |- etc        ----> Contains the YAJSW configuration
          |- lib        ----> Contains the YAJSW dependencies
```

On windows you will find a Service called JNRPE Server, while on unix you will find a script named jnrpe 
inside the `/etc/init.d` directory.