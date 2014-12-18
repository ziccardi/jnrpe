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
package it.jnrpe.plugin.tomcat;

import java.util.Collection;

/**
 * The interface that must be implemented by the dataprovider used to 
 * gather the data from the Tomcat application server.
 * 
 * @author Massimiliano Ziccardi
 *
 */
interface IAppServerDataProvider {
    
    /**
     * Called to initialize the data provider.
     * 
     * @param hostname
     *          The hostname where tomcat runs
     * @param uri
     *          The path of the XML admin page
     * @param port
     *          The port where tomcat listens to
     * @param username
     *          The administrator user name
     * @param password
     *          The administratot password
     * @param useSSL
     *          <code>true</code> if connections must be attempted
     *          through https
     * @param timeout
     *          The connection timeout
     * @throws Exception
     */
    public void init(String hostname, String uri, int port, String username, String password, boolean useSSL, int timeout) throws Exception;
    
    /**
     * @return data about JVM memory usage.
     */
    public MemoryData getJVMMemoryUsage();
    
    /**
     * @return data about the memory pools
     */
    public Collection<MemoryPoolData> getMemoryPoolData();
    
    /**
     * @return data about the connectors thread usage
     */
    public Collection<ThreadData> getThreadData();
}
