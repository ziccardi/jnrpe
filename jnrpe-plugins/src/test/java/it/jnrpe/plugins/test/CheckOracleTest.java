package it.jnrpe.plugins.test;

import it.jnrpe.ICommandLine;
import it.jnrpe.Status;
import it.jnrpe.plugin.CCheckOracle;
import it.jnrpe.plugins.Metric;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Matchers;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static org.mockito.Matchers.any;

/**
 * Created by ziccardi on 06/12/2016.
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest( { CCheckOracle.class } )
public class CheckOracleTest {

    @Test
    public void checkIsAlive() throws Exception {

        CCheckOracle checkOracle = PowerMockito.spy(new CCheckOracle());

        // Returned metric
        List<Metric> metricList = new ArrayList<Metric>();
        metricList.add(new Metric("conn", "Connection time : 10s", new BigDecimal(10), new BigDecimal(0), null));

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
}
