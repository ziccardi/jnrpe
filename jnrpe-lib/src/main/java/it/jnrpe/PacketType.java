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
package it.jnrpe;

/**
 * Enums all the known packet types.
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
    RESPONSE(2);

    /**
     * The int value of the enumeration constant.
     */
    private final int intValue;

    /**
     * Creates an enumeration constant with the given value.
     *
     * @param value
     *            the constant value
     */
    PacketType(final int value) {
        intValue = value;
    }

    /**
     * Return the integer value of the constant.
     *
     * @return The integer value
     */
    public int intValue() {
        return intValue;
    }

    /**
     * Convert from the passed in integer value to the corresponding enumeration
     * constant.
     *
     * @param value
     *            The value to be converted
     * @return The corresponding enumeration constant
     */
    public static PacketType fromIntValue(final int value) {
        switch (value) {
        case 1:
            return QUERY;
        case 2:
            return RESPONSE;
        default:
            return null;
        }
    }
}
