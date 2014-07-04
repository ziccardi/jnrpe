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

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.ssl.SslHandler;
import io.netty.handler.timeout.IdleStateHandler;
import it.jnrpe.commands.CommandInvoker;
import it.jnrpe.commands.CommandRepository;
import it.jnrpe.events.JNRPEStatusEvent;
import it.jnrpe.events.JNRPEStatusEvent.STATUS;
import it.jnrpe.net.JNRPEIdleStateHandler;
import it.jnrpe.net.JNRPERequestDecoder;
import it.jnrpe.net.JNRPEResponseEncoder;
import it.jnrpe.net.JNRPEServerHandler;
import it.jnrpe.plugins.IPluginRepository;
import it.jnrpe.utils.StreamManager;

import java.io.IOException;
import java.io.InputStream;
import java.net.UnknownHostException;
import java.nio.charset.Charset;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.util.ArrayList;
import java.util.Collection;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLEngine;
import javax.net.ssl.SSLException;

/**
 * This class is the real JNRPE worker. It must be used to start listening for
 * NRPE requests
 * 
 * @author Massimiliano Ziccardi
 */
public final class JNRPE {

    /**
     * The JNRPE logger.
     */
    private final JNRPELogger LOG = new JNRPELogger(this);
    
    /**
     * The current execution context.
     */
    private final IJNRPEExecutionContext context;
    
    /**
     * Default number of accepted connections.
     */
    static final int DEFAULT_MAX_ACCEPTED_CONNECTIONS = 128;

    /**
     * The boss group (see netty documentation).
     */
    private final EventLoopGroup bossGroup = new NioEventLoopGroup();

    /**
     * The worker group (see netty documentation).
     */
    private final EventLoopGroup workerGroup = new NioEventLoopGroup();

    /**
     * The default keystore name (used to perform SSL).
     */
    private static final String KEYSTORE_NAME = "keys.jks";

    /**
     * The default keystore password.
     */
    private static final String KEYSTORE_PWD = "p@55w0rd";

    /**
     * The plugin repository to be used to find the requested plugin.
     */
    private final IPluginRepository pluginRepository;
    /**
     * The command repository to be used to find the requested command.
     */
    private final CommandRepository commandRepository;

    /**
     * The list of accepted clients.
     */
    private Collection<String> acceptedHostsList = new ArrayList<String>();

    /**
     * Charset that will be used by JNRPE.
     */
    private final Charset charset;

    /**
     * The maximum number of concurrent connection allowed by JNRPE.
     */
    private final int maxAcceptedConnections;

    /**
     * The maximum number of seconds that JNRPE will wait for a client to send a
     * command.
     */
    private final int readTimeout;

    /**
     * The maximum number of seconds that JNRPE will wait for a plugin to
     * produce a result.
     */
    private final int writeTimeout;

    /**
     * <code>true</code> if $ARGxx$ macros must be expanded.
     */
    private final boolean acceptParams;

    /**
     * Instantiates the JNRPE engine.
     * 
     * @param pluginRepo
     *            The plugin repository object
     * @param commandRepo
     *            The command repository object
     * 
     * @deprecated This constructor will be removed as of version 2.0.5. Use
     *             {@link JNRPEBuilder} instead
     */
    @Deprecated
    public JNRPE(final IPluginRepository pluginRepo, final CommandRepository commandRepo) {
        if (pluginRepo == null) {
            throw new IllegalArgumentException("Plugin repository cannot be null");
        }

        if (commandRepo == null) {
            throw new IllegalArgumentException("Command repository cannot be null");
        }
        pluginRepository = pluginRepo;
        commandRepository = commandRepo;
        charset = Charset.forName("UTF-8");
        this.acceptParams = true;
        this.maxAcceptedConnections = DEFAULT_MAX_ACCEPTED_CONNECTIONS;
        readTimeout = 10;
        writeTimeout = 60;
        this.context = new JNRPEExecutionContext(new JNRPEEventBus(), charset, pluginRepo, commandRepo);
    }

