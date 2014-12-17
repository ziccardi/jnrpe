package it.jnrpe.plugin.tomcat;

class MemoryData {
    private final long freeMemory;
    private final long totalMemory;
    private final long maxMemory;
    
    public MemoryData(long freeMemory, long maxMemory, long totalMemory) {
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
