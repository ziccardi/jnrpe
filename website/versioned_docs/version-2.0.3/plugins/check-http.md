---
id: version-2.0.3-plugin-checkhttp
title: CHECK_HTTP
original_id: plugin-checkhttp
---

This plugin tests the HTTP service on the specified host. It can test normal (http) and secure (https) servers, follow 
redirects, search for strings and regular expressions, check connection times, and report on certificate expiration times.

Supported parameters are:

* **---hostname/-H <HOSTNAME\>**: Host name or IP Address
* **---port/-p <INTEGER\>**: Port number; default is `80`
* **---url/-u <STRING\>**: URL path to get or post, default is `/`
* **---post/-p <STRING\>**: URL encoded POST data.
* **---warning/-w <WARNING\>**: Response threshold time to result in warning status (seconds).
* **---critical/-c <CRITICAL\>**: Response threshold time to result in warning status (seconds).
* **---timeout/-t <INTEGER\>**: Seconds before connection times out (default: `10`).
* **---header/-k <HEADER\>**: Any other tags to be sent in http header. Use multiple times for additional headers.
* **---useragent/-u <AGENT\>**: User agent http header value.
* **---authorization/-a <AUTH\>**: Username:password on sites using basic authentication.
* **---proxy-authorization/-b <AUTH\>**: Username:password on proxies using basic authentication.
* **---ssl/-S <PORT\>**: Connect via SSL. Defaults to `443` if not provided.
* **---method/-j <METHOD\>**: HTTP method (`GET`, `POST`, `HEAD`, etc)
* **---expect/-e <STRING\>**: Comma-delimited list of strings, at least one of them is expected in the first (status) 
line of the server response (default: `HTTP/1`.).
* **---string/-s <STRING\>**: String to expect in the content
* **--no-body/-N** : Don't wait for document body: stop reading after headers.
* **---content-type/-T <STRING\>**: Specify Content-Type header media type when `POST`ing.
* **--linespan/-l** : Allow regex to span newlines (must precede `-r` or `-R`).
* **---regex/-r <STRING\>**: Search page for regex string.
* **---eregi/-R <STRING\>**: Search page for case-insensitive regex string.
* **--invert-regex/-I**: Return `CRITICAL` if regex found, `OK` if not.
* **---onredirect/-f <STRING\>**: How to handle redirected pages. Options: ok|warning|critical
* **---certificate/-C <CERTIFICATE\>**: Threshold value for the days a certificate has to be valid. Port defaults to 443
 (when this option is used the URL response is not checked.).

## Usage Examples
### Checking web server status
The example checks if the web server is running on port 80.

#### Configuration
<!--DOCUSAURUS_CODE_TABS-->
<!-- INI -->
```text
CHECK_HTTP : CHECK_HTTP --hostname $ARG1$ --warning $ARG3$ --critical $ARG4$
```   
<!-- XML -->
```xml
<command name="CHECK_HTTP" plugin_name="CHECK_HTTP">
  <arg name="hostname"  value="$ARG1$" />
  <arg name="port"  value="$ARG2$" />
  <arg name="warning"  value="$ARG3$" />
  <arg name="critical"  value="$ARG4$" />
</command>
```
<!--END_DOCUSAURUS_CODE_TABS-->

#### Invocation
The following call will throw a critical if the response time is 20 or more seconds and a warning if the response is 10 or more seconds.
```bash
$ check_nrpe -n -H 127.0.0.1 -c CHECK_HTTP -a mydomain.com 80 20: 10: 
```
### Checking ssl certificate status
In the following example we will check the expiry date for a ssl certificate..

**Configuration**
<!--DOCUSAURUS_CODE_TABS-->
<!-- INI -->
```text
CHECK_HTTP : CHECK_HTTP --hostname $ARG1$ --certificate $ARG2$
```
<!-- XML -->
```xml
<command name="CHECK_HTTP" plugin_name="CHECK_HTTP">
  <arg name="hostname"  value="$ARG1$" />
  <arg name="certificate"  value="$ARG3$" />
</command>
```
<!--END_DOCUSAURUS_CODE_TABS-->

**Invocation**

The following will throw a critical if the ssl certificate expiry date falls within the specified range of 30 days or more
```bash
$ check_nrpe -n -H 127.0.0.1 -c CHECK_HTTP -a mydomain.com 80 30:
```