    /**
     * Initializes the JNRPE worker.
     * 
     * @param pluginRepo
     *            The repository containing all the installed plugins
     * @param commandRepo
     *            The repository containing all the configured commands.
     * @param newCharset
     *            The charset that will be used by JNRPE
     * @param acceptParameters
     *            Sets if $ARGxx$ macros should be expanded
     * @deprecated This constructor will be removed as of version 2.0.5. Use
     *             {@link JNRPEBuilder} instead
     */
    @Deprecated
    public JNRPE(final IPluginRepository pluginRepo, final CommandRepository commandRepo, final Charset newCharset, final boolean acceptParameters) {
        if (pluginRepo == null) {
            throw new IllegalArgumentException("Plugin repository cannot be null");
        }

        if (commandRepo == null) {
            throw new IllegalArgumentException("Command repository cannot be null");
        }
        pluginRepository = pluginRepo;
        commandRepository = commandRepo;
        this.charset = newCharset;
        this.acceptParams = acceptParameters;
        this.maxAcceptedConnections = DEFAULT_MAX_ACCEPTED_CONNECTIONS;
        readTimeout = 10;
        writeTimeout = 60;
        this.context = new JNRPEExecutionContext(new JNRPEEventBus(), newCharset, pluginRepo, commandRepo);
    }

    /**
     * Constructor used by the {@link JNRPEBuilder} to build an immutable
     * instance of {@link JNRPE}.
     * 
     * @param pluginRepo
     *            The plugin repository object
     * @param commandRepo
     *            The command repository object
     * @param newCharset
     *            The charset that JNRPE will use
     * @param acceptParameters
     *            Sets if $ARGxx$ macros should be expanded
     * @param acceptedHostsCollection
     *            The list of accepted client hosts
     * @param maxConnections
     *            The maximum number of concurrent connections
     * @param readTimeoutSeconds
     *            The maximum number of seconds to wait for the client to send
     *            the command
     * @param writeTimeoutSeconds
     *            The maximum number of seconds to wait for a plugin to return a
     *            result
     */
    JNRPE(final IPluginRepository pluginRepo, 
            final CommandRepository commandRepo, 
            final Charset newCharset, 
            final boolean acceptParameters,
            final Collection<String> acceptedHostsCollection, 
            final int maxConnections, 
            final int readTimeoutSeconds, 
            final int writeTimeoutSeconds) {
        if (pluginRepo == null) {
            throw new IllegalArgumentException("Plugin repository cannot be null");
        }

        if (commandRepo == null) {
            throw new IllegalArgumentException("Command repository cannot be null");
        }
        pluginRepository = pluginRepo;
        commandRepository = commandRepo;
        this.charset = newCharset;
        this.acceptParams = acceptParameters;
        this.acceptedHostsList = acceptedHostsCollection;
        this.maxAcceptedConnections = maxConnections;
        this.readTimeout = readTimeoutSeconds;
        this.writeTimeout = writeTimeoutSeconds;
        this.context = new JNRPEExecutionContext(new JNRPEEventBus(), newCharset, pluginRepo, commandRepo);
    }

    /**
     * Instructs the server to listen to the given IP/port.
     * 
     * @param address
     *            The address to bind to
     * @param port
     *            The port to bind to
     * @throws UnknownHostException
     *             -
     */
    public void listen(final String address, final int port) throws UnknownHostException {
        listen(address, port, true);
    }

    /**
     * Creates, configures and returns the SSL engine.
     * 
     * @return the SSL Engine
     * @throws KeyStoreException
     *             on keystore errorss
     * @throws CertificateException
     *             on certificate errors
     * @throws IOException
     *             on I/O errors
     * @throws UnrecoverableKeyException
     *             if key is unrecoverable
     * @throws KeyManagementException
     *             key management error
     */
    protected SSLEngine getSSLEngine() throws KeyStoreException, 
                    CertificateException, 
                    IOException, 
                    UnrecoverableKeyException, 
                    KeyManagementException {

        // Open the KeyStore Stream
        StreamManager h = new StreamManager();

        SSLContext ctx;
        KeyManagerFactory kmf;

        try {
            InputStream ksStream = getClass().getClassLoader().getResourceAsStream(KEYSTORE_NAME);
            h.handle(ksStream);
            ctx = SSLContext.getInstance("SSLv3");

            kmf = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());

            KeyStore ks = KeyStore.getInstance("JKS");
            char[] passphrase = KEYSTORE_PWD.toCharArray();
            ks.load(ksStream, passphrase);

            kmf.init(ks, passphrase);
            ctx.init(kmf.getKeyManagers(), null, new java.security.SecureRandom());
        } catch (NoSuchAlgorithmException e) {
            throw new SSLException("Unable to initialize SSLSocketFactory.\n" + e.getMessage(), e);
        } finally {
            h.closeAll();
        }

