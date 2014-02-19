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
package it.jnrpe;

import it.jnrpe.commands.CommandInvoker;
import it.jnrpe.events.EventsUtil;
import it.jnrpe.events.IJNRPEEventListener;
import it.jnrpe.events.LogEvent;
import it.jnrpe.utils.StreamManager;

import java.io.IOException;
import java.io.InputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.net.ServerSocketFactory;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLException;
import javax.net.ssl.SSLServerSocket;
import javax.net.ssl.SSLServerSocketFactory;

/**
 * Thread that listen on a given IP:PORT. When a request is received, a
 * {@link JNRPEServerThread} is created to serve it.
 *
 * @author Massimiliano Ziccardi
 */
class JNRPEListenerThread extends Thread implements IJNRPEListener {

    /**
     * The default So Linger timeout (in seconds).
     */
    private static final int SOLINGER_TIMEOUT = 10;

    /**
     * Default time to wait before killing staled commands (milliseconds).
     */
    private static final int DEFAULT_COMMAND_EXECUTION_TIMEOUT = 20000;

    /**
     * The ServerSocket.
     */
    private ServerSocket serverSocket = null;

    /**
     * The list of all the accepted clients.
     */
    private final List<InetAddress> acceptedHostsList =
            new ArrayList<InetAddress>();

    /**
     * The thread factory to be used to create server threads.
     */
    private ThreadFactory threadFactory = null;

    /**
     * The address to bind to.
     */
    private final String bindingAddress;
    /**
     * The port to listen to.
     */
    private final int bindingPort;
    /**
     * The command invoker to be used to serve the requests.
     */
    private final CommandInvoker commandInvoker;

    /**
     * <code>true</code> if the connection must be encrypted.
     */
    private boolean useSSL = false;

    /**
     * The command execution timeout (milliseconds).
     */
    private final int commandExecutionTimeout =
            DEFAULT_COMMAND_EXECUTION_TIMEOUT;

    /**
     * The default keystore file name.
     */
    private static final String KEYSTORE_NAME = "keys.jks";
    /**
     * The default ketstore password.
     */
    private static final String KEYSTORE_PWD = "p@55w0rd";

    /**
     * The set of event listeners.
     */
    private Set<IJNRPEEventListener> eventListenersList = null;

    /**
     * Set to true if the server is shutting down.
     */
    private boolean shutdownTriggered = false;

    /**
     * Builds a listener thread.
     *
     * @param eventListeners
     *            The event listeners
     * @param newBindingAddress
     *            The address to bind to
     * @param newBindingPort
     *            The port to bind to
     * @param newCommandInvoker
     *            The command invoker to be used to serve the request
     */
    JNRPEListenerThread(final Set<IJNRPEEventListener> eventListeners,
            final String newBindingAddress, final int newBindingPort,
            final CommandInvoker newCommandInvoker) {
        this.bindingAddress = newBindingAddress;
        this.bindingPort = newBindingPort;
        this.commandInvoker = newCommandInvoker;
        this.eventListenersList = eventListeners;
    }

    /**
     * Enables the SSL.
     */
    public void enableSSL() {
        useSSL = true;
    }

    /**
     * Creates an SSLServerSocketFactory.
     *
     * @return the newly creates SSL Server Socket Factory
     * @throws KeyStoreException -
     * @throws CertificateException -
     * @throws IOException -
     * @throws UnrecoverableKeyException -
     * @throws KeyManagementException -
     */
    private SSLServerSocketFactory getSSLSocketFactory()
            throws KeyStoreException, CertificateException, IOException,
            UnrecoverableKeyException, KeyManagementException {

        // Open the KeyStore Stream
        StreamManager h = new StreamManager();

        SSLContext ctx;
        KeyManagerFactory kmf;

        try {
            InputStream ksStream =
                    getClass().getClassLoader().getResourceAsStream(
                            KEYSTORE_NAME);
            h.handle(ksStream);
            ctx = SSLContext.getInstance("SSLv3");

            kmf =
                    KeyManagerFactory.getInstance(KeyManagerFactory
                            .getDefaultAlgorithm());

            KeyStore ks = KeyStore.getInstance("JKS");
            char[] passphrase = KEYSTORE_PWD.toCharArray();
            ks.load(ksStream, passphrase);

            kmf.init(ks, passphrase);
            ctx.init(kmf.getKeyManagers(), null,
                    new java.security.SecureRandom());
        } catch (NoSuchAlgorithmException e) {
            throw new SSLException("Unable to initialize SSLSocketFactory.\n"
                    + e.getMessage());
        } finally {
            h.closeAll();
        }

        return ctx.getServerSocketFactory();
    }

