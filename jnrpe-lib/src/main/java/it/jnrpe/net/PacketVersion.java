/*
 * Copyright (c) 2008 Massimiliano Ziccardi Licensed under the Apache License,
 * Version 2.0 (the "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0 Unless required by applicable law
 * or agreed to in writing, software distributed under the License is
 * distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the specific language
 * governing permissions and limitations under the License.
 */
package it.jnrpe.net;

/**
 * Enumeration of all the supported packet versions.
 *
 * @author Massimiliano Ziccardi
 */
public enum PacketVersion {
    /**
     * Packet version 1: no longer supported.
     */
    VERSION_1(1),
    /**
     * Packet version 2.
     */
    VERSION_2(2);

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
    PacketVersion(final int value) {
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
     * Returns the enum corresponding with the given int value.
     *
     * @param value
     *            The int value to be converted
     * @return A {@link PacketVersion} enum. Null if not found
     */
    public static PacketVersion fromIntValue(final int value) {
        switch (value) {
        case 1:
            return VERSION_1;
        case 2:
            return VERSION_2;
        default:
            return null;
        }
    }
}