        return ctx.createSSLEngine();
    }

    /**
     * Creates and returns a configured NETTY ServerBootstrap object.
     * 
     * @param useSSL
     *            <code>true</code> if SSL must be used.
     * @return the server bootstrap object
     */
    private ServerBootstrap getServerBootstrap(final boolean useSSL) {

        final CommandInvoker invoker = new CommandInvoker(pluginRepository, commandRepository, acceptParams, getExecutionContext());

        ServerBootstrap b = new ServerBootstrap();
        b.group(bossGroup, workerGroup).channel(NioServerSocketChannel.class).childHandler(new ChannelInitializer<SocketChannel>() {
            @Override
            public void initChannel(final SocketChannel ch) throws Exception {

                if (useSSL) {
                    SSLEngine engine = getSSLEngine();
                    engine.setEnabledCipherSuites(engine.getSupportedCipherSuites());
                    engine.setUseClientMode(false);
                    engine.setNeedClientAuth(false);
                    ch.pipeline().addLast("ssl", new SslHandler(engine));
                }

                ch.pipeline()
                        .addLast(new JNRPERequestDecoder(), new JNRPEResponseEncoder(), new JNRPEServerHandler(invoker, context))
                        .addLast("idleStateHandler", new IdleStateHandler(readTimeout, writeTimeout, 0))
                        .addLast(
                                "jnrpeIdleStateHandler",
                                new JNRPEIdleStateHandler(context));
            }
        }).option(ChannelOption.SO_BACKLOG, this.maxAcceptedConnections).childOption(ChannelOption.SO_KEEPALIVE, true);

        return b;
    }

    /**
     * Starts a new thread that listen for requests. The method is <b>not
     * blocking</b>
     * 
     * @param address
     *            The address to bind to
     * @param port
     *            The listening port
     * @param useSSL
     *            <code>true</code> if an SSL socket must be created.
     * @throws UnknownHostException
     *             -
     */
    public void listen(final String address, final int port, final boolean useSSL) throws UnknownHostException {

        // Bind and start to accept incoming connections.
        ChannelFuture cf = getServerBootstrap(useSSL).bind(address, port);
        cf.addListener(new ChannelFutureListener() {

            public void operationComplete(final ChannelFuture future) throws Exception {
                if (future.isSuccess()) {
                    context.getEventBus().post(new JNRPEStatusEvent(STATUS.STARTED, this, "JNRPE Server started"));
                    LOG.info(context, "Listening on " + (useSSL ? "SSL/" : "") + address + ":" + port);
                } else {
                    getExecutionContext().getEventBus().post(new JNRPEStatusEvent(STATUS.FAILED, this, "JNRPE Server start failed"));
                    LOG.error(context, "Unable to listen on " + (useSSL ? "SSL/" : "") + address + ":" + port, future.cause());
                }
            }
        });

    }

    /**
     * Adds an address to the list of accepted hosts.
     * 
     * @param address
     *            The address to accept
     * @deprecated The JNRPE object will become immutable as of version 2.0.5.
     *             Use {@link JNRPEBuilder} instead
     */
    @Deprecated
    public void addAcceptedHost(final String address) {
        acceptedHostsList.add(address);
    }

    /**
     * Returns the current JNRPE Execution context. An execution context
     * contains useful informations such the encoding to be used ad the list of
     * listeners configured to receive the events.
     * 
     * @return the current JNRPE Execution context.
     */
    public IJNRPEExecutionContext getExecutionContext() {
        //return new JNRPEExecutionContext(eventBus, charset, pluginRepository, commandRepository);
        return context;
    }

    /**
     * Shuts down the server.
     */
    public void shutdown() {
        workerGroup.shutdownGracefully().syncUninterruptibly();
        bossGroup.shutdownGracefully().syncUninterruptibly();
        getExecutionContext().getEventBus().post(new JNRPEStatusEvent(STATUS.STOPPED, this, "JNRPE Server stopped"));
    }
}
