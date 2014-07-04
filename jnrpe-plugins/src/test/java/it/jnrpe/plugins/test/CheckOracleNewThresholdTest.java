/*******************************************************************************
 * Copyright (c) 2007, 2014 Massimiliano Ziccardi
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *******************************************************************************/
package it.jnrpe.plugins.test;

import it.jnrpe.Status;
import it.jnrpe.client.JNRPEClientException;
import it.jnrpe.plugin.CCheckOracle;
import oracle.jdbc.driver.OracleDriver;

import org.testng.annotations.Test;

public class CheckOracleNewThresholdTest {
    
    @Test
    public void checkIsAliveOk() throws JNRPEClientException {
        PluginTester.given(new CCheckOracle())
            .withOption("username", 'u', "scott")
            .withOption("password", 'p', "tiger")
            .withOption("db", 'd', "mockdb")
            .withOption("server", 's', "127.0.0.1")
            .withOption("th", 't', "metric=conn,ok=0..10,warn=10..20,crit=20..inf,unit=s")
            .withOption("alive", 'a', null)
            .expect(Status.OK);
    }

    @Test
    public void checkIsAliveWarning() throws JNRPEClientException {
        
        OracleDriver.QUERY_TIME = 4000;
        
        PluginTester.given(new CCheckOracle())
            .withOption("username", 'u', "scott")
            .withOption("password", 'p', "tiger")
            .withOption("db", 'd', "mockdb")
            .withOption("server", 's', "127.0.0.1")
            .withOption("th", 't', "metric=conn,ok=1..3,warn=3..5,crit=5..inf,unit=s")
            .withOption("alive", 'a', null)
            .expect(Status.WARNING);

        OracleDriver.QUERY_TIME = 0;
        
    }

    @Test
    public void checkIsAliveCritical() throws JNRPEClientException {

        OracleDriver.QUERY_TIME = 6000;

        PluginTester.given(new CCheckOracle())
            .withOption("username", 'u', "scott")
            .withOption("password", 'p', "tiger")
            .withOption("db", 'd', "mockdb")
            .withOption("server", 's', "127.0.0.1")
            .withOption("th", 't', "metric=conn,ok=1..3,warn=3..5,crit=5..inf,unit=s")
            .withOption("alive", 'a', null)
            .expect(Status.CRITICAL);
            
       OracleDriver.QUERY_TIME = 0;
       
    }

    @Test
    public void checkIsAliveKo() throws JNRPEClientException {
        
        PluginTester.given(new CCheckOracle())
            .withOption("username", 'u', "scott")
            .withOption("password", 'p', "tiger")
            .withOption("db", 'd', "mockdbko")
            .withOption("server", 's', "127.0.0.1")
            .withOption("th", 't', "metric=conn,ok=0..10,warn=10..20,crit=20..inf,unit=s")
            .withOption("alive", 'a', null)
            .expect(Status.CRITICAL);
        
    }
}
