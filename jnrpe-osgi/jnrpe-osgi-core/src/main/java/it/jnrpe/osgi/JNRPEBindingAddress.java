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
package it.jnrpe.osgi;

class JNRPEBindingAddress {

    private boolean ssl = false;
    private String address;
    private int port = 5667;

    JNRPEBindingAddress(final String bindingAddress) {
        init(bindingAddress);
    }

    private void init(final String bindingAddress) {

        String addr = bindingAddress;

        ssl = addr.startsWith("SSL/");
        if (ssl) {
            addr = addr.substring(4);
        }

        if (addr.indexOf(':') != -1) {
            String[] components = addr.split(":");
            address = components[0];
            port = Integer.parseInt(components[1]);
        } else {
            address = addr;
        }
    }

    protected boolean isSsl() {
        return ssl;
    }

    protected String getAddress() {
        return address;
    }

    protected int getPort() {
        return port;
    }
}
