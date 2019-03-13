package it.jnrpe.plugins.test;

import it.jnrpe.Status;
import it.jnrpe.plugin.CheckPgsql;
import it.jnrpe.plugin.mysql.Mysql;
import it.jnrpe.plugins.Metric;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Matchers;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

@RunWith(PowerMockRunner.class)
@PrepareForTest({CheckPgsql.class, Mysql.class})
public class CheckPGSQLTest {


    private CheckPgsql prepareForTesting(final long connectionDelay) throws Exception {
        CheckPgsql checkMySql = PowerMockito.spy(new CheckPgsql());

        // Mock Database Connection
        final Connection conn = Mockito.mock(Connection.class);
        Statement st = Mockito.mock(Statement.class);
        ResultSet rs = Mockito.mock(ResultSet.class);
        Mockito.when(conn.createStatement()).thenReturn(st);
        //Mockito.when(st.executeQuery(CheckPgsql.SLAVE_STATUS_QRY)).thenReturn(rs);

        Mockito.when(rs.next()).thenReturn(true, false);
        Mockito.when(rs.getInt("Slave_IO_Running")).thenReturn(1);
        Mockito.when(rs.getInt("Slave_SQL_Running")).thenReturn(1);
        Mockito.when(rs.getInt("Seconds_Behind_Master")).thenReturn(4);

        PowerMockito.doReturn(conn).when(checkMySql, "getConnection", Matchers.any());

        return checkMySql;
    }

    @Test
    public void checkIsAliveOk() throws Exception {

        PluginTester.given(prepareForTesting(0))
            .withOption("database", 'd', "mockdb")
            .withOption("hostname", 'H', "localhost")
            .withOption("port", 'P', "5001")
            .withOption("password", 'p', "dbpasswd")
            .withOption("warning", 'w', "5:10")
            .withOption("critical", 'c', "10:")
            .expect(Status.OK);

    }

    @Test
    public void checkIsAliveWarning() throws Exception {
        CheckPgsql check = prepareForTesting(0);

        List<Metric> metrics = new ArrayList<Metric>();
        //metrics.add(new Metric("conn", "Connection time : 6s", new BigDecimal(6), new BigDecimal(0), null));
        metrics.add(
                Metric.forMetric("conn", Integer.class)
                .withMessage("Connection time : 6s")
                .withValue(6)
                .withMinValue(0)
                .build()
        );
        PowerMockito.doReturn(metrics).when(check, "gatherMetrics", Matchers.any());

        PluginTester.given(check)
            .withOption("database", 'd', "mockdb")
            .withOption("hostname", 'H', "localhost")
            .withOption("port", 'P', "5001")
            .withOption("password", 'p', "dbpasswd")
            .withOption("warning", 'w', "5:10")
            .withOption("critical", 'c', "10:")
            .expect(Status.WARNING);

    }

    @Test
    public void checkIsAliveNewOk() throws Exception {

        PluginTester.given(prepareForTesting(0))
            .withOption("database", 'd', "mockdb")
            .withOption("hostname", 'H', "localhost")
            .withOption("port", 'P', "5001")
            .withOption("password", 'p', "dbpasswd")
            .withOption("th", 't', "metric=conn,ok=inf..5,warn=5..10,crit=10..inf,unit=s")
            .expect(Status.OK);

    }

    @Test
    public void checkIsAliveNewWarning() throws Exception {

        CheckPgsql check = prepareForTesting(0);

        List<Metric> metrics = new ArrayList<Metric>();
        //metrics.add(new Metric("conn", "Connection time : 3s", new BigDecimal(3), new BigDecimal(0), null));
        metrics.add(
                Metric.forMetric("conn", Integer.class)
                .withMessage("Connection time : 3s")
                .withValue(3)
                .withMinValue(0)
                .build()
        );
        PowerMockito.doReturn(metrics).when(check, "gatherMetrics", Matchers.any());

        PluginTester.given(check)
            .withOption("database", 'd', "mockdb")
            .withOption("hostname", 'H', "localhost")
            .withOption("port", 'P', "5001")
            .withOption("password", 'p', "dbpasswd")
            .withOption("th", 't', "metric=conn,ok=inf..2,warn=2..5,crit=5..inf,unit=s")
            .expect(Status.WARNING);

    }

    @Test
    public void checkIsAliveCritical() throws Exception {

        CheckPgsql check = prepareForTesting(0);

        List<Metric> metrics = new ArrayList<Metric>();
        //metrics.add(new Metric("conn", "Connection time : 6s", new BigDecimal(6), new BigDecimal(0), null));
        metrics.add(
                Metric.forMetric("conn", Integer.class)
                .withMessage("Connection time : 6s")
                .withValue(6)
                .withMinValue(0)
                .build()
        );
        PowerMockito.doReturn(metrics).when(check, "gatherMetrics", Matchers.any());

        PluginTester.given(check)
            .withOption("database", 'd', "mockdb")
            .withOption("hostname", 'H', "localhost")
            .withOption("port", 'P', "5001")
            .withOption("password", 'p', "dbpasswd")
            .withOption("warning", 'w', "2:5")
            .withOption("critical", 'c', "5:")
            .expect(Status.CRITICAL);

    }

    @Test
    public void checkIsAliveNewCritical() throws Exception {

        CheckPgsql check = prepareForTesting(0);

        List<Metric> metrics = new ArrayList<Metric>();
        metrics.add(
                Metric.forMetric("conn", Integer.class)
                        .withMessage("Connection time : 6s")
                        .withValue(6)
                        .withMinValue(0)
                        .build()
        );
        PowerMockito.doReturn(metrics).when(check, "gatherMetrics", Matchers.any());

        PluginTester.given(check)
            .withOption("database", 'd', "mockdb")
            .withOption("hostname", 'H', "localhost")
            .withOption("port", 'P', "5001")
            .withOption("password", 'p', "dbpasswd")
            .withOption("th", 't', "metric=conn,ok=inf..2,warn=2..5,crit=5..inf,unit=s")
            .expect(Status.CRITICAL);

    }

}
