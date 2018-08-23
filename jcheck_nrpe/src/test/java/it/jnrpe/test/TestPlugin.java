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
package it.jnrpe.test;

import it.jnrpe.ICommandLine;
import it.jnrpe.ReturnValue;
import it.jnrpe.plugins.PluginBase;

public class TestPlugin extends PluginBase {

    public TestPlugin() {
    }

    public ReturnValue execute(final ICommandLine cl) {

        if ("NullPointerException".equals(cl.getOptionValue("t")))
            throw new NullPointerException(
                    "Thrown NullPointerException as requested");

        if ("ReturnNull".equals(cl.getOptionValue("type")))
            return null;

        if ("ThrowRuntimeException".equals(cl.getOptionValue("type")))
            throw new RuntimeException("Thrown RuntimeException as requested");

        return null;
    }

    @Override
    protected String getPluginName() {
        return "TEST_PLUGIN";
    }
}
