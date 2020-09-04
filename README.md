# Build the software
```bash
# gradle build
```
# Create a distribution

```bash
# gradle distZip
```

# Run the software

```bash
# mkdir dist
# unzip ../server-module/build/distributions/server-module-1.0-SNAPSHOT.zip 
# cd server-module-1.0-SNAPSHOT
# java --module-path lib -m jnrpe.server.module/it.jnrpe.server.Server
```

If you want to install the bundled plugins:

```bash
cp ../../plugins-module/build/libs/plugins-module-1.0-SNAPSHOT.jar lib
```
