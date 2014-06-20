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

import it.jnrpe.Status;

/**
 * This is the exception that must be thrown if an error occurs gathering
 * metrics.
 *
 * @author Massimiliano Ziccardi
 *
 */
public class MetricGatheringException extends Exception {

    /**
     *
     */
    private static final long serialVersionUID = -8799905566378155453L;

    /**
     * The status to return to Nagios.
     */
    private final Status returnStatus;

    /**
     * Builds and initializes the exception.
     *
     * @param message
     *            The message to return to Nagios.
     * @param status
     *            The status to return to Nagios.
     * @param cause
     *            The original cause of the error.
     */
    public MetricGatheringException(final String message, final Status status, final Throwable cause) {
        super(message, cause);
        returnStatus = status;
    }

    /**
     * @return The status to be returned to Nagios.
     */
    public final Status getStatus() {
        return returnStatus;
    }

}
