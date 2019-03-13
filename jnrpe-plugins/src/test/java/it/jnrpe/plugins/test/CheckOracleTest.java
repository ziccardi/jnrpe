package it.jnrpe.plugins.test;

import it.jnrpe.ICommandLine;
import it.jnrpe.Status;
import it.jnrpe.plugin.CCheckOracle;
import it.jnrpe.plugins.Metric;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Matchers;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import static org.mockito.Matchers.any;

/**
 * Created by ziccardi on 06/12/2016.
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest( { CCheckOracle.class, Connection.class } )
public class CheckOracleTest {

    @Test
    public void checkIsAliveOk() throws Exception {

        CCheckOracle checkOracle = PowerMockito.spy(new CCheckOracle());

        // Returned metric
        List<Metric> metricList = new ArrayList<Metric>();
        metricList.add(
                Metric.forMetric("conn", Integer.class)
                .withMessage("Connection time : 10s")
                .withValue(10)
                .withMinValue(0)
                .build()
        );

        //PowerMockito.when(checkOracle, "getConnection").;
        PowerMockito.doReturn(null).when(checkOracle, "getConnection", any(ICommandLine.class));
        PowerMockito.doReturn(metricList).when(checkOracle, "checkAlive", Matchers.any(), Matchers.any());

        PluginTester.given(checkOracle)
            .withOption("username", 'u', "scott")
            .withOption("password", 'p', "tiger")
            .withOption("db", 'd', "mockdb")
            .withOption("server", 's', "127.0.0.1")
            .withOption("alive", 'a', null)
            .expect(Status.OK);
    }

    /**
     * This class is to overcome PowerMock bug when mocking interfaces
     * https://github.com/powermock/powermock/issues/717
     */
    abstract class MyConn implements Connection {

    }

    private CCheckOracle prepareForTablespaceTesting(final long connectionDelay) throws Exception {
        CCheckOracle checkOracle = PowerMockito.spy(new CCheckOracle());

        // Mock Database Connection
        final Connection conn = Mockito.mock(Connection.class);
        Statement st = Mockito.mock(Statement.class);
        ResultSet rs = Mockito.mock(ResultSet.class);

        String qry = String.format(CCheckOracle.QRY_CHECK_TBLSPACE_PATTERN, "MYTBLSPACE");

        Mockito.when(conn.createStatement()).thenReturn(st);
        Mockito.when(st.executeQuery(qry)).thenReturn(rs);
        Mockito.when(rs.next()).thenReturn(true);

        Mockito.when(rs.getBigDecimal(1)).thenReturn(new BigDecimal(100));
        Mockito.when(rs.getBigDecimal(2)).thenReturn(new BigDecimal(500));
        Mockito.when(rs.getBigDecimal(3)).thenReturn(new BigDecimal(80));

        Mockito.when(rs.getLong(1)).thenReturn(100L);
        Mockito.when(rs.getLong(2)).thenReturn(500L);
        Mockito.when(rs.getLong(3)).thenReturn(80L);

        Mockito.when(rs.getInt(1)).thenReturn(100);
        Mockito.when(rs.getInt(2)).thenReturn(500);
        Mockito.when(rs.getInt(3)).thenReturn(80);

        // Mock test methods
        PowerMockito.doReturn(new ArrayList<Metric>()).when(checkOracle, "checkAlive", any(), any());

        PowerMockito.doReturn(conn).when(checkOracle, "getConnection", any(ICommandLine.class));
        PowerMockito.doAnswer(new Answer<Connection>() {
            public Connection answer(InvocationOnMock invocation){
                try {
                    Thread.sleep(connectionDelay);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                return conn;
            }
        }).when(checkOracle, "getConnection", any(ICommandLine.class));
        return checkOracle;
    }

    @Test
    public void checkTablespaceOk() throws Exception {
        PluginTester.given(prepareForTablespaceTesting(0))
            .withOption("username", 'u', "scott")
            .withOption("password", 'p', "tiger")
            .withOption("db", 'd', "mockdb")
            .withOption("server", 's', "127.0.0.1")
            .withOption("tablespace", 't', "MYTBLSPACE")
            .withOption("warning", 'w', ":70")
            .withOption("critical", 'c', ":60")
            .expect(Status.OK);
    }

    @Test
    public void checkTablespaceWarning() throws Exception {
        PluginTester.given(prepareForTablespaceTesting(0))
            .withOption("username", 'u', "scott")
            .withOption("password", 'p', "tiger")
            .withOption("db", 'd', "mockdb")
            .withOption("server", 's', "127.0.0.1")
            .withOption("tablespace", 't', "MYTBLSPACE")
            .withOption("warning", 'w', ":80")
            .withOption("critical", 'c', "81:")
            .expect(Status.WARNING);
    }

    @Test
    public void checkTablespaceCritical() throws Exception {
        PluginTester.given(prepareForTablespaceTesting(0))
            .withOption("username", 'u', "scott")
            .withOption("password", 'p', "tiger")
            .withOption("db", 'd', "mockdb")
            .withOption("server", 's', "127.0.0.1")
            .withOption("tablespace", 't', "MYTBLSPACE")
            .withOption("warning", 'w', ":80")
            .withOption("critical", 'c', ":60")
            .expect(Status.WARNING);
    }

    @Test
    public void checkIsAliveOkNewThresholdSyntax() throws Exception {
        PluginTester.given(prepareForTablespaceTesting(0))
            .withOption("username", 'u', "scott")
            .withOption("password", 'p', "tiger")
            .withOption("db", 'd', "mockdb")
            .withOption("server", 's', "127.0.0.1")
            .withOption("th", 't', "metric=conn,ok=0..10,warn=10..20,crit=20..inf,unit=s")
            .withOption("alive", 'a', null)
            .expect(Status.OK);
    }

    @Test
    public void checkIsAliveWarningNewThresholdSyntax() throws Exception {

        CCheckOracle checkOracle = PowerMockito.spy(new CCheckOracle());

        // Returned metric
        List<Metric> metricList = new ArrayList<Metric>();
        metricList.add(
                Metric.forMetric("conn", Integer.class)
                        .withMessage("Connection time : 15s")
                        .withValue(15)
                        .withMinValue(0)
                        .build()

        );

        PowerMockito.doReturn(null).when(checkOracle, "getConnection", any(ICommandLine.class));
        PowerMockito.doReturn(metricList).when(checkOracle, "checkAlive", Matchers.any(), Matchers.any());

        PluginTester.given(checkOracle)
            .withOption("username", 'u', "scott")
            .withOption("password", 'p', "tiger")
            .withOption("db", 'd', "mockdb")
            .withOption("server", 's', "127.0.0.1")
            .withOption("th", 't', "metric=conn,ok=0..10,warn=10..20,crit=20..inf,unit=s")
            .withOption("alive", 'a', null)
            .expect(Status.WARNING);
    }

    @Test
    public void checkIsAliveCriticalNewThresholdSyntax() throws Exception {

        CCheckOracle checkOracle = PowerMockito.spy(new CCheckOracle());

        // Returned metric
        List<Metric> metricList = new ArrayList<Metric>();
        metricList.add(
                Metric.forMetric("conn", Integer.class)
                        .withMessage("Connection time : 15s")
                        .withValue(15)
                        .withMinValue(0)
                        .build()

        );

        PowerMockito.doReturn(null).when(checkOracle, "getConnection", any(ICommandLine.class));
        PowerMockito.doReturn(metricList).when(checkOracle, "checkAlive", Matchers.any(), Matchers.any());

        PluginTester.given(checkOracle)
            .withOption("username", 'u', "scott")
            .withOption("password", 'p', "tiger")
            .withOption("db", 'd', "mockdb")
            .withOption("server", 's', "127.0.0.1")
            .withOption("th", 't', "metric=conn,ok=0..10,warn=10..15,crit=15..inf,unit=s")
            .withOption("alive", 'a', null)
            .expect(Status.CRITICAL);
    }

    @Test
    public void checkIsAliveKo() throws Exception {
        CCheckOracle checkOracle = PowerMockito.spy(new CCheckOracle());

        // Returned metric
        List<Metric> metricList = new ArrayList<Metric>();
        metricList.add(
                Metric.forMetric("conn", Integer.class)
                        .withMessage("Connection time : 15s")
                        .withValue(15)
                        .withMinValue(0)
                        .build()

        );

        PowerMockito.doThrow(new SQLException("Connection failed")).when(checkOracle, "getConnection", any(ICommandLine.class));
        PowerMockito.doReturn(metricList).when(checkOracle, "checkAlive", Matchers.any(), Matchers.any());

        PluginTester.given(checkOracle)
            .withOption("username", 'u', "scott")
            .withOption("password", 'p', "tiger")
            .withOption("db", 'd', "mockdb")
            .withOption("server", 's', "127.0.0.1")
            .withOption("th", 't', "metric=conn,ok=0..10,warn=10..15,crit=15..inf,unit=s")
            .withOption("alive", 'a', null)
            .expect(Status.CRITICAL);
    }



}
