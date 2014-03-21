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

import java.io.IOException;
import java.io.InputStream;
import java.util.zip.CRC32;

import org.apache.commons.lang.StringUtils;

/**
 * This object represent a generic request packet.
 *
 * @author Massimiliano Ziccardi
 */
public class JNRPERequest extends JNRPEProtocolPacket {
    /**
     * This constructor initializes the object with the data read from the given
     * input stream.
     *
     * @param in
     *            The stream containing the data to be parsed
     * @throws IOException
     *             On any IO exception
     * @throws BadCRCException
     *             If the CRC can't be validated
     */
    public JNRPERequest(final InputStream in) throws IOException,
            BadCRCException {
        fromInputStream(in);

        validate();
    }

    /**
     * Inizialize the request with the supplied command and command arguments.
     *
     * @param commandName
     *            The command
     * @param arguments
     *            The arguments
     */
    public JNRPERequest(final String commandName, final String... arguments) {
        init(commandName, arguments);
    }

    /**
     * Inizialize the request with the supplied command and command arguments.
     * The arguments must be separated by an exclamation mark ('!')
     *
     * @param commandName
     *            The command
     * @param sArguments
     *            The arguments
     */
    JNRPERequest(final String commandName, final String sArguments) {
        init(commandName, sArguments);
    }

    /**
     * Initializes the object with the given command and the given arguments.
     *
     * The arguments gets quoted if they contains '!' and are then joined using
     * the '!' as separator.
     *
     * @param commandName
     *            The command
     * @param arguments
     *            The arguments
     */
    private void init(final String commandName, final String... arguments) {
        if (arguments != null) {
            if (arguments.length == 1) {
                init(commandName, arguments[0]);
                return;
            }

            String[] ary = new String[arguments.length];

            for (int i = 0; i < arguments.length; i++) {
                if (arguments[i].indexOf('!') == -1) {
                    ary[i] = arguments[i];
                } else {
                    ary[i] = "'" + arguments[i] + "'";
                }
            }
            // sCommandBytes = StringUtils.join(ary, '!');
            init(commandName, StringUtils.join(ary, '!'));
        } else {
            init(commandName, (String) null);
        }
    }

    /**
     * Initializes the object with the given command and the given list of '!'
     * separated list of arguments.
     *
     * @param commandName
     *            The command
     * @param argumentsString
     *            The arguments
     */
    private void init(final String commandName, final String argumentsString) {
        String fullCommandString;

        String tmpArgumentsString = argumentsString;

        if (tmpArgumentsString != null && tmpArgumentsString.startsWith("!")) {
            tmpArgumentsString = tmpArgumentsString.substring(1);
        }

        if (!StringUtils.isBlank(tmpArgumentsString)) {
            fullCommandString = commandName + "!" + tmpArgumentsString;
        } else {
            fullCommandString = commandName;
        }

        setPacketVersion(PacketVersion.VERSION_2);
        super.setPacketType(PacketType.QUERY);
        super.initRandomBuffer();
        super.setDataBuffer(fullCommandString);
        updateCRC();
    }

    /**
     * Updates the CRC value.
     */
    // TODO : move into base class....
    private void updateCRC() {
        setCRC(0);
        int crc = 0;

        CRC32 crcAlg = new CRC32();
        crcAlg.update(toByteArray());

        crc = (int) crcAlg.getValue();

        setCRC(crc);
    }

}
