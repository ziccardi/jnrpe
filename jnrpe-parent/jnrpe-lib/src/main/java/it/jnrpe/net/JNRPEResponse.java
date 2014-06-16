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
import java.util.Arrays;

/**
 * This object represent a generic response packet.
 * 
 * @author Massimiliano Ziccardi
 */
public final class JNRPEResponse extends JNRPEProtocolPacket {
	/**
	 * Default constructor.
	 */
	public JNRPEResponse() {
		super();
		setPacketType(PacketType.RESPONSE);
	}

	/**
	 * Builds a JNRPE response reading the content from an InputStream.
	 * 
	 * @param in
	 *            The InputStream to read from
	 * 
	 * @throws IOException
	 *             on any io exception
	 */
	public JNRPEResponse(final InputStream in) throws IOException {
		super();
		fromInputStream(in);
	}

	/**
	 * Sets the message to be included in the response.
	 * 
	 * @param message
	 *            the response message
	 */
	public void setMessage(final String message) {
		setBuffer(message);
	}

	/**
	 * Returns the response message.
	 * 
	 * @return the response message
	 */
	public String getMessage() {
		return getPacketString();
	}

	@Override
	public String toString() {

		return "JNRPEResponse [getPacketType()=" + getPacketType()
				+ ", getPacketVersion()=" + getPacketVersion()
				+ ", getResultCode()=" + getResultCode() + ", getBuffer()="
				+ Arrays.toString(getBuffer()) + "]";
	}

}
