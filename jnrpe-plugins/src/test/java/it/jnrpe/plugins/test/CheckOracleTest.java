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

import org.testng.annotations.Test;

public class CheckOracleTest {

    @Test
    public void checkIsAliveOk() throws JNRPEClientException {
        
        PluginTester.given(new CCheckOracle())
            .withOption("username", 'u', "scott")
            .withOption("password", 'p', "tiger")
            .withOption("db", 'd', "mockdb")
            .withOption("server", 's', "127.0.0.1")
            .withOption("alive", 'a', null)
            .expect(Status.OK);
        
    }
    
    @Test
    public void checkTablespaceOk() throws JNRPEClientException {

        PluginTester.given(new CCheckOracle())
            .withOption("username", 'u', "scott")
            .withOption("password", 'p', "tiger")
            .withOption("db", 'd', "mockdb")
            .withOption("server", 's', "127.0.0.1")
            .withOption("tablespace", 't', "system")
            .withOption("warning", 'w', "70:")
            .withOption("critical", 'c', "80:")
            .expect(Status.OK);
        
    }

    @Test
    public void checkTablespaceWarning() throws JNRPEClientException {
        
        PluginTester.given(new CCheckOracle())
            .withOption("username", 'u', "scott")
            .withOption("password", 'p', "tiger")
            .withOption("db", 'd', "mockdb")
            .withOption("server", 's', "127.0.0.1")
            .withOption("tablespace", 't', "user")
            .withOption("warning", 'w', "70:")
            .withOption("critical", 'c', "80:")
            .expect(Status.WARNING);

    }
    
    @Test
    public void checkTablespaceCritical() throws JNRPEClientException {

        PluginTester.given(new CCheckOracle())
            .withOption("username", 'u', "scott")
            .withOption("password", 'p', "tiger")
            .withOption("db", 'd', "mockdb")
            .withOption("server", 's', "127.0.0.1")
            .withOption("tablespace", 't', "temp")
            .withOption("warning", 'w', "70:")
            .withOption("critical", 'c', "80:")
            .expect(Status.CRITICAL);
        
    }
}
