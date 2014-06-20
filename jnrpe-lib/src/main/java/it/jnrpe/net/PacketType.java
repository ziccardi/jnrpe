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
 * Enumeration of all the supported packet types.
 * 
 * @author Massimiliano Ziccardi
 */
public enum PacketType {
    /**
     * Id code for a packet containing a query.
     */
    QUERY(1),
    /**
     * Id code for a packet containing a response.
     */
    RESPONSE(2),
    /**
     * Id code for an unknown packet type.
     */
    UNKNOWN(99);

    /**
     * The enumeration int value.
     */
    private final int intValue;

    /**
     * Builds an enumeration with the given int value.
     * 
     * @param value
     *            The value
     */
    PacketType(final int value) {
        intValue = value;
    }

    /**
     * Returns the int value of the enum.
     * 
     * @return The int value
     */
    public int intValue() {
        return intValue;
    }

    /**
     * Converts from the protocol int value representing the state to the
     * enumeration constant.
     * 
     * @param value
     *            The int value state
     * @return The Enumeration
     */
    public static PacketType fromIntValue(final int value) {
        switch (value) {
        case 1:
            return QUERY;
        case 2:
            return RESPONSE;
        default:
            return UNKNOWN;
        }
    }
}
