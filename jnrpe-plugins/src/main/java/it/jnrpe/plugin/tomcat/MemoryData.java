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
 * Container for memory information data.
 * 
 * @author Massimiliano Ziccardi
 *
 */
class MemoryData {
    
    /**
     * Tomcat free memory in bytes.
     */
    private final long freeMemory;
    
    /**
     * Tomcat total memory in bytes.
     */
    private final long totalMemory;
    
    /**
     * Tomcat maxMemory in bytes.
     */
    private final long maxMemory;
    
    /**
     * Constructor.
     * 
     * @param freeMemory tomcat free memory in bytes
     * @param maxMemory tomcat max memory in bytes
     * @param totalMemory tomcat total memory in bytes
     */
    public MemoryData(final long freeMemory, final long maxMemory, 
            final long totalMemory) {
        this.maxMemory = maxMemory;
        this.freeMemory = freeMemory;
        this.totalMemory = totalMemory;
    }

    /**
     * @return the current maximum amount of already allocated memory in bytes
     */
    public long getMaxMemory() {
        return maxMemory;
    }

    /**
     * @return the freeMemory in bytes
     */
    public long getFreeMemory() {
        return freeMemory;
    }

    /**
     * @return the total amount of the allocable memory in bytes
     */
    public long getTotalMemory() {
        return totalMemory;
    }
}
