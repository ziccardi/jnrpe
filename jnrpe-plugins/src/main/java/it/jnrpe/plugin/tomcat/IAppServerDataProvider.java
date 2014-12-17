package it.jnrpe.plugin.tomcat;

import java.util.Collection;

interface IAppServerDataProvider {
    
    public void init(String hostname, String uri, int port, String username, String password, boolean useSSL, int timeout) throws Exception;
    
    public MemoryData getJVMMemoryUsage();
    public Collection<MemoryPoolData> getMemoryPoolData();
    public Collection<ThreadData> getThreadData();
}
