package oracle.jdbc.driver;

import it.jnrpe.plugins.mocks.sql.DbConnectionMock;
import it.jnrpe.plugins.mocks.sql.MockDriver;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;
import java.util.Properties;
import java.util.logging.Logger;

public class OracleDriver extends MockDriver {

    public Connection newConnection(String url, Properties info) throws SQLException {
        if (url.equals("jdbc:oracle:thin:@127.0.0.1:1521:mockdb")) {
            return new DbConnectionMock(new OracleSQLQueryResolver());
        }

        throw new SQLException(
                "Listener refused the connection with the following error: ORA-12505, TNS:listener does not currently know of SID given in connect descriptor");
    }

    @Override
    public boolean acceptsURL(String url) throws SQLException {
        return url.startsWith("jdbc:oracle:");
    }

	/* (non-Javadoc)
	 * @see java.sql.Driver#getParentLogger()
	 */
	public Logger getParentLogger() throws SQLFeatureNotSupportedException {
		// TODO Auto-generated method stub
		return null;
	}

}
