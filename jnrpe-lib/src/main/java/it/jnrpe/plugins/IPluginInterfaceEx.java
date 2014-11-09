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


/**
 * This interface must be implemented by all the plugins that needs to send
 * events that must be intercepted by the JNRPE Server.
 *
 * If you plan to handle the events independently from JNRPE server, you can
 * avoid implementing this interface.
 *
 * @author Massimiliano Ziccardi
 * @deprecated this interface is not useful anymore
 * @version $Revision: 1.0 $
 */
public interface IPluginInterfaceEx extends IPluginInterface {
    
    //void setContext(IJNRPEExecutionContext ctx);
}
