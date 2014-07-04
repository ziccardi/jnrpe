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

    public DriverPropertyInfo[] getPropertyInfo(String url, Properties info) throws SQLException {
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
        return obj.getClass() == getClass();
    }
}
