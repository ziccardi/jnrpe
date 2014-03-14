package it.jnrpe.plugins.mocks.sql;

import java.sql.Connection;
import java.sql.Driver;
import java.sql.DriverPropertyInfo;
import java.sql.SQLException;
import java.util.Properties;

public abstract class MockDriver implements Driver {

    public static int QUERY_TIME = 0;

    private static int CONNECTION_TIME = 0;
    private Connection conn = null;

    private void delay() {
        try {
            Thread.sleep(CONNECTION_TIME);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public final Connection connect(String url, Properties info) throws SQLException {
        if (!acceptsURL(url))
            return null;
        if (conn != null && !conn.isClosed()) {
            return conn;
        }
        delay();
        return conn = newConnection(url, info);
    }

    protected abstract Connection newConnection(String url, Properties info) throws SQLException;

    public static void setConnectionTime(int millis) {
        CONNECTION_TIME = millis;
    }

    public abstract boolean acceptsURL(String url) throws SQLException;


    public DriverPropertyInfo[] getPropertyInfo(String url, Properties info)
            throws SQLException {
        return null;
    }

    public int getMajorVersion() {
        return 1;
    }

    public int getMinorVersion() {
        return 0;
    }

    public boolean jdbcCompliant() {
        return true;
    }

    public boolean isConnectionClosed() throws SQLException {
        return conn.isClosed();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null)
            return false;
        return obj.getClass().getName().equals(this.getClass().getName());
    }
}
