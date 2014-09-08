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

import org.testng.annotations.Test;

/**
 * Unit test for the check_users plugin.
 *
 * @author Frederico Campos
 * @author Massimiliano Ziccardi
 */
// Massimiliano - 2014-07-07 - Rewrote the tests using the fluent api and moved 
// from Integration Tests to Unit tests. 
public class CheckUsersTest {
    
    @Test
    public void checkUsersOk() throws Exception {

        PluginTester.given(new CheckUsers())
            .withOption("warning", 'w', "10:")
            .withOption("critical", 'c', "20:")
            .expect(Status.OK);
        
    }

    @Test
    public void checkUsersWarning() throws Exception {
        
        PluginTester.given(new CheckUsers())
            .withOption("warning", 'w', "0:")
            .withOption("critical", 'c', "20:")
            .expect(Status.WARNING);
        
    }

    @Test
    public void checkUsersCritical() throws Exception {
        
        PluginTester.given(new CheckUsers())
            .withOption("warning", 'w', "~:0")
            .withOption("critical", 'c', "0:")
            .expect(Status.CRITICAL);
        
    }
}
