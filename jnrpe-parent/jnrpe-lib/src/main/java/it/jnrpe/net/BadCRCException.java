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
package it.jnrpe.net;

/**
 * This exception is thrown if the response CRC or the request CRC ccould not be
 * verified.
 *
 * @author Massimiliano Ziccardi
 *
 */
public class BadCRCException extends Exception {
    /**
     *
     */
    private static final long serialVersionUID = -4870060988392897195L;

    /**
     * Initialize the exception with an emtpy error message.
     */
    public BadCRCException() {
        super();
    }

    /**
     * Initialize the exception with the given error message and the give root
     * cause.
     *
     * @param message
     *            The error message
     * @param cause
     *            The root exception
     */
    public BadCRCException(final String message, final Throwable cause) {
        super(message, cause);
    }

    /**
     * Initialize the exception with the given error message.
     *
     * @param message
     *            The error message
     */
    public BadCRCException(final String message) {
        super(message);
    }

    /**
     * Initialize the exception with the given root cause.
     *
     * @param cause
     *            The root cause
     */
    public BadCRCException(final Throwable cause) {
        super(cause);
    }

}
