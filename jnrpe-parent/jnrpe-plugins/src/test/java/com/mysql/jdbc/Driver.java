package com.mysql.jdbc;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;
import java.util.Properties;
import java.util.logging.Logger;

import it.jnrpe.plugins.mocks.sql.DbConnectionMock;
import it.jnrpe.plugins.mocks.sql.MockDriver;

public class Driver extends MockDriver {
    private final String RIGHTDBURL =
            "jdbc:mysql://localhost:3306/mockdb?user=dbadmin&password=dbadminpwd&autoReconnect=true&failOverReadOnly=false&maxReconnects=3";

    static boolean _slaveIoRunning = true;
    static boolean _slaveSQLRunning = true;
    static int _slaveBehindSeconds = 0;

    public Connection newConnection(String url, Properties info) throws SQLException {

        if (url.equals(RIGHTDBURL)) {
            return new DbConnectionMock(new MySQLQueryResolver());
        }

        throw new SQLException(
                "Unable to connect to any hosts due to exception: java.net.ConnectException: Connection refused");
    }

    public static void setSlaveStatus(boolean slaveIoRunning,
            boolean slaveSQLRunning, int slaveBehindSeconds) {
        _slaveIoRunning = slaveIoRunning;
        _slaveSQLRunning = slaveSQLRunning;
        _slaveBehindSeconds = slaveBehindSeconds;
    }

    @Override
    public boolean acceptsURL(String url) throws SQLException {
        return url.startsWith("jdbc:mysql:");
    }
    
	/* (non-Javadoc)
	 * @see java.sql.Driver#getParentLogger()
	 */
	public Logger getParentLogger() throws SQLFeatureNotSupportedException {
		// TODO Auto-generated method stub
		return null;
	}


}
