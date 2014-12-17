package it.jnrpe.plugin.tomcat;

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
