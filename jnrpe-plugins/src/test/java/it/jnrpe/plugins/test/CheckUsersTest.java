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
import it.jnrpe.plugin.CheckUsers;
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


/**
 * Unit test for the check_users plugin.
 *
 * @author Frederico Campos
 * @author Massimiliano Ziccardi
 */
// Massimiliano - 2014-07-07 - Rewrote the tests using the fluent api and moved 
// from Integration Tests to Unit tests.
@RunWith(PowerMockRunner.class)
@PrepareForTest(CheckUsers.class)
public class CheckUsersTest {

    private CheckUsers getMock(int numberOfUsers) throws Exception {
        CheckUsers cu = PowerMockito.spy(new CheckUsers());
        List<Metric> metrics = new ArrayList<Metric>();
        //metrics.add(new Metric("users", "", new BigDecimal(numberOfUsers), null, null));
        metrics.add(
                Metric.forMetric("users", Integer.class)
                .withValue(numberOfUsers)
                .build()
        );

        PowerMockito.doReturn(metrics).when(cu, "gatherMetrics", Matchers.any());

        return cu;
    }

    @Test
    public void checkUsersOk() throws Exception {
        PluginTester.given(getMock(5))
            .withOption("warning", 'w', "10:")
            .withOption("critical", 'c', "20:")
            .expect(Status.OK);

    }

    @Test
    public void checkUsersWarning() throws Exception {

        PluginTester.given(getMock(15))
            .withOption("warning", 'w', "10:")
            .withOption("critical", 'c', "20:")
            .expect(Status.WARNING);

    }

    @Test
    public void checkUsersCritical() throws Exception {

        PluginTester.given(getMock(25))
            .withOption("warning", 'w', "10:")
            .withOption("critical", 'c', "20:")
            .expect(Status.CRITICAL);

    }
}