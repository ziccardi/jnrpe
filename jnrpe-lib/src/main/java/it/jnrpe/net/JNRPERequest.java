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

import it.jnrpe.net.packet.IJNRPEProtocolPacket;
import it.jnrpe.net.packet.JNRPEProtocolPacketFactory;
import org.apache.commons.lang.StringUtils;

/**
 * This object represent a generic request packet.
 * 
 * @author Massimiliano Ziccardi
 * @version $Revision: 1.0 $
 */
public class JNRPERequest {

    private IJNRPEProtocolPacket packet;

    JNRPERequest(IJNRPEProtocolPacket packet) {
        this.packet = packet;
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
        this.packet = JNRPEProtocolPacketFactory.createNew();
        this.packet.setType(PacketType.QUERY);
        init(commandName, arguments);
        this.packet.updateCRC();
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

        if (tmpArgumentsString != null && !tmpArgumentsString.isEmpty() && tmpArgumentsString.charAt(0) == '!') {
            tmpArgumentsString = tmpArgumentsString.substring(1);
        }

        if (!StringUtils.isBlank(tmpArgumentsString)) {
            fullCommandString = commandName + "!" + tmpArgumentsString;
        } else {
            fullCommandString = commandName;
        }

        this.packet.setType(PacketType.QUERY);
        this.packet.setBuffer(fullCommandString);
        // updateCRC();
    }

    /**
     * Returns the query command.
     * 
    
     * @return the query command */
    public final String getCommand() {
        // extracting command
        String[] partsAry = split(this.packet.getBufferAsString());

        return partsAry[0];
    }

    /**
     * Returns the command arguments.
     * 
    
     * @return the command arguments */
    public final String[] getArguments() {
        // extracting params
        String[] partsAry = split(this.packet.getBufferAsString());
        String[] argsAry = new String[partsAry.length - 1];

        System.arraycopy(partsAry, 1, argsAry, 0, argsAry.length);

        return argsAry;
    }

    /**
     * Utility method that splits using the '!' character and handling quoting
     * by "'" and '"'.
     * 
     * @param sCommandLine
     *            The command line string
    
     * @return The splitted string */
    private String[] split(final String sCommandLine) {
        return it.jnrpe.utils.StringUtils.split(sCommandLine, '!', false);
    }

    /**
     * Method toString.
     * @return String
     */
    @Override
    public String toString() {
        return "JNRPERequest []";
    }

    public byte[] toByteArray() {
        return this.packet.toByteArray();
    }

    public boolean isValid() {
        return this.packet.isValid();
    }
}
