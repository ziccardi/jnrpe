/*
 * Copyright (c) 2013 Massimiliano Ziccardi Licensed under the Apache License,
 * Version 2.0 (the "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0 Unless required by applicable law
 * or agreed to in writing, software distributed under the License is
 * distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the specific language
 * governing permissions and limitations under the License.
 */
package it.jnrpe.plugins;

/**
 * Exception thrown in case of plugin configuration errors.
 *
 * @author Massimiliano Ziccardi
 */
public class PluginConfigurationException extends Exception {
    /**
     *
     */
    private static final long serialVersionUID = -9096977896397218216L;

    /**
     * Default constructor.
     */
    public PluginConfigurationException() {
        super();
    }

    /**
     * Initializes the exception with a message and an optional root cause.
     *
     * @param message
     *            The exception message
     * @param cause
     *            The root cause
     */
    public PluginConfigurationException(final String message,
            final Throwable cause) {
        super(message, cause);
    }

    /**
     * Initializes the exception with a message.
     *
     * @param message
     *            The exception message
     */
    public PluginConfigurationException(final String message) {
        super(message);
    }

    /**
     * Initializes the exception with a cause. The message is inherited from the
     * root cause.
     *
     * @param cause
     *            The root cause.
     */
    public PluginConfigurationException(final Throwable cause) {
        super(cause);
    }

}
