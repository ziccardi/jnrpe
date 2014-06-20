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

/**
 * Holds the binding address informations.
 *
 * @author Massimiliano Ziccardi
 */
class BindAddress {
    /**
     * <code>true</code> if SSL must be used.
     */
    private final boolean useSSL;

    /**
     * The binding IP or URL.
     */
    private final String bindingAddress;

    /**
     * Constructs the object.
     *
     * @param address
     *            The binding IP or URL
     */
    public BindAddress(final String address) {
        bindingAddress = address;
        useSSL = true;
    }

    /**
     * Constructs the object.
     *
     * @param address
     *            The binding IP or URL
     * @param ssl
     *            <code>true</code> if SSL must be used.
     */
    public BindAddress(final String address, final boolean ssl) {
        bindingAddress = address;
        useSSL = ssl;
    }

    /**
     * @return the binding address/url
     */
    public String getBindingAddress() {
        return bindingAddress;
    }

    /**
     * @return <code>true</code> if SSL must be used
     */
    public boolean isSSL() {
        return useSSL;
    }
}
