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

import it.jnrpe.commands.CommandInvoker;
import it.jnrpe.events.EventsUtil;
import it.jnrpe.events.LogEvent;
import it.jnrpe.net.BadCRCException;
import it.jnrpe.net.JNRPERequest;
import it.jnrpe.net.JNRPEResponse;
import it.jnrpe.net.PacketVersion;
import it.jnrpe.utils.StreamManager;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.text.MessageFormat;

import javax.net.ssl.SSLSocket;

import org.apache.commons.lang.StringUtils;

/**
 * Thread used to server client request.
 * 
 * @author Massimiliano Ziccardi
 */
class JNRPEServerThread extends Thread {
	/**
	 * The socket used by this thread to read the request and write the answer.
	 */
	private final Socket attachedSocket;

	/**
	 * <code>true</code> if this thread must stop working as soon as possible.
	 */
	private Boolean stopTriggered = Boolean.FALSE;

	/**
	 * The command invoker to be used to serve the request.
	 */
	private final CommandInvoker commandInvoker;

	/**
	 * The source of the events (I.e. the JNRPE listeners that received the
	 * request).
	 */
	private JNRPEListenerThread parent = null;

	// /**
	// * The list of event listeners.
	// */
	// private Set<IJNRPEEventListener> listenersList;

	private JNRPEExecutionContext context;

	/**
	 * Builds and initializes a new server thread.
	 * 
	 * @param socket
	 *            The socket to be used to read and write
	 * @param cmdInvoker
	 *            The command invoker that will serve the request
	 */
	public JNRPEServerThread(final Socket socket,
			final CommandInvoker cmdInvoker) {
		super("JNRPEServerThread");
		this.attachedSocket = socket;
		this.commandInvoker = cmdInvoker;
	}

	/**
	 * Configures this server thread.
	 * 
	 * @param listenerThread
	 *            The listener that received the request
	 * @param vListeners
	 *            The event listeners
	 */
	void configure(final JNRPEListenerThread listenerThread,
			final JNRPEExecutionContext ctx) {
		parent = listenerThread;
		// listenersList = vListeners;
		context = ctx;
	}

	/**
	 * Utility method that splits using the '!' character and handling quoting
	 * by "'" and '"'.
	 * 
	 * @param sCommandLine
	 *            The command line string
	 * @return The splitted string
	 */
	private String[] split(final String sCommandLine) {
		return it.jnrpe.utils.StringUtils.split(sCommandLine, '!', false);
	}

	/**
	 * Serve the request.
	 * 
	 * @param req
	 *            The request
	 * @return The Response
	 */
	public JNRPEResponse handleRequest(final JNRPERequest req) {
		// extracting command name and params
		String[] partsAry = split(req.getStringMessage());

		String commandName = partsAry[0];
		String[] argsAry = new String[partsAry.length - 1];

		System.arraycopy(partsAry, 1, argsAry, 0, argsAry.length);

		ReturnValue ret = commandInvoker.invoke(commandName, argsAry);

		if (ret == null) {
			String args = StringUtils.join(argsAry, ',');

			ret = new ReturnValue(Status.UNKNOWN, "Command [" + commandName
					+ "] with args [" + args + "] returned null");
		}

		JNRPEResponse res = new JNRPEResponse();
		res.setPacketVersion(PacketVersion.VERSION_2);

		res.setResultCode(ret.getStatus().intValue());
		res.setMessage(ret.getMessage());
		res.updateCRC();

		String messageInvokedLog = MessageFormat.format(
				"Invoked command {0} - Status : "
						+ "{1} - Return Message : ''{2}''", commandName, ret
						.getStatus().name(), ret.getMessage());
		String paramTraceLog = MessageFormat.format("Arguments : ''{0}''",
				argsToString(argsAry));

		EventsUtil
				.sendEvent(context, parent, LogEvent.DEBUG, messageInvokedLog);
		EventsUtil.sendEvent(context, parent, LogEvent.TRACE, paramTraceLog);

		return res;
	}

	/**
	 * Utility to convert the arguments to a printable string.
	 * 
	 * @param args
	 *            The arguments array
	 * @return The printable string
	 */
	private String argsToString(final String[] args) {
		return new StringBuilder().append('[')
				.append(StringUtils.join(args, ',')).append(']').toString();
	}

	/**
	 * Runs the thread.
	 */
	@Override
	public void run() {
		StreamManager streamMgr = new StreamManager();

		try {
			InputStream in = streamMgr.handle(attachedSocket.getInputStream());
			JNRPEResponse res;
			JNRPERequest req;

			try {
				req = new JNRPERequest(in);

				switch (req.getPacketType()) {
				case QUERY:
					res = handleRequest(req);
					break;
				default:
					PacketVersion version = req.getPacketVersion();
					if (version == null) {
						version = PacketVersion.VERSION_2;
					}

					res = new JNRPEResponse();
					res.setPacketVersion(version);
					res.setResultCode(Status.UNKNOWN.intValue());
					res.setMessage("Invalid Packet Type");
					res.updateCRC();

				}

			} catch (BadCRCException e) {
				res = new JNRPEResponse();
				res.setPacketVersion(PacketVersion.VERSION_2);
				res.setResultCode(Status.UNKNOWN.intValue());
				res.setMessage("BAD REQUEST CRC");
				res.updateCRC();

			}

			synchronized (this) {
				if (!stopTriggered.booleanValue()) {
					OutputStream out = streamMgr.handle(attachedSocket
							.getOutputStream());
					out.write(res.toByteArray());
				}
			}
		} catch (IOException e) {
			// if (!m_bStopped.booleanValue())
			// m_Logger.error("ERROR DURING SOCKET OPERATION.", e);
			EventsUtil.sendEvent(context, parent, LogEvent.ERROR,
					"Error during socket operation", e);
		} finally {
			try {
				if (attachedSocket != null && !attachedSocket.isClosed()) {
					if (!(attachedSocket instanceof SSLSocket)) {
						attachedSocket.shutdownInput();
						attachedSocket.shutdownOutput();
					}
					attachedSocket.close();
				}
			} catch (IOException e) {
				// Ignore...
				// m_Logger.error("ERROR CLOSING SOCKET", e);
			}

			streamMgr.closeAll();
		}

	}

	/**
	 * Tries to stop the thread.
	 */
	public void stopNow() {
		StreamManager streamMgr = new StreamManager();
		try {
			synchronized (this) {
				// If the socket is closed, the thread has finished...
				if (!attachedSocket.isClosed()) {
					stopTriggered = Boolean.TRUE;

					try {
						JNRPEResponse res = new JNRPEResponse();
						res.setPacketVersion(PacketVersion.VERSION_2);
						res.setResultCode(Status.UNKNOWN.intValue());
						res.setMessage("Command execution timeout");
						res.updateCRC();

						OutputStream out = streamMgr.handle(attachedSocket
								.getOutputStream());
						out.write(res.toByteArray());

						// This is just to stop any socket operations...
						attachedSocket.close();
					} catch (IOException e) {
						// Intentionally ignored..
					}

					// Let's try to interrupt all other operations...
					if (this.isAlive()) {
						this.interrupt();
					}

					// We can exit now..
				}
			}
		} finally {
			streamMgr.closeAll();
		}
	}
}
