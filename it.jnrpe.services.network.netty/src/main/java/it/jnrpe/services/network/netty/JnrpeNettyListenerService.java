/*******************************************************************************
 * Copyright (C) 2020, Massimiliano Ziccardi
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 *******************************************************************************/
package it.jnrpe.services.network.netty;

import java.math.BigInteger;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.KeyStore;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.cert.Certificate;
import java.security.cert.X509Certificate;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLEngine;
import javax.net.ssl.SSLException;
import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.cert.X509v3CertificateBuilder;
import org.bouncycastle.cert.jcajce.JcaX509CertificateConverter;
import org.bouncycastle.cert.jcajce.JcaX509v3CertificateBuilder;
import org.bouncycastle.operator.ContentSigner;
import org.bouncycastle.operator.jcajce.JcaContentSignerBuilder;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.ssl.SslHandler;
import it.jnrpe.engine.events.EventManager;
import it.jnrpe.engine.services.config.IJNRPEConfig;
import it.jnrpe.engine.services.network.INetworkListener;

/**
 * The class for the Netty listener service.
 *
 * <p>
 * This class implements the INetworkListener interface and provides a Netty server that listens for
 * JNRPE requests.
 *
 * @author Massimiliano Ziccardi
 */
public class JnrpeNettyListenerService implements INetworkListener {
  private final ServerHandler serverHandler = new ServerHandler();
  private final EventLoopGroup bossGroup = new NioEventLoopGroup();
  private final EventLoopGroup workerGroup = new NioEventLoopGroup();

  @Override
  public String getName() {
    return "Netty Listener Service";
  }

  static X509Certificate generateSelfSignedX509Certificate(KeyPair keyPair) throws Exception {
    Instant now = Instant.now();
    Date validityBeginDate = Date.from(now.minus(1, ChronoUnit.DAYS));
    Date validityEndDate = Date.from(now.plus(730, ChronoUnit.DAYS)); // 2 years

    X500Name dnName = new X500Name("CN=JNRPE SERVER");
    BigInteger serialNumber = BigInteger.valueOf(System.currentTimeMillis());

    X509v3CertificateBuilder certBuilder = new JcaX509v3CertificateBuilder(dnName, serialNumber,
        validityBeginDate, validityEndDate, dnName, keyPair.getPublic());

    ContentSigner signer = new JcaContentSignerBuilder("SHA256withRSA").build(keyPair.getPrivate());
    return new JcaX509CertificateConverter().getCertificate(certBuilder.build(signer));
  }

  /**
   * Creates, configures and returns the SSL engine.
   *
   * @return the SSL Engine
   */
  private SSLEngine getSSLEngine() throws Exception {
    SSLContext ctx;
    KeyManagerFactory kmf;

    try {
      // Gen key pair
      KeyPairGenerator keyGen = KeyPairGenerator.getInstance("RSA");
      keyGen.initialize(1024, SecureRandom.getInstanceStrong());
      var keyPair = keyGen.generateKeyPair();

      ctx = SSLContext.getInstance("SSLv3");

      kmf = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());

      final KeyStore ks = KeyStore.getInstance(KeyStore.getDefaultType());
      ks.load(null, null);
      char[] pwd = SecureRandom.getInstanceStrong().ints('a', 'z').limit(20)
          .collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append)
          .toString().toCharArray();
      ks.setKeyEntry("sslkey", keyPair.getPrivate(), pwd,
          new Certificate[] {generateSelfSignedX509Certificate(keyPair)});
      kmf.init(ks, pwd);
      ctx.init(kmf.getKeyManagers(), null, new java.security.SecureRandom());
    } catch (NoSuchAlgorithmException e) {
      throw new SSLException("Unable to initialize SSLSocketFactory" + e.getMessage(), e);
    }
    return ctx.createSSLEngine();
  }

  private ServerBootstrap getServerBootStrap(final Boolean useSSL) {
    final ServerBootstrap serverBootstrap = new ServerBootstrap();
    serverBootstrap.group(bossGroup, workerGroup).channel(NioServerSocketChannel.class)
        .childHandler(new ChannelInitializer<>() {
          @Override
          protected void initChannel(Channel ch) throws Exception {
            if (useSSL) {
              final SSLEngine engine = getSSLEngine();
              engine.setEnabledCipherSuites(engine.getSupportedCipherSuites());
              engine.setUseClientMode(false);
              engine.setNeedClientAuth(false);
              ch.pipeline().addLast("ssl", new SslHandler(engine));
            }
            ch.pipeline().addLast(new AuthorizationHandler(), new RequestDecoder(),
                new ResponseEncoder(), serverHandler);
          }
        }).option(ChannelOption.SO_BACKLOG, 50)
        .childOption(ChannelOption.SO_KEEPALIVE, Boolean.TRUE);
    return serverBootstrap;
  }

  public void bind(IJNRPEConfig.Binding binding) {
    var serverBootstrap = getServerBootStrap(binding.ssl());
    serverBootstrap.bind(binding.ip(), binding.port());
    EventManager.debug("[%s] Started listening on %s:%d %s", this.getName(), binding.ip(),
        binding.port(), binding.ssl() ? "[SSL]" : "");
  }

  @Override
  public boolean supportBinding(IJNRPEConfig.Binding binding) {
    return true;
  }

  public void shutdown() {
    workerGroup.shutdownGracefully().syncUninterruptibly();
    bossGroup.shutdownGracefully().syncUninterruptibly();
  }
}
