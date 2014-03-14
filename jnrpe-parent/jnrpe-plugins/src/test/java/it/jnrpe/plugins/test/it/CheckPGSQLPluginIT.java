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

import org.postgresql.Driver;
import org.testng.Assert;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

public class CheckPGSQLPluginIT implements ITConstants {

    @BeforeTest
    public void setup() throws Exception {
        ClassLoader cl = CCheckOracle.class.getClassLoader();

        PluginDefinition checkFile =
                PluginRepositoryUtil.parseXmlPluginDefinition(cl,
                        cl.getResourceAsStream("check_pgsql_plugin.xml"));

        ITSetup.getPluginRepository().addPluginDefinition(checkFile);

        CommandRepository cr = ITSetup.getCommandRepository();
        // --database $ARG1$ --hostname $ARG2$ --port $ARG3$ --logname $ARG4$
        // --password $ARG5$ --warning $ARG6$ --critical $ARG7$
        cr.addCommandDefinition(new CommandDefinition("CHECK_PGSQL_ALIVE",
                "CHECK_PGSQL")
                .addArgument(new CommandOption("database", "$ARG1$"))
                .addArgument(new CommandOption("hostname", "$ARG2$"))
                .addArgument(new CommandOption("port", "$ARG3$"))
                .addArgument(new CommandOption("password", "$ARG4$"))
                .addArgument(new CommandOption("warning", "$ARG5$"))
                .addArgument(new CommandOption("critical", "$ARG6$"))
                );

//      metric={metric},ok={range},warn={range},crit={range},unit={unit},prefix={SI prefix}
        cr.addCommandDefinition(new CommandDefinition("CHECK_PGSQL_ALIVE_NEW",
                "CHECK_PGSQL")
                .addArgument(new CommandOption("database", "$ARG1$"))
                .addArgument(new CommandOption("hostname", "$ARG2$"))
                .addArgument(new CommandOption("port", "$ARG3$"))
                .addArgument(new CommandOption("password", "$ARG4$"))
                .addArgument(new CommandOption("th", "metric=conn,ok=$ARG5$,warn=$ARG6$,crit=$ARG7$,unit=s"))
                );
    }

    @Test
    public void checkIsAliveOk() throws JNRPEClientException {
        JNRPEClient client = new JNRPEClient(BIND_ADDRESS, JNRPE_PORT, false);
        client.setTimeout(120);
        ReturnValue ret =
                client.sendCommand("CHECK_PGSQL_ALIVE", "mockdb", "localhost",
                        "5001", "dbpasswd", "5:10", "10:");
        Assert.assertEquals(ret.getStatus(), Status.OK, ret.getMessage());
    }

    @Test
    public void checkIsAliveNewOk() throws JNRPEClientException {
        JNRPEClient client = new JNRPEClient(BIND_ADDRESS, JNRPE_PORT, false);
        client.setTimeout(120);
        ReturnValue ret =
                client.sendCommand("CHECK_PGSQL_ALIVE_NEW", "mockdb", "localhost",
                        "5001", "dbpasswd", "inf..5", "5..10", "10..inf");
        Assert.assertEquals(ret.getStatus(), Status.OK, ret.getMessage());
    }

    @Test
    public void checkIsAliveWarning() throws JNRPEClientException {
        JNRPEClient client = new JNRPEClient(BIND_ADDRESS, JNRPE_PORT, false);
        client.setTimeout(120);
        Driver.setConnectionTime(2500);

        ReturnValue ret =
                client.sendCommand("CHECK_PGSQL_ALIVE", "mockdb", "localhost",
                        "5001", "dbpasswd", "2:5", "5:");
        Driver.setConnectionTime(0);

        Assert.assertEquals(ret.getStatus(), Status.WARNING, ret.getMessage());
    }

    @Test
    public void checkIsAliveNewWarning() throws JNRPEClientException {
        JNRPEClient client = new JNRPEClient(BIND_ADDRESS, JNRPE_PORT, false);
        client.setTimeout(120);
        Driver.setConnectionTime(3000);

        ReturnValue ret =
                client.sendCommand("CHECK_PGSQL_ALIVE_NEW", "mockdb", "localhost",
                        "5001", "dbpasswd", "inf..2", "2..5", "5..inf");
        Driver.setConnectionTime(0);

        Assert.assertEquals(ret.getStatus(), Status.WARNING, ret.getMessage());
    }

