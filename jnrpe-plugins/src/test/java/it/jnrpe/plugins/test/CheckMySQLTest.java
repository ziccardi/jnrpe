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
import it.jnrpe.plugin.mysql.CheckMysql;

import org.testng.annotations.Test;

import com.mysql.jdbc.Driver;

/**
 * Tests the check mysql plugin.
 * 
 * @author Massimiliano Ziccardi
 *
 */
public class CheckMySQLTest {

    @Test
    public void checkConnectionOk() throws JNRPEClientException {
        Driver.setConnectionTime(0);
        
        PluginTester
            .given(new CheckMysql())
                .withOption("hostname", 'h', "localhost")
                .withOption("port", 'p', "3306")
                .withOption("database", 'd', "mockdb")
                .withOption("user", 'u', "dbadmin")
                .withOption("password", 'p', "dbadminpwd")
                .withOption("warning", 'w', "3:5")
                .withOption("critical", 'c', "5:")
            .expect(Status.OK);
        
    }

    @Test
    public void checkConnectionWarning() throws JNRPEClientException {
        
        Driver.setConnectionTime(3000);
        Driver.QUERY_TIME = 0;
        
        PluginTester
            .given(new CheckMysql())
                .withOption("hostname", 'h', "localhost")
                .withOption("port", 'p', "3306")
                .withOption("database", 'd', "mockdb")
                .withOption("user", 'u', "dbadmin")
                .withOption("password", 'p', "dbadminpwd")
                .withOption("warning", 'w', "3:5")
                .withOption("critical", 'c', "5:")
            .expect(Status.WARNING);
        
        Driver.setConnectionTime(0);
    }

    @Test
    public void checkConnectionCritical() throws JNRPEClientException {
        Driver.setConnectionTime(5000);
        
        PluginTester
            .given(new CheckMysql())
                .withOption("hostname", 'h', "localhost")
                .withOption("port", 'p', "3306")
                .withOption("database", 'd', "mockdb")
                .withOption("user", 'u', "dbadmin")
                .withOption("password", 'p', "dbadminpwd")
                .withOption("warning", 'w', "3:5")
                .withOption("critical", 'c', "5:")
            .expect(Status.CRITICAL);
        
        Driver.setConnectionTime(0);
    }

    @Test
    public void checkSlavesOk() throws JNRPEClientException {
        
        PluginTester
            .given(new CheckMysql())
                .withOption("hostname", 'h', "localhost")
                .withOption("port", 'p', "3306")
                .withOption("database", 'd', "mockdb")
                .withOption("user", 'u', "dbadmin")
                .withOption("password", 'p', "dbadminpwd")
                .withOption("warning", 'w', "5:8")
                .withOption("critical", 'c', "8:")
                .withOption("check-slave", 's', null)
            .expect(Status.OK);
        
    }

    @Test
    public void checkSlavesWarning() throws JNRPEClientException {
        
        Driver.setSlaveStatus(true, true, 6);

        PluginTester
            .given(new CheckMysql())
                .withOption("hostname", 'h', "localhost")
                .withOption("port", 'p', "3306")
                .withOption("database", 'd', "mockdb")
                .withOption("user", 'u', "dbadmin")
                .withOption("password", 'p', "dbadminpwd")
                .withOption("warning", 'w', "5:8")
                .withOption("critical", 'c', "8:")
                .withOption("check-slave", 's', null)
            .expect(Status.WARNING);
        
        Driver.setSlaveStatus(true, true, 0);
        
    }

    @Test
    public void checkSlavesCritical() throws JNRPEClientException {
        
        Driver.setSlaveStatus(true, true, 10);

        PluginTester
            .given(new CheckMysql())
                .withOption("hostname", 'h', "localhost")
                .withOption("port", 'p', "3306")
                .withOption("database", 'd', "mockdb")
                .withOption("user", 'u', "dbadmin")
                .withOption("password", 'p', "dbadminpwd")
                .withOption("warning", 'w', "5:8")
                .withOption("critical", 'c', "8:")
                .withOption("check-slave", 's', null)
            .expect(Status.CRITICAL);
    
        Driver.setSlaveStatus(true, true, 0);
            
    }

    @Test
    public void checkSlavesNotRunningCritical() throws JNRPEClientException {
        Driver.setSlaveStatus(false, true, 0);

        PluginTester
            .given(new CheckMysql())
                .withOption("hostname", 'h', "localhost")
                .withOption("port", 'p', "3306")
                .withOption("database", 'd', "mockdb")
                .withOption("user", 'u', "dbadmin")
                .withOption("password", 'p', "dbadminpwd")
                .withOption("warning", 'w', "5:8")
                .withOption("critical", 'c', "8:")
                .withOption("check-slave", 's', null)
            .expect(Status.CRITICAL);
        
        Driver.setSlaveStatus(true, true, 0);
    }
}
