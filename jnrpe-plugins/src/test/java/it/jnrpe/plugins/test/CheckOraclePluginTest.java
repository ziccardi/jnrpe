package it.jnrpe.plugins.test;

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

import org.testng.Assert;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

public class CheckOraclePluginTest implements Constants {

    @BeforeTest
    public void setup() throws Exception {
        ClassLoader cl = CCheckOracle.class.getClassLoader();

        PluginDefinition checkFile =
                PluginRepositoryUtil.parseXmlPluginDefinition(cl,
                        cl.getResourceAsStream("check_oracle_plugin.xml"));

        SetupTest.getPluginRepository().addPluginDefinition(checkFile);

        CommandRepository cr = SetupTest.getCommandRepository();

        cr.addCommandDefinition(new CommandDefinition("CHECK_ORACLE_ALIVE",
                "CHECK_ORACLE")
                .addArgument(new CommandOption("username", "$ARG1$"))
                .addArgument(new CommandOption("password", "$ARG2$"))
                .addArgument(new CommandOption("db", "$ARG3$"))
                .addArgument(new CommandOption("server", "$ARG4$"))
                .addArgument(new CommandOption("alive")));

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
                client.sendCommand("CHECK_ORACLE_ALIVE", "scott", "tiger",
                        "mockdb", "127.0.0.1");
        Assert.assertEquals(ret.getStatus(), Status.OK, ret.getMessage());
    }

    @Test
    public void checkIsAliveKo() throws JNRPEClientException {
        JNRPEClient client = new JNRPEClient(BIND_ADDRESS, JNRPE_PORT, false);
        client.setTimeout(120);
        ReturnValue ret =
                client.sendCommand("CHECK_ORACLE_ALIVE", "scott", "tiger",
                        "mockdbko", "127.0.0.1");
        Assert.assertEquals(ret.getStatus(), Status.CRITICAL, ret.getMessage());
    }

    @Test
    public void checkTablespaceOk() throws JNRPEClientException {
        // <command name="check_oracle_ts" plugin_name="CHECK_ORACLE"
        // params="--username $ARG1$ --password $ARG2$ -db $ARG3$
        // --server $ARG4$ --tablespace $ARG5$ -w 70 -c 80"/>
        //
        // Example invocation:
        // ./check_nrpe -t 20 -n -H myjnrpeserver -c check_oracle_ts -a
        // 'username!password!SID!server!tablespacename'

        JNRPEClient client = new JNRPEClient(BIND_ADDRESS, JNRPE_PORT, false);
        client.setTimeout(120);
        ReturnValue ret =
                client.sendCommand("CHECK_TABLESPACE", "scott", "tiger",
                        "mockdb", "127.0.0.1", "system");
        Assert.assertEquals(ret.getStatus(), Status.OK, ret.getMessage());
    }

    @Test
    public void checkTablespaceWarning() throws JNRPEClientException {
        // <command name="check_oracle_ts" plugin_name="CHECK_ORACLE"
        // params="--username $ARG1$ --password $ARG2$ -db $ARG3$
        // --server $ARG4$ --tablespace $ARG5$ -w 70 -c 80"/>
        //
        // Example invocation:
        // ./check_nrpe -t 20 -n -H myjnrpeserver -c check_oracle_ts -a
        // 'username!password!SID!server!tablespacename'

        JNRPEClient client = new JNRPEClient(BIND_ADDRESS, JNRPE_PORT, false);
        client.setTimeout(120);
        ReturnValue ret =
                client.sendCommand("CHECK_TABLESPACE", "scott", "tiger",
                        "mockdb", "127.0.0.1", "user");
        Assert.assertEquals(ret.getStatus(), Status.WARNING, ret.getMessage());
    }

    @Test
    public void checkTablespaceCritical() throws JNRPEClientException {
        // <command name="check_oracle_ts" plugin_name="CHECK_ORACLE"
        // params="--username $ARG1$ --password $ARG2$ -db $ARG3$
        // --server $ARG4$ --tablespace $ARG5$ -w 70 -c 80"/>
        //
        // Example invocation:
        // ./check_nrpe -t 20 -n -H myjnrpeserver -c check_oracle_ts -a
        // 'username!password!SID!server!tablespacename'

        JNRPEClient client = new JNRPEClient(BIND_ADDRESS, JNRPE_PORT, false);
        client.setTimeout(120);
        ReturnValue ret =
                client.sendCommand("CHECK_TABLESPACE", "scott", "tiger",
                        "mockdb", "127.0.0.1", "temp");
        Assert.assertEquals(ret.getStatus(), Status.CRITICAL, ret.getMessage());
    }

}
