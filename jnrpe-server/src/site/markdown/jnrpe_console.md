#The JNRPE console

  As of version 2.0.3-RC4 JNRPE gives you a simple way to test plugins and
  commands through the use of the JNRPE interactive console.
  
  To run JNRPE in console mode, use the --interactive parameter:
  
        ./jnrpe --conf ../etc/jnrpe.ini --interactive
        Listening on 127.0.0.1:5666
        JNRPE> 
  
  JNRPE is now ready to receive your commands.
  
## Getting help

  If you issue 'help' without any parameter, you sill get the list of
  available commands.
  
        JNRPE> help
          * command:check_test
          * exit
          * help
          * plugin:CHECK_DISK
          * plugin:CHECK_FILE

  Now, if you need help about any of the listed command, you can run:
  
        help [command_name]
  
  where command_name can be any of the listed commands:
  
        JNRPE> help plugin:check_disk
        Command Line: 
          plugin:CHECK_DISK  [-p <path> -w <range> -c <range> -T <arg>] 
                                              
        Usage:
          -p|-w|-c|-T                                                                   
          -p (--path) path         The path to check                                  
          -w (--warning) range     The used space warning threshold (percent)         
          -c (--critical) range    The used space critical threshold (percent)        
          -T (--th) arg            Configure a threshold. Format :                    
                                   metric={metric},ok={range},warn={range},crit={range
                                   },unit={unit},prefix={SI prefix}  
  
  Be aware that the console is case insensitive: plugin:check_disk is the same
  as PLUGIN:CHECK_DISK. 
  
  If you want to run the check_disk plugin to test some parameters, you can 
  issue, for example:
  
        JNRPE> plugin:check_disk -p /tmp -w 20 -c 10
        CHECK_DISK : CRITICAL - Used: 246678 MB(89%) Free: 28967 MB(10%)|freepct=10.000000;20;10;0.000000;100.000000 

  The same can be done with the commands:
  
        JNRPE> command:check_test HELLO
        TEST : HELLO
