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
import it.jnrpe.plugin.CheckPgsql;

import org.postgresql.Driver;
import org.testng.annotations.Test;

public class CheckPGSQLTest {

    @Test
    public void checkIsAliveOk() throws JNRPEClientException {
        
        PluginTester.given(new CheckPgsql())
            .withOption("database", 'd', "mockdb")
            .withOption("hostname", 'H', "localhost")
            .withOption("port", 'P', "5001")
            .withOption("password", 'p', "dbpasswd")
            .withOption("warning", 'w', "5:10")
            .withOption("critical", 'c', "10:")
        .expect(Status.OK);
        
    }

    @Test
    public void checkIsAliveNewOk() throws JNRPEClientException {
        
        PluginTester.given(new CheckPgsql())
            .withOption("database", 'd', "mockdb")
            .withOption("hostname", 'H', "localhost")
            .withOption("port", 'P', "5001")
            .withOption("password", 'p', "dbpasswd")
            .withOption("th", 't', "metric=conn,ok=inf..5,warn=5..10,crit=10..inf,unit=s")
        .expect(Status.OK);
        
    }

    @Test
    public void checkIsAliveWarning() throws JNRPEClientException {
        
        Driver.setConnectionTime(2500);
        
        try {
            PluginTester.given(new CheckPgsql())
                .withOption("database", 'd', "mockdb")
                .withOption("hostname", 'H', "localhost")
                .withOption("port", 'P', "5001")
                .withOption("password", 'p', "dbpasswd")
                .withOption("warning", 'w', "2:5")
                .withOption("critical", 'c', "5:")
            .expect(Status.WARNING);
        } finally {
            Driver.setConnectionTime(0);
        }
        
    }

    @Test
    public void checkIsAliveNewWarning() throws JNRPEClientException {
        
        try {
            Driver.setConnectionTime(3000);
            PluginTester.given(new CheckPgsql())
                .withOption("database", 'd', "mockdb")
                .withOption("hostname", 'H', "localhost")
                .withOption("port", 'P', "5001")
                .withOption("password", 'p', "dbpasswd")
                .withOption("th", 't', "metric=conn,ok=inf..2,warn=2..5,crit=5..inf,unit=s")
            .expect(Status.WARNING);
        } finally {
            Driver.setConnectionTime(0);
        }
        
    }

    @Test
    public void checkIsAliveCritical() throws JNRPEClientException {
        
        Driver.setConnectionTime(5500);
        
        try {
            PluginTester.given(new CheckPgsql())
                .withOption("database", 'd', "mockdb")
                .withOption("hostname", 'H', "localhost")
                .withOption("port", 'P', "5001")
                .withOption("password", 'p', "dbpasswd")
                .withOption("warning", 'w', "2:5")
                .withOption("critical", 'c', "5:")
            .expect(Status.CRITICAL);
        } finally {
            Driver.setConnectionTime(0);
        }
        
    }

    @Test
    public void checkIsAliveNewCritical() throws JNRPEClientException {
        
        try {
            Driver.setConnectionTime(5500);
            PluginTester.given(new CheckPgsql())
                .withOption("database", 'd', "mockdb")
                .withOption("hostname", 'H', "localhost")
                .withOption("port", 'P', "5001")
                .withOption("password", 'p', "dbpasswd")
                .withOption("th", 't', "metric=conn,ok=inf..2,warn=2..5,crit=5..inf,unit=s")
            .expect(Status.CRITICAL);
        } finally {
            Driver.setConnectionTime(0);
        }
        
    }

}
