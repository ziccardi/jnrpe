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
package it.jnrpe.plugins.test.it;

import it.jnrpe.ReturnValue;
import it.jnrpe.Status;
import it.jnrpe.client.JNRPEClient;
import it.jnrpe.client.JNRPEClientException;
import it.jnrpe.commands.CommandDefinition;
import it.jnrpe.commands.CommandOption;
import it.jnrpe.commands.CommandRepository;
import it.jnrpe.plugins.PluginDefinition;
import it.jnrpe.utils.PluginRepositoryUtil;

import org.testng.Assert;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import com.mysql.jdbc.Driver;

public class CheckMySQLPluginIT implements ITConstants {

    @BeforeTest
    public void setup() throws Exception {
        ClassLoader cl = CheckMySQLPluginIT.class.getClassLoader();

        PluginDefinition checkFile =
                PluginRepositoryUtil.parseXmlPluginDefinition(cl,
                        cl.getResourceAsStream("check_mysql_plugin.xml"));

        ITSetup.getPluginRepository().addPluginDefinition(checkFile);

        CommandRepository cr = ITSetup.getCommandRepository();

        cr.addCommandDefinition(new CommandDefinition("CHECK_MYSQL_SLAVE",
                "CHECK_MYSQL")
        .addArgument(new CommandOption("hostname", "$ARG1$"))
        .addArgument(new CommandOption("port", "$ARG2$"))
        .addArgument(new CommandOption("database", "$ARG3$"))
        .addArgument(new CommandOption("user", "$ARG4$"))
        .addArgument(new CommandOption("password", "$ARG5$"))
        .addArgument(new CommandOption("warning", "$ARG6$"))
        .addArgument(new CommandOption("critical", "$ARG7$"))
        .addArgument(new CommandOption("check-slave")));

        cr.addCommandDefinition(new CommandDefinition("CHECK_MYSQL",
                "CHECK_MYSQL")
        .addArgument(new CommandOption("hostname", "$ARG1$"))
        .addArgument(new CommandOption("port", "$ARG2$"))
        .addArgument(new CommandOption("database", "$ARG3$"))
        .addArgument(new CommandOption("user", "$ARG4$"))
        .addArgument(new CommandOption("password", "$ARG5$"))
        .addArgument(new CommandOption("warning", "$ARG6$"))
        .addArgument(new CommandOption("critical", "$ARG7$")));
    }

    @Test
    public void checkConnectionOk() throws JNRPEClientException {
        Driver.setConnectionTime(0);
        JNRPEClient client = new JNRPEClient(BIND_ADDRESS, JNRPE_PORT, false);
        client.setTimeout(120);
        ReturnValue ret =
                client.sendCommand("CHECK_MYSQL", "localhost", "3306",
                        "mockdb", "dbadmin", "dbadminpwd", "3:5", "5:");
        Assert.assertEquals(ret.getStatus(), Status.OK, ret.getMessage());
    }

    @Test
    public void checkConnectionWarning() throws JNRPEClientException {
        try {
            Driver.setConnectionTime(3000);
            Driver.QUERY_TIME = 0;
            JNRPEClient client =
                    new JNRPEClient(BIND_ADDRESS, JNRPE_PORT, false);
            client.setTimeout(120);
            ReturnValue ret =
                    client.sendCommand("CHECK_MYSQL", "localhost", "3306",
                            "mockdb", "dbadmin", "dbadminpwd", "3:5", "5:");
            Assert.assertEquals(ret.getStatus(), Status.WARNING,
                    ret.getMessage());
        } finally {
            Driver.setConnectionTime(0);
        }
    }

    @Test
    public void checkConnectionCritical() throws JNRPEClientException {
        try {
            Driver.setConnectionTime(5000);
            JNRPEClient client =
                    new JNRPEClient(BIND_ADDRESS, JNRPE_PORT, false);
            client.setTimeout(120);
            ReturnValue ret =
                    client.sendCommand("CHECK_MYSQL", "localhost", "3306",
                            "mockdb", "dbadmin", "dbadminpwd", "3:5", "5:");
            Assert.assertEquals(ret.getStatus(), Status.CRITICAL,
                    ret.getMessage());
        } finally {
            Driver.setConnectionTime(0);
        }
    }

    @Test
    public void checkSlavesOk() throws JNRPEClientException {
        JNRPEClient client = new JNRPEClient(BIND_ADDRESS, JNRPE_PORT, false);
        client.setTimeout(120);
        ReturnValue ret =
                client.sendCommand("CHECK_MYSQL_SLAVE", "localhost", "3306",
                        "mockdb", "dbadmin", "dbadminpwd", "5:8", "8:");
        Assert.assertEquals(ret.getStatus(), Status.OK, ret.getMessage());
    }

    @Test
    public void checkSlavesWarning() throws JNRPEClientException {
        Driver.setSlaveStatus(true, true, 6);

        try {
            JNRPEClient client =
                    new JNRPEClient(BIND_ADDRESS, JNRPE_PORT, false);
            client.setTimeout(120);
            ReturnValue ret =
                    client.sendCommand("CHECK_MYSQL_SLAVE", "localhost",
                            "3306", "mockdb", "dbadmin", "dbadminpwd", "5:8",
                            "8:");
            Assert.assertEquals(ret.getStatus(), Status.WARNING,
                    ret.getMessage());
        } finally {
            Driver.setSlaveStatus(true, true, 0);
        }
    }

    @Test
    public void checkSlavesCritical() throws JNRPEClientException {
        Driver.setSlaveStatus(true, true, 10);

        try {
            JNRPEClient client =
                    new JNRPEClient(BIND_ADDRESS, JNRPE_PORT, false);
            client.setTimeout(120);
            ReturnValue ret =
                    client.sendCommand("CHECK_MYSQL_SLAVE", "localhost",
                            "3306", "mockdb", "dbadmin", "dbadminpwd", "5:8",
                            "8:");
            Assert.assertEquals(ret.getStatus(), Status.CRITICAL,
                    ret.getMessage());
        } finally {
            Driver.setSlaveStatus(true, true, 0);
        }
    }

    @Test
    public void checkSlavesNotRunningCritical() throws JNRPEClientException {
        Driver.setSlaveStatus(false, true, 0);

        try {
            JNRPEClient client =
                    new JNRPEClient(BIND_ADDRESS, JNRPE_PORT, false);
            client.setTimeout(120);
            ReturnValue ret =
                    client.sendCommand("CHECK_MYSQL_SLAVE", "localhost",
                            "3306", "mockdb", "dbadmin", "dbadminpwd", "5:8",
                            "8:");
            Assert.assertEquals(ret.getStatus(), Status.CRITICAL,
                    ret.getMessage());
        } finally {
            Driver.setSlaveStatus(true, true, 0);
        }
    }
}
