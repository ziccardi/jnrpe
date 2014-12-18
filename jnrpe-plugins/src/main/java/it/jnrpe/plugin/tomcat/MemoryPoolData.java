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

/**
 * Container for the memory pool informations.
 * 
 * @author Massimiliano Ziccardi
 */
class MemoryPoolData  {
    
    /**
     * The name of the pool.
     */
    private final String poolName;

    private final long usageInit;
    private final long usageCommitted;
    private final long usageMax;
    private final long usageUsed;

    /**
     * Constructor.
     * 
     * @param poolName
     * @param usageInit
     * @param usageCommitted
     * @param usageMax
     * @param usageUsed
     */
    public MemoryPoolData(String poolName, long usageInit, long usageCommitted, long usageMax, long usageUsed) {
        this.poolName = poolName;
        this.usageInit = usageInit;
        this.usageCommitted = usageCommitted;
        this.usageMax = usageMax;
        this.usageUsed = usageUsed;
    }

    public String getPoolName() {
        return poolName;
    }

    public long getUsageInit() {
        return usageInit;
    }

    public long getUsageCommitted() {
        return usageCommitted;
    }

    public long getUsageMax() {
        return usageMax;
    }

    public long getUsageUsed() {
        return usageUsed;
    }
}
