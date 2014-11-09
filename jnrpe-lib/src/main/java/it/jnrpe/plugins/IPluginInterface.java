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
package it.jnrpe.plugins;

import it.jnrpe.ICommandLine;
import it.jnrpe.ReturnValue;
import it.jnrpe.utils.BadThresholdException;

/**
 * This is the interface that all the JAVA plugins must implement.
 *
 * @author Massimiliano Ziccardi
 *
 * @version $Revision: 1.0 $
 */
public interface IPluginInterface {
    /**
     * Executes the plugin.
     *
     * @param cl
     *            The parsified command line arguments
    
    
     * @return The result of the plugin * @throws BadThresholdException
     *             - */
    ReturnValue execute(ICommandLine cl) throws BadThresholdException;
}
