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
package it.jnrpe.client;

import it.jnrpe.ReturnValue;
import it.jnrpe.Status;
import it.jnrpe.net.JNRPERequest;
import it.jnrpe.net.JNRPEResponse;
import it.jnrpe.utils.TimeUnit;

import java.io.IOException;
import java.io.InputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.List;

import javax.net.SocketFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import org.apache.commons.cli2.CommandLine;
import org.apache.commons.cli2.DisplaySetting;
import org.apache.commons.cli2.Group;
import org.apache.commons.cli2.OptionException;
import org.apache.commons.cli2.builder.ArgumentBuilder;
import org.apache.commons.cli2.builder.DefaultOptionBuilder;
import org.apache.commons.cli2.builder.GroupBuilder;
import org.apache.commons.cli2.commandline.Parser;
import org.apache.commons.cli2.option.DefaultOption;
import org.apache.commons.cli2.util.HelpFormatter;
import org.apache.commons.cli2.validation.NumberValidator;

/**
 * This class represent a simple JNRPE client that can be used to invoke
 * commands installed inside JNRPE by code. It is the JAVA equivalent of
 * check_nrpe.
 * 
 * @author Massimiliano Ziccardi
 */
public class JNRPEClient {

    /**
     * Default timeout in seconds.
     */
    private static final int DEFAULT_TIMEOUT = 10;

    /**
     * Default server port.
     */
    private static final int DEFAULT_PORT = 5666;

    /**
     * The IP address (or URL) of the JNRPE Server.
     */
    private final String serverIPorURL;

    /**
     * The port where the JNRPE server listens to.
     */
    private final int serverPort;

    /**
     * <code>true</code> if the communication must happens through SSL.
     */
    private final boolean useSSL;

    /**
     * The communication timeout (in seconds).
     */
    private int communicationTimeout = DEFAULT_TIMEOUT;

    /**
     * The trust manager. 
     */
    private static final TrustAllManager TRUSTALL_MGR = new TrustAllManager();
    
    /**
     * Trust manager implementation.
     * This trust manager allows connection on any host.
     * 
     * @author Massimiliano Ziccardi
     */
    private static final class TrustAllManager implements X509TrustManager {

        /**
         * Simply returns null.
         * @return null
         */
        public X509Certificate[] getAcceptedIssuers() {
            return null;
        }

        /**
         * No checks performed.
         * 
         * @param chain certificate chain
         * @param authType authentication type
         * @throws CertificateException on errors.
         */
        public void checkServerTrusted(final X509Certificate[] chain, final String authType) {

        }

        /**
         * No checks performed.
         * 
         * @param chain certificate chain
         * @param authType authentication type
         * @throws CertificateException on errors.
         */
        public void checkClientTrusted(final X509Certificate[] chain, final String authType) {
        }
    }
    
    /**
     * Instantiates a JNRPE client.
     * 
     * @param sJNRPEServerIP
     *            The IP where the JNRPE is installed
     * @param iJNRPEServerPort
     *            The port where the JNRPE server listens
     * @param bSSL
     *            <code>true</code> if SSL must be used
     */
    public JNRPEClient(final String sJNRPEServerIP, final int iJNRPEServerPort, final boolean bSSL) {
        serverIPorURL = sJNRPEServerIP;
        serverPort = iJNRPEServerPort;
        useSSL = bSSL;
    }

    /**
     * Creates a custom TrustManager that trusts any certificate.
     * 
     * @return The custom trustmanager
     */
    private TrustManager getTrustManager() {
        return TRUSTALL_MGR;
    }