    @Test
    public void checkIsAliveCritical() throws JNRPEClientException {
        JNRPEClient client = new JNRPEClient(BIND_ADDRESS, JNRPE_PORT, false);
        client.setTimeout(120);
        Driver.setConnectionTime(5500);

        ReturnValue ret =
                client.sendCommand("CHECK_PGSQL_ALIVE", "mockdb", "localhost",
                        "5001", "dbpasswd", "2:5", "5:");
        Driver.setConnectionTime(0);

        Assert.assertEquals(ret.getStatus(), Status.CRITICAL, ret.getMessage());
    }

    @Test
    public void checkIsAliveNewCritical() throws JNRPEClientException {
        JNRPEClient client = new JNRPEClient(BIND_ADDRESS, JNRPE_PORT, false);
        client.setTimeout(120);
        Driver.setConnectionTime(5500);

        ReturnValue ret =
                client.sendCommand("CHECK_PGSQL_ALIVE_NEW", "mockdb", "localhost",
                        "5001", "dbpasswd", "inf..2", "2..5", "5..inf");
        Driver.setConnectionTime(0);

        Assert.assertEquals(ret.getStatus(), Status.CRITICAL, ret.getMessage());
    }

    // @Test
    // public void checkIsAliveKo() throws JNRPEClientException {
    // JNRPEClient client = new JNRPEClient(BIND_ADDRESS, JNRPE_PORT, false);
    // client.setTimeout(120);
    // ReturnValue ret =
    // client.sendCommand("CHECK_ORACLE_ALIVE", "scott", "tiger",
    // "mockdbko", "127.0.0.1");
    // Assert.assertEquals(ret.getStatus(), Status.CRITICAL, ret.getMessage());
    // }
    //
    // @Test
    // public void checkTablespaceOk() throws JNRPEClientException {
    // // <command name="check_oracle_ts" plugin_name="CHECK_ORACLE"
    // // params="--username $ARG1$ --password $ARG2$ -db $ARG3$
    // // --server $ARG4$ --tablespace $ARG5$ -w 70 -c 80"/>
    // //
    // // Example invocation:
    // // ./check_nrpe -t 20 -n -H myjnrpeserver -c check_oracle_ts -a
    // // 'username!password!SID!server!tablespacename'
    //
    // JNRPEClient client = new JNRPEClient(BIND_ADDRESS, JNRPE_PORT, false);
    // client.setTimeout(120);
    // ReturnValue ret =
    // client.sendCommand("CHECK_TABLESPACE", "scott", "tiger",
    // "mockdb", "127.0.0.1", "system");
    // Assert.assertEquals(ret.getStatus(), Status.OK, ret.getMessage());
    // }
    //
    // @Test
    // public void checkTablespaceWarning() throws JNRPEClientException {
    // // <command name="check_oracle_ts" plugin_name="CHECK_ORACLE"
    // // params="--username $ARG1$ --password $ARG2$ -db $ARG3$
    // // --server $ARG4$ --tablespace $ARG5$ -w 70 -c 80"/>
    // //
    // // Example invocation:
    // // ./check_nrpe -t 20 -n -H myjnrpeserver -c check_oracle_ts -a
    // // 'username!password!SID!server!tablespacename'
    //
    // JNRPEClient client = new JNRPEClient(BIND_ADDRESS, JNRPE_PORT, false);
    // client.setTimeout(120);
    // ReturnValue ret =
    // client.sendCommand("CHECK_TABLESPACE", "scott", "tiger",
    // "mockdb", "127.0.0.1", "user");
    // Assert.assertEquals(ret.getStatus(), Status.WARNING, ret.getMessage());
    // }
    //
    // @Test
    // public void checkTablespaceCritical() throws JNRPEClientException {
    // // <command name="check_oracle_ts" plugin_name="CHECK_ORACLE"
    // // params="--username $ARG1$ --password $ARG2$ -db $ARG3$
    // // --server $ARG4$ --tablespace $ARG5$ -w 70 -c 80"/>
    // //
    // // Example invocation:
    // // ./check_nrpe -t 20 -n -H myjnrpeserver -c check_oracle_ts -a
    // // 'username!password!SID!server!tablespacename'
    //
    // JNRPEClient client = new JNRPEClient(BIND_ADDRESS, JNRPE_PORT, false);
    // client.setTimeout(120);
    // ReturnValue ret =
    // client.sendCommand("CHECK_TABLESPACE", "scott", "tiger",
    // "mockdb", "127.0.0.1", "temp");
    // Assert.assertEquals(ret.getStatus(), Status.CRITICAL, ret.getMessage());
    // }

}
