#jcheck_nrpe
 
   jcheck_nrpe can be useful if you want to test your JNRPE configuration and you don't want to compile the check_nrpe sources for your architecture.
   
   It accepts all the same parameters as check_nrpe does and allows you to talk to both JNRPE and NRPE (as with check_nrpe).
 
## Using the command line 
   
   The command line is very simple, and accepts the following parameters:
   
   * -n/--nossl instruct jcheck_nrpe to not use SSL for communication.
   * -u/--unknown instructs jcheck_nrpe to return an unknown status (instead of critical) on connection timeout errors
   * -H/--host tells jcheck_nrpe where to find the JNRPE/NRPE server
   * -p/--port tells jcheck_nrpe which port JNRPE/NRPE is listening to. Defaults to 5666
   * -t/--timeout configure the connection timeout in seconds
   * -c/--command configure the name of the command to be invoked
   * -a/--args The list of arguments to be passed to the JNRPE/NRPE command. All the parameters must separed with a whitespace. This argument must be
   the last one on the command line.
   * -h/--help shows the jcheck_nrpe command line help
  
##Examples

  The following samples assumes that inside JNRPE the following command is configured:

        check_test : TEST --text $ARG1$ --status $ARG2$ 

### Unix sample

        ./jcheck_nrpe.sh -H 127.0.0.1 -t 10 -c check_test -a HELLO WORLD critical
  
### Windows sample

        jcheck_nrpe.bat -H 127.0.0.1 -t 10 -c check_test -a HELLO WORLD critical