    /**
     * Inovoke a command installed in JNRPE.
     * 
     * @param sCommandName
     *            The name of the command to be invoked
     * @param arguments
     *            The arguments to pass to the command (will substitute the
     *            $ARGSx$ parameters)
     * @return The value returned by the server
     * @throws JNRPEClientException
     *             Thrown on any communication error.
     */
    public final ReturnValue sendCommand(final String sCommandName, final String... arguments) throws JNRPEClientException {
        SocketFactory socketFactory = null;

        Socket s = null;
        try {
            if (!useSSL) {
                socketFactory = SocketFactory.getDefault();
            } else {
                SSLContext sslContext = SSLContext.getInstance("SSL");
                sslContext.init(null, null, new java.security.SecureRandom());

                sslContext.init(null, new TrustManager[] { getTrustManager() }, new SecureRandom());

                socketFactory = sslContext.getSocketFactory();
            }

            s = socketFactory.createSocket();
            s.setSoTimeout((int) TimeUnit.SECOND.convert(communicationTimeout));
            s.connect(new InetSocketAddress(serverIPorURL, serverPort));
            JNRPERequest req = new JNRPERequest(sCommandName, arguments);

            s.getOutputStream().write(req.toByteArray());

            InputStream in = s.getInputStream();
            JNRPEResponse res = new JNRPEResponse(in);

            return new ReturnValue(Status.fromIntValue(res.getResultCode()), res.getMessage());
        } catch (Exception e) {
            throw new JNRPEClientException(e);
        } finally {
            if (s != null) {
                try {
                    s.close();
                } catch (IOException e) {
                    // Ignore
                }
            }
        }
    }

    /**
     * Sets the connection timeout in seconds.
     * 
     * @param iTimeout
     *            The new connection timeout. Default : 10
     */
    public final void setTimeout(final int iTimeout) {
        communicationTimeout = iTimeout;
    }

    /**
     * Returns the currently configured connection timeout in seconds.
     * 
     * @return The connection timeout
     */
    public final int getTimeout() {
        return communicationTimeout;
    }

    /**
     * Configures the command line parser.
     * 
     * @return The command line parser configuration
     */
    private static Group configureCommandLine() {
        DefaultOptionBuilder oBuilder = new DefaultOptionBuilder();
        ArgumentBuilder aBuilder = new ArgumentBuilder();
        GroupBuilder gBuilder = new GroupBuilder();

        DefaultOption nosslOption = oBuilder.withLongName("nossl").withShortName("n").withDescription("Do no use SSL").create();

        DefaultOption unknownOption = oBuilder.withLongName("unknown").withShortName("u")
                .withDescription("Make socket timeouts return an UNKNOWN state instead of CRITICAL").create();

        DefaultOption hostOption = oBuilder.withLongName("host").withShortName("H")
                .withDescription("The address of the host running " + "the JNRPE/NRPE daemon")
                .withArgument(aBuilder.withName("host").withMinimum(1).withMaximum(1).create()).create();

        NumberValidator positiveInt = NumberValidator.getIntegerInstance();
        positiveInt.setMinimum(0);
        DefaultOption portOption = oBuilder
                .withLongName("port")
                .withShortName("p")
                .withDescription("The port on which the daemon " + "is running (default=5666)")
                .withArgument(
                        aBuilder.withName("port").withMinimum(1).withMaximum(1).withDefault(Long.valueOf(DEFAULT_PORT)).withValidator(positiveInt)
                                .create()).create();

        DefaultOption timeoutOption = oBuilder
                .withLongName("timeout")
                .withShortName("t")
                .withDescription("Number of seconds before connection " + "times out (default=10)")
                .withArgument(
                        aBuilder.withName("timeout").withMinimum(1).withMaximum(1).withDefault(Long.valueOf(DEFAULT_TIMEOUT)).withValidator(positiveInt)
                                .create()).create();

        DefaultOption commandOption = oBuilder.withLongName("command").withShortName("c")
                .withDescription("The name of the command that " + "the remote daemon should run")
                .withArgument(aBuilder.withName("command").withMinimum(1).withMaximum(1).create()).create();

        DefaultOption argsOption = oBuilder
                .withLongName("arglist")
                .withShortName("a")
                .withDescription(
                        "Optional arguments that should be " + "passed to the command.  Multiple " + "arguments should be separated by "
                                + "a space (' '). If provided, " + "this must be the last option " + "supplied on the command line.")
                .withArgument(aBuilder.withName("arglist").withMinimum(1).create()).create();

        DefaultOption helpOption = oBuilder.withLongName("help").withShortName("h").withDescription("Shows this help").create();

        Group executionOption = gBuilder.withOption(nosslOption).withOption(unknownOption).withOption(hostOption).withOption(portOption)
                .withOption(timeoutOption).withOption(commandOption).withOption(argsOption).create();

        return gBuilder.withOption(executionOption).withOption(helpOption).withMinimum(1).withMaximum(1).create();
    }

