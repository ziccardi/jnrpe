package it.jnrpe.plugin.tomcat;

class MemoryPoolData  {
    
    private final String poolName;

    private final long usageInit;
    private final long usageCommitted;
    private final long usageMax;
    private final long usageUsed;
    
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
