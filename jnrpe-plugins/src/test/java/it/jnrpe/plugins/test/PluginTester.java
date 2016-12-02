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

import java.nio.charset.Charset;

import it.jnrpe.JNRPEEventBus;
import it.jnrpe.ReturnValue;
import it.jnrpe.Status;
import it.jnrpe.plugins.IPluginInterface;
import it.jnrpe.test.utils.TestCommandLine;
import it.jnrpe.test.utils.TestContext;
import it.jnrpe.utils.BadThresholdException;
import it.jnrpe.utils.internal.InjectionUtils;
import org.junit.Assert;

/**
 * Utility class to perform plugin tests using fluent api.
 * 
 * @author Massimiliano Ziccardi
 */
public final class PluginTester {
    
    /**
     * The plugin to be tested.
     */
    private final IPluginInterface jnrpePlugin;
    
    /**
     * The command line to ass to the plugin.
     */
    private TestCommandLine cli = new TestCommandLine();
    
    /**
     * The return value of the plugin execution.
     * It is cached here so subsequent calls to {@link #expect(Status)} won't run
     * the plugin many times.
     */
    private ReturnValue retValue = null;
    
    /**
     * Constructor.
     * 
     * @param plugin the plugin to be tested.
     */
    private PluginTester(final IPluginInterface plugin) {
        jnrpePlugin = plugin;
    }
    
    /**
     * Initializes the tester with the plugin to be tested.
     * 
     * @param plugin the plugin to be tested
     * @return this
     */
    public static PluginTester given(final IPluginInterface plugin) {
        InjectionUtils.inject(plugin, new TestContext(new JNRPEEventBus(), Charset.defaultCharset(), null, null));
        return new PluginTester(plugin);
    }
    
    /**
     * Adds an option to the plugin under testing.
     * 
     * @param longName the long option name
     * @param shortName the short option name
     * @param optionValue the option value. Can be null
     * @return this
     */
    public PluginTester withOption(final String longName, final char shortName, final String optionValue) {
        cli.withOption(longName, shortName, optionValue);
        return this;
    }
    
    /**
     * Checks that the value returned by the plugin corresponds to the expected status.
     * 
     * @param status the status to be checked
     */
    public void expect(final Status status) {
        
        if (retValue == null) {
            execute();
        }
        Assert.assertEquals(retValue.getMessage(), status, retValue.getStatus());
    }
    
    /**
     * Executes the plugin.
     */
    private void execute() {
        try {
            retValue = jnrpePlugin.execute(cli);
        } catch (BadThresholdException e) {
            Assert.fail("Failed with error: " + e.getMessage());
        }
    }
}
