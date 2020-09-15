---
id: version-2.0.3-plugin-test
title: TEST
original_id: plugin-test
---

As the name implies, it is just a test plugin that returns a passed in string and a passed in status.
It can optionally wait a specified delay before returning.

Supported parameters are:
* **--text/-t <text\>**: The text to be returned
* **--status/-s <status\>**: The status to return (ok, warning, critical, unknown) - defaults to "OK"
* **--delay/-d <seconds\>**: The number of seconds to delay (defaults to 0)

## Usage Example

### Configuration

<!--DOCUSAURUS_CODE_TABS-->
<!-- INI -->
```text
check_test : TEST --text $ARG1$ --status $ARG2$ --delay $ARG3$
```
<!-- XML -->
```xml
<command name="test" plugin_name="TEST">
  <arg name="text" value="$ARG1$"/>
  <arg name="status" value="$ARG2$"/>
  <arg name="delay" value="$ARG3$"/>
</command>
```
<!--END_DOCUSAURUS_CODE_TABS-->

Any of the parameters (`$ARG?$` macros) could be hardcoded inside the JNRPE configuration.

### Invocation

With the command below, we will ask to the `check_test` command to return the message `Hello World`, with status `critical`.
The resul will be returned after 3 second.

```bash
$ check_nrpe -n -H myjnrpeserver -c check_test -a 'Hello World!critical!3'
```
