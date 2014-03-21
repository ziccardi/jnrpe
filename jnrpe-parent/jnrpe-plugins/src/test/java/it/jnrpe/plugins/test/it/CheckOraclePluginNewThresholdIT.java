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
import it.jnrpe.plugin.CCheckOracle;
import it.jnrpe.plugins.PluginDefinition;
import it.jnrpe.utils.PluginRepositoryUtil;
import oracle.jdbc.driver.OracleDriver;

import org.testng.Assert;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

public class CheckOraclePluginNewThresholdIT implements ITConstants {

    @BeforeTest
    public void setup() throws Exception {
        ClassLoader cl = CCheckOracle.class.getClassLoader();

//    metric={metric},ok={range},warn={range},crit={range},unit={unit},prefix={SI prefix}


        PluginDefinition checkFile =
                PluginRepositoryUtil.parseXmlPluginDefinition(cl,
                        cl.getResourceAsStream("check_oracle_plugin.xml"));

        ITSetup.getPluginRepository().addPluginDefinition(checkFile);

        CommandRepository cr = ITSetup.getCommandRepository();

        cr.addCommandDefinition(new CommandDefinition("CHECK_ORACLE_ALIVE_NEW",
                "CHECK_ORACLE")
                .addArgument(new CommandOption("username", "$ARG1$"))
                .addArgument(new CommandOption("password", "$ARG2$"))
                .addArgument(new CommandOption("db", "$ARG3$"))
                .addArgument(new CommandOption("server", "$ARG4$"))
                .addArgument(new CommandOption("th", "metric=conn,ok=$ARG5$,warn=$ARG6$,crit=$ARG7$,unit=s"))
                .addArgument(new CommandOption("alive"))
                );

        cr.addCommandDefinition(new CommandDefinition("CHECK_TABLESPACE",
                "CHECK_ORACLE")
                .addArgument(new CommandOption("username", "$ARG1$"))
                .addArgument(new CommandOption("password", "$ARG2$"))
                .addArgument(new CommandOption("db", "$ARG3$"))
                .addArgument(new CommandOption("server", "$ARG4$"))
                .addArgument(new CommandOption("tablespace", "$ARG5$"))
                .addArgument(new CommandOption("warning", "70:"))
                .addArgument(new CommandOption("critical", "80:")));

    }

    @Test
    public void checkIsAliveOk() throws JNRPEClientException {
        JNRPEClient client = new JNRPEClient(BIND_ADDRESS, JNRPE_PORT, false);
        client.setTimeout(120);
        ReturnValue ret =
                client.sendCommand("CHECK_ORACLE_ALIVE_NEW", "scott", "tiger",
                        "mockdb", "127.0.0.1", "0..10", "10..20", "20..inf");
        Assert.assertEquals(ret.getStatus(), Status.OK, ret.getMessage());
    }

    @Test
    public void checkIsAliveWarning() throws JNRPEClientException {
        JNRPEClient client = new JNRPEClient(BIND_ADDRESS, JNRPE_PORT, false);
        client.setTimeout(120);

        OracleDriver.QUERY_TIME = 4000;

        ReturnValue ret =
                client.sendCommand("CHECK_ORACLE_ALIVE_NEW", "scott", "tiger",
                        "mockdb", "127.0.0.1", "1..3", "3..5", "5..inf");
        OracleDriver.QUERY_TIME = 0;
        Assert.assertEquals(ret.getStatus(), Status.WARNING, ret.getMessage());
    }

    @Test
    public void checkIsAliveCritical() throws JNRPEClientException {
        JNRPEClient client = new JNRPEClient(BIND_ADDRESS, JNRPE_PORT, false);
        client.setTimeout(120);

        OracleDriver.QUERY_TIME = 6000;

        ReturnValue ret =
                client.sendCommand("CHECK_ORACLE_ALIVE_NEW", "scott", "tiger",
                        "mockdb", "127.0.0.1", "1..3", "3..5", "5..inf");
        OracleDriver.QUERY_TIME = 0;
        Assert.assertEquals(ret.getStatus(), Status.CRITICAL, ret.getMessage());
    }

    @Test
    public void checkIsAliveKo() throws JNRPEClientException {
        JNRPEClient client = new JNRPEClient(BIND_ADDRESS, JNRPE_PORT, false);
        client.setTimeout(120);
        ReturnValue ret =
                client.sendCommand("CHECK_ORACLE_ALIVE_NEW", "scott", "tiger",
                        "mockdbko", "127.0.0.1", "0..10", "10..20", "20..inf");
        Assert.assertEquals(ret.getStatus(), Status.CRITICAL, ret.getMessage());
    }

//    @Test
//    public void checkTablespaceOk() throws JNRPEClientException {
//        // <command name="check_oracle_ts" plugin_name="CHECK_ORACLE"
//        // params="--username $ARG1$ --password $ARG2$ -db $ARG3$
//        // --server $ARG4$ --tablespace $ARG5$ -w 70 -c 80"/>
//        //
//        // Example invocation:
//        // ./check_nrpe -t 20 -n -H myjnrpeserver -c check_oracle_ts -a
//        // 'username!password!SID!server!tablespacename'
//
//        JNRPEClient client = new JNRPEClient(BIND_ADDRESS, JNRPE_PORT, false);
//        client.setTimeout(120);
//        ReturnValue ret =
//                client.sendCommand("CHECK_TABLESPACE", "scott", "tiger",
//                        "mockdb", "127.0.0.1", "system");
//        Assert.assertEquals(ret.getStatus(), Status.OK);
//    }
//
//    @Test
//    public void checkTablespaceWarning() throws JNRPEClientException {
//        // <command name="check_oracle_ts" plugin_name="CHECK_ORACLE"
//        // params="--username $ARG1$ --password $ARG2$ -db $ARG3$
//        // --server $ARG4$ --tablespace $ARG5$ -w 70 -c 80"/>
//        //
//        // Example invocation:
//        // ./check_nrpe -t 20 -n -H myjnrpeserver -c check_oracle_ts -a
//        // 'username!password!SID!server!tablespacename'
//
//        JNRPEClient client = new JNRPEClient(BIND_ADDRESS, JNRPE_PORT, false);
//        client.setTimeout(120);
//        ReturnValue ret =
//                client.sendCommand("CHECK_TABLESPACE", "scott", "tiger",
//                        "mockdb", "127.0.0.1", "user");
//        Assert.assertEquals(ret.getStatus(), Status.WARNING);
//    }
//
//    @Test
//    public void checkTablespaceCritical() throws JNRPEClientException {
//        // <command name="check_oracle_ts" plugin_name="CHECK_ORACLE"
//        // params="--username $ARG1$ --password $ARG2$ -db $ARG3$
//        // --server $ARG4$ --tablespace $ARG5$ -w 70 -c 80"/>
//        //
//        // Example invocation:
//        // ./check_nrpe -t 20 -n -H myjnrpeserver -c check_oracle_ts -a
//        // 'username!password!SID!server!tablespacename'
//
//        JNRPEClient client = new JNRPEClient(BIND_ADDRESS, JNRPE_PORT, false);
//        client.setTimeout(120);
//        ReturnValue ret =
//                client.sendCommand("CHECK_TABLESPACE", "scott", "tiger",
//                        "mockdb", "127.0.0.1", "temp");
//        Assert.assertEquals(ret.getStatus(), Status.CRITICAL);
//    }

}