    /**
     * Initializes the object.
     *
     * @throws IOException
     *             -
     * @throws KeyManagementException
     *             -
     * @throws KeyStoreException
     *             -
     * @throws CertificateException
     *             -
     * @throws UnrecoverableKeyException
     *             -
     */
    private void init() throws IOException, KeyManagementException,
            KeyStoreException, CertificateException, UnrecoverableKeyException {
        InetAddress addr = InetAddress.getByName(bindingAddress);
        ServerSocketFactory sf = null;

        if (useSSL) {
            sf = getSSLSocketFactory();
            // sf = getSSLSocketFactory(m_Binding.getKeyStoreFile(),
            // m_Binding.getKeyStorePassword(), "JKS");
        } else {
            sf = ServerSocketFactory.getDefault();
        }

        serverSocket = sf.createServerSocket(bindingPort, 0, addr);
        if (serverSocket instanceof SSLServerSocket) {
            ((SSLServerSocket) serverSocket)
                    .setEnabledCipherSuites(((SSLServerSocket) serverSocket)
                            .getSupportedCipherSuites());
        }

        // Init the thread factory
        threadFactory =
                new ThreadFactory(commandExecutionTimeout, commandInvoker);
    }

    /**
     * Adds an host to the list of accepted hosts.
     *
     * @param sHost
     *            The hostname or IP
     * @throws UnknownHostException
     *             thrown if the host name can't be translated to an IP.
     */
    public void addAcceptedHosts(final String sHost)
            throws UnknownHostException {
        InetAddress addr = InetAddress.getByName(sHost);
        acceptedHostsList.add(addr);
    }

    /**
     * Executes the thread.
     */
    @Override
    public void run() {
        try {
            init();

            StringBuffer msg = new StringBuffer("Listening on ");

            if (useSSL) {
                msg = msg.append("SSL/");
            }

            msg = msg.append(bindingAddress).append(":").append(bindingPort);

            EventsUtil.sendEvent(eventListenersList, this, LogEvent.INFO,
                    msg.toString());

            while (true) {
                Socket clientSocket = serverSocket.accept();
                clientSocket.setSoLinger(false, SOLINGER_TIMEOUT);
                clientSocket.setSoTimeout(commandExecutionTimeout);

                if (!canAccept(clientSocket.getInetAddress())) {
                    clientSocket.close();
                    continue;
                }

                JNRPEServerThread kk =
                        threadFactory.createNewThread(clientSocket);
                kk.configure(this, eventListenersList);
                kk.start();
            }
        } catch (SocketException se) {
            if (!shutdownTriggered) {
                EventsUtil.sendEvent(eventListenersList, this, LogEvent.ERROR,
                        "Unable to listen on " + bindingAddress + ":"
                                + bindingPort + ": " + se.getMessage(), se);
            }

            // This exception is thrown when the server socket is closed.
            // Ignoring

        } catch (Throwable e) {
            EventsUtil.sendEvent(eventListenersList, this, LogEvent.ERROR,
                    e.getMessage(), e);
        }

        exit();
    }

    /**
     * Closes the listener.
     */
    private synchronized void exit() {
        serverSocket = null;
        notifyAll();
        EventsUtil.sendEvent(eventListenersList, this, LogEvent.INFO,
                "Listener Closed");
    }

    /**
     * @see it.jnrpe.IJNRPEListener#close()
     */
    public synchronized void shutdown() {
        shutdownTriggered = true;

        try {
            if (serverSocket != null) {
                serverSocket.close();

                while (serverSocket != null) {
                    wait();
                }
            }

        } catch (InterruptedException ie) {
        } catch (IOException e) {
        }
    }

    /**
     * Returns <code>true</code> if the request must be accepted.
     *
     * @param inetAddress
     *            The client IP address
     * @return <code>true</code> if the request must be accepted.
     */
    private boolean canAccept(final InetAddress inetAddress) {
        for (InetAddress addr : acceptedHostsList) {
            if (addr.equals(inetAddress)) {
                return true;
            }
        }

        // System.out.println ("Refusing connection to " + inetAddress);
        EventsUtil.sendEvent(eventListenersList, this, LogEvent.INFO,
                "Connection refused from : " + inetAddress);

        return false;
    }
}
