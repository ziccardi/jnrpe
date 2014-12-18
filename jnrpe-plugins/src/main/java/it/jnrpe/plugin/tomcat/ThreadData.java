package it.jnrpe.plugin.tomcat;

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
class ThreadData {
    
    private final String connectorName;
    private final long maxThreadCount;
    private final long currentThreadCount;
    private final long currentThreadBusy;
    
    public ThreadData(String connectorName, long currentThreadCount, long currentThreadBusy, long maxThreadCount) {
        this.maxThreadCount = maxThreadCount;
        this.currentThreadBusy = currentThreadBusy;
        this.currentThreadCount = currentThreadCount;
        this.connectorName = connectorName;
    }

    public long getMaxThreadCount() {
        return maxThreadCount;
    }

    public long getCurrentThreadCount() {
        return currentThreadCount;
    }

    /**
     * @return the currentThreadBusy
     */
    public long getCurrentThreadBusy() {
        return currentThreadBusy;
    }

    /**
     * @return the connectorName
     */
    public String getConnectorName() {
        return connectorName;
    }
}
