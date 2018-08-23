package it.jnrpe.plugins.test;

import it.jnrpe.Status;
import it.jnrpe.plugin.mysql.CheckMysql;
import it.jnrpe.plugin.mysql.Mysql;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

@RunWith(PowerMockRunner.class)
@PrepareForTest({CheckMysql.class, Mysql.class})
public class CheckMySQLTest {

    private CheckMysql prepareForTesting(final long connectionDelay) throws Exception {
        CheckMysql checkMySql = PowerMockito.spy(new CheckMysql());

        // Mock Database Connection
        final Connection conn = Mockito.mock(Connection.class);
        Statement st = Mockito.mock(Statement.class);
        ResultSet rs = Mockito.mock(ResultSet.class);
        Mockito.when(conn.createStatement()).thenReturn(st);
        Mockito.when(st.executeQuery(CheckMysql.SLAVE_STATUS_QRY)).thenReturn(rs);

        Mockito.when(rs.next()).thenReturn(true, false);
        Mockito.when(rs.getInt("Slave_IO_Running")).thenReturn(1);
        Mockito.when(rs.getInt("Slave_SQL_Running")).thenReturn(1);
        Mockito.when(rs.getInt("Seconds_Behind_Master")).thenReturn(4);

        Mysql m = PowerMockito.mock(Mysql.class);

        PowerMockito.when(m.getConnection()).then(new Answer<Connection>() {
            public Connection answer(InvocationOnMock invocationOnMock) throws Throwable {
                Thread.sleep(connectionDelay);
                return conn;
            }
        });

        PowerMockito.whenNew(Mysql.class).withAnyArguments().thenReturn(m);


        return checkMySql;
    }

    @Test
    public void checkConnectionOk() throws Exception {

        PluginTester
            .given(prepareForTesting(0))
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
    public void checkConnectionWarning() throws Exception {

        PluginTester
            .given(prepareForTesting(3000))
            .withOption("hostname", 'h', "localhost")
            .withOption("port", 'p', "3306")
            .withOption("database", 'd', "mockdb")
            .withOption("user", 'u', "dbadmin")
            .withOption("password", 'p', "dbadminpwd")
            .withOption("warning", 'w', "3:5")
            .withOption("critical", 'c', "5:")
            .expect(Status.WARNING);

    }

    @Test
    public void checkConnectionCritical() throws Exception {

        PluginTester
            .given(prepareForTesting(5000))
            .withOption("hostname", 'h', "localhost")
            .withOption("port", 'p', "3306")
            .withOption("database", 'd', "mockdb")
            .withOption("user", 'u', "dbadmin")
            .withOption("password", 'p', "dbadminpwd")
            .withOption("warning", 'w', "3:5")
            .withOption("critical", 'c', "5:")
            .expect(Status.CRITICAL);
    }

    @Test
    public void checkSlavesOk() throws Exception {

        PluginTester
            .given(prepareForTesting(0))
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
    public void checkSlavesWarning() throws Exception {

        PluginTester
            .given(prepareForTesting(0))
            .withOption("hostname", 'h', "localhost")
            .withOption("port", 'p', "3306")
            .withOption("database", 'd', "mockdb")
            .withOption("user", 'u', "dbadmin")
            .withOption("password", 'p', "dbadminpwd")
            .withOption("warning", 'w', "3:8")
            .withOption("critical", 'c', "8:")
            .withOption("check-slave", 's', null)
            .expect(Status.WARNING);

    }

    @Test
    public void checkSlavesCritical() throws Exception {

        PluginTester
            .given(prepareForTesting(0))
            .withOption("hostname", 'h', "localhost")
            .withOption("port", 'p', "3306")
            .withOption("database", 'd', "mockdb")
            .withOption("user", 'u', "dbadmin")
            .withOption("password", 'p', "dbadminpwd")
            .withOption("warning", 'w', "2:3")
            .withOption("critical", 'c', "4:")
            .withOption("check-slave", 's', null)
            .expect(Status.CRITICAL);

    }

}
