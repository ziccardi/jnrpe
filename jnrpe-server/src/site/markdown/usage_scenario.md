#Usage Scenario
 
![JNRPE deployment.](images/usage_scenario.png)

   You will want to use JNRPE when at least one of this events occurs:
   
   * You need to monitor resources that can't be reached remotely
   * You need to monitor a non windows/linux machine
   * You need to monitor many different machines with different operating systems
     and you want to use the same software/installation/configuration on every 
     machine
   * You need to run a custom check that you developed with Java and you don't want
     a JVM to be started on each check
   * You don't want to bother compiling software from source to get it running on 
     your machine
   
   In all this cases, JNRPE can lend you an hand.   