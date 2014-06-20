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
package it.jnrpe.plugins;

/**
 * Exception throws when errors occurs accessing the plugin repository.
 * 
 * @author Massimiliano Ziccardi
 */
public class PluginRepositoryException extends Exception {

    /**
	 * 
	 */
    private static final long serialVersionUID = -440994387085530219L;

    /**
     * Constructor.
     */
    public PluginRepositoryException() {
        super();
    }

    /**
     * Constructor.
     * 
     * @param message
     *            the exception message
     * @param cause
     *            the error cause
     */
    public PluginRepositoryException(final String message, final Throwable cause) {
        super(message, cause);
    }

    /**
     * Constructor.
     * 
     * @param message
     *            the exception message
     */
    public PluginRepositoryException(final String message) {
        super(message);
    }

    /**
     * Constructor.
     * 
     * @param cause
     *            the error cause
     */
    public PluginRepositoryException(final Throwable cause) {
        super(cause);
    }

}
