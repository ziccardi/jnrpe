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
package it.jnrpe.server;

import java.util.ArrayList;
import java.util.List;

/**
 * Represent the JNRPE configuration server section.
 * 
 * @author Massimiliano Ziccardi
 */
public class ServerSection {

    /**
     * The default backlog size.
     */
    public final static int DEFAULT_BACKLOG = 128;

    /**
     * <code>true</code> if dynamic parameters ($ARGx$ macros) must be
     * interpolated.
     */
    private boolean acceptParams;

    /**
     * The plugins directory path.
     */
    private String pluginPath;

    /**
     * The list of all the binding where JNRPE must listen on.
     */
    private final List<BindAddress> bindingsList = new ArrayList<BindAddress>();

    /**
     * The list of all the addresses (IP/URL) JNRPE must accept requests from.
     */
    private final List<String> allowedAddressesList = new ArrayList<String>();

    /**
     * The maximum number connections.
     */
    private int backlogSize = DEFAULT_BACKLOG;

    /**
     * Read timeout in seconds.
     */
    private Integer readTimeout = null;

    /**
     * Write timeout in seconds.
     */
    private Integer writeTimeout = null;

    /**
     * @return all the configured binding addresses.
     */
    public final List<BindAddress> getBindAddresses() {
        return bindingsList;
    }

    /**
     * Adds a binding address to the list. The format of a binding address is
     * [SSL/]address[:port]:
     * <ul>
     * <li>SSL/ : if present means than JNRPE must create an SSL socket
     * <li>:port : is the port where JNRPE must listen to
     * </ul>
     * 
     * @param bindAddress
     *            The address to add
     */
    final void addBindAddress(final String bindAddress) {

        boolean ssl = bindAddress.toUpperCase().startsWith("SSL/");

        String sAddress;

        if (ssl) {
            sAddress = bindAddress.substring("SSL/".length());
        } else {
            sAddress = bindAddress;
        }

        addBindAddress(sAddress, ssl);
    }

    /**
     * Adds a binding address to the list of binding address.
     * 
     * @param bindAddress
     *            The IP/URL
     * @param useSSL
     *            <code>true</code> if SSL must be used
     */
    final void addBindAddress(final String bindAddress, final boolean useSSL) {
        bindingsList.add(new BindAddress(bindAddress, useSSL));
    }

    /**
     * @return the list of allowed IP address client
     */
    public final List<String> getAllowedAddresses() {
        return allowedAddressesList;
    }

    /**
     * Adds an address to the list of allowed clients.
     * 
     * @param address
     *            The IP/URL that must be accepted
     */
    final void addAllowedAddress(final String address) {
        allowedAddressesList.add(address);
    }

    /**
     * Returns whether this server must resolve $ARGx$ macros or not.
     * 
     * @return <code>true</code> if macros must be resolved
     */
    public final boolean acceptParams() {
        return acceptParams;
    }

    public final int getBacklogSize() {
        return this.backlogSize;
    }

    public final int getReadTimeout() {
        return readTimeout;
    }

    public final int getWriteTimeout() {
        return writeTimeout;
    }

    /**
     * Sets whether this server must resolve $ARGx$ macros or not.
     * 
     * @param acceptParms
     *            pass <code>true</code> if macros must be resolved.
     */
    final void setAcceptParams(final boolean acceptParms) {
        this.acceptParams = acceptParms;
    }

    /**
     * @return the path to the directory where all plugins are istalled.
     */
    public final String getPluginPath() {
        return pluginPath;
    }

    /**
     * Sets the path to the directory where all plugins are istalled.
     * 
     * @param pluginDirectoryPath
     *            The path to the plugins installation directory
     */
    final void setPluginPath(final String pluginDirectoryPath) {
        this.pluginPath = pluginDirectoryPath;
    }

    /**
     * Sets the maximum number of accepted connections. Default is
     * {@value #DEFAULT_BACKLOG}.
     * 
     * @param backLogSize
     */
    final void setBackLogSize(final int backLogSize) {
        this.backlogSize = backLogSize;
    }

    final void setReadTimeout(final int readTimeout) {
        this.readTimeout = readTimeout;
    }

    final void setWriteTimeout(final int writeTimeout) {
        this.writeTimeout = writeTimeout;
    }
}
