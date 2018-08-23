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
package it.jnrpe.plugins.mocks.sql;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.SQLWarning;
import java.sql.Statement;

public class StatementMock implements Statement {
    private final ISQLQueryResolver m_queryResolver;

    public StatementMock(ISQLQueryResolver queryResolver) {
        m_queryResolver = queryResolver;
    }

    public <T> T unwrap(final Class<T> iface) throws SQLException {
        // TODO Auto-generated method stub
        return null;
    }

    public boolean isWrapperFor(Class<?> iface) throws SQLException {
        // TODO Auto-generated method stub
        return false;
    }

    public ResultSet executeQuery(String sql) throws SQLException {
        return new ResultSetMock(m_queryResolver.resolveSQL(sql));
    }

    public int executeUpdate(String sql) throws SQLException {
        // TODO Auto-generated method stub
        return 0;
    }

    public void close() throws SQLException {
        // TODO Auto-generated method stub

    }

    public int getMaxFieldSize() throws SQLException {
        // TODO Auto-generated method stub
        return 0;
    }

    public void setMaxFieldSize(int max) throws SQLException {
        // TODO Auto-generated method stub

    }

    public int getMaxRows() throws SQLException {
        // TODO Auto-generated method stub
        return 0;
    }

    public void setMaxRows(int max) throws SQLException {
        // TODO Auto-generated method stub

    }

    public void setEscapeProcessing(boolean enable) throws SQLException {
        // TODO Auto-generated method stub

    }

    public int getQueryTimeout() throws SQLException {
        // TODO Auto-generated method stub
        return 0;
    }

    public void setQueryTimeout(int seconds) throws SQLException {
        // TODO Auto-generated method stub

    }

    public void cancel() throws SQLException {
        // TODO Auto-generated method stub

    }

    public SQLWarning getWarnings() throws SQLException {
        // TODO Auto-generated method stub
        return null;
    }

    public void clearWarnings() throws SQLException {
        // TODO Auto-generated method stub

    }

    public void setCursorName(String name) throws SQLException {
        // TODO Auto-generated method stub

    }

    public boolean execute(String sql) throws SQLException {
        // TODO Auto-generated method stub
        return false;
    }

    public ResultSet getResultSet() throws SQLException {
        // TODO Auto-generated method stub
        return null;
    }

    public int getUpdateCount() throws SQLException {
        // TODO Auto-generated method stub
        return 0;
    }

    public boolean getMoreResults() throws SQLException {
        // TODO Auto-generated method stub
        return false;
    }

    public void setFetchDirection(int direction) throws SQLException {
        // TODO Auto-generated method stub

    }

    public int getFetchDirection() throws SQLException {
        // TODO Auto-generated method stub
        return 0;
    }

    public void setFetchSize(int rows) throws SQLException {
        // TODO Auto-generated method stub

    }

    public int getFetchSize() throws SQLException {
        // TODO Auto-generated method stub
        return 0;
    }

    public int getResultSetConcurrency() throws SQLException {
        // TODO Auto-generated method stub
        return 0;
    }

    public int getResultSetType() throws SQLException {
        // TODO Auto-generated method stub
        return 0;
    }

    public void addBatch(String sql) throws SQLException {
        // TODO Auto-generated method stub

    }

    public void clearBatch() throws SQLException {
        // TODO Auto-generated method stub

    }

    public int[] executeBatch() throws SQLException {
        // TODO Auto-generated method stub
        return null;
    }

    public Connection getConnection() throws SQLException {
        // TODO Auto-generated method stub
        return null;
    }

    public boolean getMoreResults(int current) throws SQLException {
        // TODO Auto-generated method stub
        return false;
    }

    public ResultSet getGeneratedKeys() throws SQLException {
        // TODO Auto-generated method stub
        return null;
    }

    public int executeUpdate(String sql, int autoGeneratedKeys)
            throws SQLException {
        // TODO Auto-generated method stub
        return 0;
    }

    public int executeUpdate(String sql, int[] columnIndexes)
            throws SQLException {
        // TODO Auto-generated method stub
        return 0;
    }

    public int executeUpdate(String sql, String[] columnNames)
            throws SQLException {
        // TODO Auto-generated method stub
        return 0;
    }

    public boolean execute(String sql, int autoGeneratedKeys)
            throws SQLException {
        // TODO Auto-generated method stub
        return false;
    }

    public boolean execute(String sql, int[] columnIndexes) throws SQLException {
        // TODO Auto-generated method stub
        return false;
    }

    public boolean execute(String sql, String[] columnNames)
            throws SQLException {
        // TODO Auto-generated method stub
        return false;
    }

    public int getResultSetHoldability() throws SQLException {
        // TODO Auto-generated method stub
        return 0;
    }

    public boolean isClosed() throws SQLException {
        // TODO Auto-generated method stub
        return false;
    }

    public void setPoolable(boolean poolable) throws SQLException {
        // TODO Auto-generated method stub

    }

    public boolean isPoolable() throws SQLException {
        // TODO Auto-generated method stub
        return false;
    }

	/* (non-Javadoc)
	 * @see java.sql.Statement#closeOnCompletion()
	 */
	public void closeOnCompletion() throws SQLException {
		// TODO Auto-generated method stub
		
	}

	/* (non-Javadoc)
	 * @see java.sql.Statement#isCloseOnCompletion()
	 */
	public boolean isCloseOnCompletion() throws SQLException {
		// TODO Auto-generated method stub
		return false;
	}

}