    /**
     * Prints the application version.
     */
    private static void printVersion() {

        System.out.println("jcheck_nrpe version " + JNRPEClient.class.getPackage().getImplementationVersion());
        System.out.println("Copyright (c) 2013 Massimiliano Ziccardi");
        System.out.println("Licensed under the Apache License, Version 2.0");
        System.out.println();
    }

    /**
     * Prints usage instrunctions and, eventually, an error message about the
     * latest execution.
     * 
     * @param e
     *            The exception error
     */
    @SuppressWarnings("unchecked")
    private static void printUsage(final Exception e) {
        printVersion();

        StringBuilder sbDivider = new StringBuilder("=");

        if (e != null) {
            System.out.println(e.getMessage() + "\n");
        }

        HelpFormatter hf = new HelpFormatter();
        while (sbDivider.length() < hf.getPageWidth()) {
            sbDivider.append("=");
        }

        // DISPLAY SETTING
        hf.getDisplaySettings().clear();
        hf.getDisplaySettings().add(DisplaySetting.DISPLAY_GROUP_EXPANDED);
        hf.getDisplaySettings().add(DisplaySetting.DISPLAY_PARENT_CHILDREN);

        // USAGE SETTING

        hf.getFullUsageSettings().clear();
        hf.getFullUsageSettings().add(DisplaySetting.DISPLAY_PARENT_ARGUMENT);
        hf.getFullUsageSettings().add(DisplaySetting.DISPLAY_ARGUMENT_BRACKETED);
        hf.getFullUsageSettings().add(DisplaySetting.DISPLAY_PARENT_CHILDREN);
        hf.getFullUsageSettings().add(DisplaySetting.DISPLAY_GROUP_EXPANDED);

        hf.setDivider(sbDivider.toString());

        hf.setGroup(configureCommandLine());
        hf.print();
    }

    /**
     * 
     * @param args
     *            command line arguments
     * @throws Exception
     *             -
     */
    public static void main(final String[] args) throws Exception {

        Parser parser = new Parser();
        parser.setGroup(configureCommandLine());

        boolean timeoutAsUnknown = false;

        try {
            CommandLine cli = parser.parse(args);

            if (cli.hasOption("--help")) {
                printUsage(null);
            }

            timeoutAsUnknown = cli.hasOption("--unknown");

            String sHost = (String) cli.getValue("--host");
            Long port = (Long) cli.getValue("--port", Long.valueOf(DEFAULT_PORT));
            String sCommand = (String) cli.getValue("--command");

            JNRPEClient client = new JNRPEClient(sHost, port.intValue(), !cli.hasOption("--nossl"));
            client.setTimeout(((Long) cli.getValue("--timeout", Long.valueOf(DEFAULT_TIMEOUT))).intValue());

            @SuppressWarnings("unchecked")
            List<String> argList = cli.getValues("--arglist");
            ReturnValue ret = client.sendCommand(sCommand, argList.toArray(new String[argList.size()]));

            System.out.println(ret.getMessage());
            System.exit(ret.getStatus().intValue());
        } catch (JNRPEClientException exc) {
            Status returnStatus = null;

            Throwable cause = exc.getCause();
            if (timeoutAsUnknown && cause instanceof SocketTimeoutException) {
                returnStatus = Status.UNKNOWN;
            } else {
                returnStatus = Status.CRITICAL;
            }

            System.out.println(exc.getMessage());
            System.exit(returnStatus.intValue());
        } catch (OptionException oe) {
            printUsage(oe);
        }
    }
}
