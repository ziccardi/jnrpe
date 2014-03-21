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
package it.jnrpe.plugin.mysql;

import it.jnrpe.ICommandLine;

import java.sql.Connection;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Helper class to connect to a mysql database.
 *
 * @author Frederico Campos
 */
public class Mysql {
    /*
     * Helper class to connect to a Msysql database
     *
     * Author: Frederico Campos
     */

    /**
     * Db hostname.
     */
    private String hostname;

    /**
     * Db port number.
     */
    private String port;

    /**
     * Db username.
     */
    private String username;

    /**
     * Db password.
     */
    private String password;

    /**
     * Db name.
     */
    private String database;

    /**
     * Constructs and initializes.
     *
     * @param cl
     *            The command line
     */
    public Mysql(final ICommandLine cl) {
        this.database = "mysql";
        if (cl.hasOption("database")) {
            this.database = cl.getOptionValue("database");
        }
        this.hostname = "localhost";
        if (cl.hasOption("hostname")
                && !"".equals(cl.getOptionValue("hostname"))) {
            this.hostname = cl.getOptionValue("hostname");
        }
        this.port = "3306";
        if (cl.hasOption("port") && !"".equals(cl.getOptionValue("port"))) {
            this.port = cl.getOptionValue("port");
        }
        this.password = "";
        if (cl.hasOption("password")) {
            this.password = cl.getOptionValue("password");
        } else {
            // find password from my.cfg or my.ini
        }
        this.username = "";
        if (cl.hasOption("user")) {
            this.username = cl.getOptionValue("user");
        }
    }

    /**
     * Contructs the object with the given paramters.
     *
     * @param serverHostname
     *            The MySQL hostname
     * @param serverPort
     *            The MySQL port
     * @param dbUsername
     *            The database username
     * @param dbPassword
     *            The database password
     * @param databaseName
     *            The database name
     */
    public Mysql(final String serverHostname, final String serverPort,
            final String dbUsername, final String dbPassword,
            final String databaseName) {
        this.hostname = serverHostname;
        this.port = serverPort;
        this.username = dbUsername;
        this.password = dbPassword;
        this.database = databaseName;
    }

    /**
     * Get database connection.
     *
     * @return The connection
     * @throws SQLException
     *             -
     * @throws InstantiationException
     *             -
     * @throws IllegalAccessException
     *             -
     * @throws ClassNotFoundException
     *             -
     */
    public final Connection getConnection() throws SQLException,
            InstantiationException, IllegalAccessException,
            ClassNotFoundException {
        String url =
                "jdbc:mysql://"
                        + this.hostname
                        + ":"
                        + this.port
                        + "/"
                        + this.database
                        + "?user="
                        + this.username
                        + "&password="
                        + this.password
                        + "&autoReconnect=true"
                        + "&failOverReadOnly=false&maxReconnects=3";
        DriverManager.registerDriver((Driver) Class.forName(
                "com.mysql.jdbc.Driver").newInstance());
        Connection conn = DriverManager.getConnection(url);
        return conn;
    }

    /**
     * Closes the connection.
     *
     * @param conn
     *            The connection
     */
    public final void closeConnection(final Connection conn) {
        try {
            if (conn != null) {
                conn.close();
            }
        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    /**
     * Returns the hostname.
     *
     * @return the hostname
     */
    public final String getHostname() {
        return hostname;
    }

    /**
     * Sets the hostname.
     *
     * @param host
     *            the hostname to set
     */
    public final void setHostname(final String host) {
        this.hostname = host;
    }

    /**
     * @return the port
     */
    public final String getPort() {
        return port;
    }

    /**
     * @param serverPort
     *            the port to set
     */
    public final void setPort(final String serverPort) {
        this.port = serverPort;
    }

    /**
     * @return the username
     */
    public final String getUsername() {
        return username;
    }

    /**
     * @param dbUsername
     *            the username to set
     */
    public final void setUsername(final String dbUsername) {
        this.username = dbUsername;
    }

    /**
     * @return the password
     */
    public final String getPassword() {
        return password;
    }

    /**
     * @param dbPassword
     *            the password to set
     */
    public final void setPassword(final String dbPassword) {
        this.password = dbPassword;
    }

    /**
     * @return the database
     */
    public final String getDatabase() {
        return database;
    }

    /**
     * @param dbName
     *            the database to set
     */
    public final void setDatabase(final String dbName) {
        this.database = dbName;
    }
}
