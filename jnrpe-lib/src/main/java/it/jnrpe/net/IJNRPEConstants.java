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
 * In this interface are initialized all needed constants.
 *
 * @author Massimiliano Ziccardi

 * @deprecated since <unknown>
 * @version $Revision: 1.0 $
 */
public interface IJNRPEConstants {
    /**
     * Contains the version of the software.
     */
    String VERSION = "0.1/2.0";

    /**
     * Service status 'UNKNOWN'.
     */
    int STATE_UNKNOWN = 3;
    /**
     * Service status 'CRITICAL'.
     */
    int STATE_CRITICAL = 2;
    /**
     * Service status 'WARNING'.
     */
    int STATE_WARNING = 1;
    /**
     * Service status 'OK'.
     */
    int STATE_OK = 0;

}
