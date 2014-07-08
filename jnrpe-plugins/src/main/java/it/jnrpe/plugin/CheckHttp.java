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
package it.jnrpe.plugin;

import it.jnrpe.ICommandLine;
import it.jnrpe.Status;
import it.jnrpe.plugin.utils.HttpUtils;
import it.jnrpe.plugin.utils.Utils;
import it.jnrpe.plugins.Metric;
import it.jnrpe.plugins.MetricGatheringException;
import it.jnrpe.plugins.PluginBase;
import it.jnrpe.utils.BadThresholdException;
import it.jnrpe.utils.thresholds.ThresholdsEvaluatorBuilder;

import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.security.SecureRandom;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Properties;
import java.util.regex.Pattern;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.KeyManager;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import org.apache.commons.codec.binary.Base64;

/**
 * This plugin tests the HTTP service on the specified host. It can test normal
 * (http) and secure (https) servers, follow redirects, search for strings and
 * regular expressions on page results and check connection times.
 * 
 * @author Frederico Campos
 * 
 */

public class CheckHttp extends PluginBase {

    /**
     * Default HTTP port.
     */
    private static final String DEFAULT_PORT = "80";

    /**
     * Default HTTPS port.
     */
    private static final String DEFAULT_SSL_PORT = "443";

    /**
     * Default timeout.
     */
    private static final int DEFAULT_TIMEOUT = 30;

    /**
     * Default http path.
     */
    private static final String DEFAULT_PATH = "/";

    /**
     * Default HTTP method.
     */
    private static final String DEFAULT_METHOD = "GET";

    /**
     * Default user agent.
     */
    private static final String DEFAULT_USER_AGENT = "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 "
            + "(KHTML, like Gecko) Chrome/27.0.1453.94 Safari/537.36";

    private static final String CHARSET = "UTF-8";

    /**
     * Configures the threshold evaluator. This plugin supports both the legacy
     * threshold format and the new format specification.
     * 
     * @param thrb
     *            - the evaluator to be configured
     * @param cl
     *            - the received command line
     * @throws BadThresholdException
     *             - if the threshold can't be parsed
     */
    @Override
    public final void configureThresholdEvaluatorBuilder(final ThresholdsEvaluatorBuilder thrb, final ICommandLine cl) throws BadThresholdException {
        thrb.withLegacyThreshold("time", null, cl.getOptionValue("warning"), cl.getOptionValue("critical"));
        if (cl.hasOption("regex")) {
            if (cl.hasOption("invert-regex")) {
                // invert-regex: CRITICAL value if regex is found (true = 1)
                thrb.withLegacyThreshold("invert-regex", null, null, "1");
            } else {
                // WARNING if regex not found (false = 0)
                thrb.withLegacyThreshold("regex", null, null, "0");
            }
        }
        if (cl.hasOption("expect")) {
            // WARNING if expected string not found (false = 0)
            thrb.withLegacyThreshold("expect", null, "0", null);
        }
        if (cl.hasOption("string")) {
            // WARNING if expected string not found (false = 0)
            thrb.withLegacyThreshold("string", null, "0", null);
        }
        if (cl.hasOption("onredirect")) {
            String redirect = cl.getOptionValue("onredirect").toUpperCase();
            if ("OK".equals(redirect)) {
                thrb.withLegacyThreshold("onredirect", "1:", null, null);
            } else if ("CRITICAL".equals(redirect)) {
                thrb.withLegacyThreshold("onredirect", null, null, "1:");
            } else if ("WARNING".equals(redirect)) {
                thrb.withLegacyThreshold("onredirect", null, "1:", null);
            }
        }
        if (cl.hasOption("certificate")) {
            String ok = cl.getOptionValue("certificate");
            thrb.withLegacyThreshold("certificate", ok, null, null);
        }
    }

    /**
     * Execute and gather metrics.
     * 
     * @param cl
     *            - The command line parameters
     * @throws MetricGatheringException
     *             - If any error occurs during metric gathering process
     * @return the gathered metrics
     */
    @Override
    public final Collection<Metric> gatherMetrics(final ICommandLine cl) throws MetricGatheringException {
        List<Metric> metrics = new ArrayList<Metric>();
        String hostname = cl.getOptionValue("hostname");
        if (hostname == null) {
            throw new MetricGatheringException("No hostname specified.", Status.WARNING, null);
        }

        String port = cl.getOptionValue("port", DEFAULT_PORT);
        String path = cl.getOptionValue("url", DEFAULT_PATH);
        String method = cl.getOptionValue("method", DEFAULT_METHOD);

        int timeout = DEFAULT_TIMEOUT;
        if (cl.hasOption("post")) {
            method = "POST";
        }
        boolean ssl = false;
        if (cl.hasOption("ssl") || cl.getOptionValue("certificate") != null) {
            port = cl.getOptionValue("ssl", DEFAULT_SSL_PORT);
            ssl = true;
        }

        if (cl.hasOption("timeout")) {
            try {
                timeout = Integer.parseInt(cl.getOptionValue("timeout"));
            } catch (NumberFormatException e) {
                throw new MetricGatheringException("Invalid numeric value for timeout.", Status.CRITICAL, e);
            }
        }
        if (!path.startsWith("/")) {
            path = "/" + path;
        }
        if (hostname.endsWith("/")) {
            hostname = hostname.substring(0, hostname.length() - 1);
        }

        long then = System.currentTimeMillis();

        String response = getHttpResponse(cl, hostname, port, method, path, timeout, ssl, metrics);
        int elapsed = (int) Utils.milliToSec(System.currentTimeMillis() - then);
        if (response != null) {
            metrics.addAll(analyzeResponse(cl, response, elapsed));
        }
        return metrics;
    }

    /**
     * Do the actual http request and return the response string.
     * 
     * @param cl
     *            - The received command line
     * @param hostname
     *            - The server hostname
     * @param port
     *            - The server port
     * @param method
     *            - The HTTP method
     * @param path
     *            - The connection path
     * @param timeout
     *            - The timeout
     * @param ssl
     *            - if SSL must be used
     * @param metrics
     *            - This list will be filled with the gathered metrics
     * @return - the response
     * @throws MetricGatheringException
     *             - if an error occurs during the execution
     */
    private String getHttpResponse(final ICommandLine cl, final String hostname, final String port, final String method, final String path,
            final int timeout, final boolean ssl, final List<Metric> metrics) throws MetricGatheringException {
        Properties props = null;
        try {
            props = getRequestProperties(cl, method);
        } catch (UnsupportedEncodingException e) {
            throw new MetricGatheringException("Error occurred: " + e.getMessage(), Status.CRITICAL, e);
        }
        String response = null;
        String redirect = cl.getOptionValue("onredirect");
        boolean ignoreBody = false;
        try {
            String data = null;
            if ("POST".equals(method)) {
                data = getPostData(cl);
            }

            if (cl.hasOption("no-body")) {
                ignoreBody = true;
            }
            String urlString = hostname + ":" + port + path;
            if (cl.hasOption("authorization")) {
                urlString = cl.getOptionValue("authorization") + "@" + urlString;
            } else if (cl.hasOption("proxy-authorization")) {
                urlString = cl.getOptionValue("proxy-authorization") + "@" + urlString;
            }
            if (ssl) {
                urlString = "https://" + urlString;
            } else {
                urlString = "http://" + urlString;
            }
            URL url = new URL(urlString);
            if (cl.getOptionValue("certificate") != null) {
                checkCertificateExpiryDate(url, metrics);
            } else if (redirect != null) {
                response = checkRedirectResponse(url, method, timeout, props, data, redirect, ignoreBody, metrics);
            } else {
                try {
                    if ("GET".equals(method)) {
                        response = HttpUtils.doGET(url, props, timeout, true, ignoreBody);
                    } else if ("POST".equals(method)) {
                        response = HttpUtils.doPOST(url, props, null, data, true, ignoreBody);
                    } else if ("HEAD".equals(method)) {
                        response = HttpUtils.doHEAD(url, props, timeout, true, ignoreBody);
                    }
                    // @TODO complete for other http methods

                } catch (MalformedURLException e) {
                    LOG.error(getContext(), "Bad url", e);
                    throw new MetricGatheringException("Bad url string : " + urlString, Status.CRITICAL, e);
                }
            }

        } catch (Exception e) {
            LOG.error(getContext(), "Exception: " + e.getMessage(), e);
            throw new MetricGatheringException(e.getClass().getName() + ": " + e.getMessage(), Status.CRITICAL, e);
        }
        return response;
    }

    /**
     * Apply the logic to check for url redirects.
     * 
     * @param url
     *            - The server URL
     * @param method
     *            - The HTTP method
     * @param timeout
     *            - The timeout
     * @param props
     *            -
     * @param postData
     *            -
     * @param redirect
     *            -
     * @param ignoreBody
     *            -
     * @param metrics
     *            - This list will be filled with the gathered metrics
     * @return String
     * @throws Exception
     *             -
     */
    private String checkRedirectResponse(final URL url, final String method, final Integer timeout, final Properties props, final String postData,
            final String redirect, final boolean ignoreBody, final List<Metric> metrics) throws Exception {
        // @todo handle sticky/port and follow param options

        String response = null;
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod(method);
        HttpUtils.setRequestProperties(props, conn, timeout);
        String initialUrl = conn.getURL() + "";
        String redirectedUrl = null;
        if ("POST".equals(method)) {
            HttpUtils.sendPostData(conn, postData);
        }
        response = HttpUtils.parseHttpResponse(conn, false, ignoreBody);
        redirectedUrl = conn.getURL() + "";

        if (!redirectedUrl.equals(initialUrl)) {
            Metric metric = new Metric("onredirect", "", new BigDecimal(1), null, null);
            metrics.add(metric);
        }
        return response;
    }

    /**
     * Apply logic to the http response and build metrics.
     * 
     * @param opt
     *            -
     * @param response
     *            -
     * @param elapsed
     *            -
     * @return - The metrics
     * @throws MetricGatheringException
     *             List<Metric>
     */
    private List<Metric> analyzeResponse(final ICommandLine opt, final String response, final int elapsed) throws MetricGatheringException {
        List<Metric> metrics = new ArrayList<Metric>();
        metrics.add(new Metric("time", "", new BigDecimal(elapsed), null, null));

        if (!opt.hasOption("certificate")) {
            if (opt.hasOption("string")) {
                boolean found = false;
                String string = opt.getOptionValue("string");
                found = response.contains(string);
                metrics.add(new Metric("string", "", new BigDecimal(Utils.getIntValue(found)), null, null));
            }
            if (opt.hasOption("expect")) {
                int count = 0;
                String[] values = opt.getOptionValue("expect").split(",");
                for (String value : values) {
                    if (response.contains(value)) {
                        count++;
                    }
                }
                metrics.add(new Metric("expect", String.valueOf(count) + " times. ", new BigDecimal(count), null, null));
            }
            if (opt.hasOption("regex")) {
                String regex = opt.getOptionValue("regex");
                Pattern p = null;
                int flags = 0;
                if (opt.hasOption("eregi")) {
                    flags = Pattern.CASE_INSENSITIVE;
                }
                if (opt.hasOption("linespan")) {
                    flags = flags | Pattern.MULTILINE;
                }
                p = Pattern.compile(regex, flags);
                boolean found = p.matcher(response).find();
                if (opt.hasOption("invert-regex")) {
                    metrics.add(new Metric("invert-regex", String.valueOf(found), new BigDecimal(Utils.getIntValue(found)), null, null));
                } else {
                    metrics.add(new Metric("regex", String.valueOf(found), new BigDecimal(Utils.getIntValue(found)), null, null));
                }
            }
        }
        return metrics;
    }

    /**
     * Set the http request properties and headers.
     * 
     * @param cl
     *            The received command line
     * @param method
     *            The HTTP method
     * @return Properties
     * @throws UnsupportedEncodingException
     */
    private Properties getRequestProperties(final ICommandLine cl, final String method) throws UnsupportedEncodingException {
        Properties props = new Properties();
        if (cl.hasOption("useragent")) {
            props.setProperty("User-Agent", cl.getOptionValue("useragent"));
        } else {
            props.setProperty("User-Agent", DEFAULT_USER_AGENT);
        }
        if (cl.hasOption("content-type") && "POST".equalsIgnoreCase(method)) {
            props.setProperty("Content-Type", cl.getOptionValue("content-type"));
        }
        if (cl.hasOption("header")) {
            List headers = cl.getOptionValues("header");
            for (Object obj : headers) {
                String header = (String) obj;
                String key = header.split(":")[0].trim();
                String value = header.split(":")[1].trim();
                props.setProperty(key, value);
            }
        }
        String auth = null;
        String encoded = null;
        if (cl.hasOption("authorization")) {
            encoded = Base64.encodeBase64String(cl.getOptionValue("authorization").getBytes(CHARSET));
            auth = "Authorization";
        } else if (cl.hasOption("proxy-authorization")) {
            encoded = Base64.encodeBase64String(cl.getOptionValue("proxy-authorization").getBytes(CHARSET));
            auth = "Proxy-Authorization";
        }
        if (auth != null && encoded != null) {
            props.setProperty(auth, "Basic " + encoded);
        }
        return props;
    }

    /**
     * Returns encoded post data.
     * 
     * @param cl
     *            - The received command line
     * @return - The encoded post data
     * @throws Exception
     *             -
     */
    private String getPostData(final ICommandLine cl) throws Exception {
        // String encoded = "";
        StringBuilder encoded = new StringBuilder();
        String data = cl.getOptionValue("post");
        if (data == null) {
            return null;
        }
        String[] values = data.split("&");
        for (String value : values) {
            String[] splitted = value.split("=");
            String key = splitted[0];
            String val = "";
            if (splitted.length > 1) {
                val = splitted[1];
            }
            if (encoded.length() != 0) {
                encoded.append('&');
            }
            encoded.append(key).append('=').append(URLEncoder.encode(val, "UTF-8"));
            // encoded += key + "=" + URLEncoder.encode(val, "UTF-8") + "&";
        }
        // if (encoded.endsWith("&")) {
        // StringUtils.removeEnd(encoded, "&");
        // }
        return encoded.toString();
    }

    /*
     * (non-Javadoc)
     * 
     * @see it.jnrpe.plugins.PluginBase#getPluginName()
     */
    @Override
    protected String getPluginName() {
        return "CHECK_HTTP";
    }

    // stuff for checking certificate
    private void checkCertificateExpiryDate(URL url, List<Metric> metrics) throws Exception {
        SSLContext ctx = SSLContext.getInstance("TLS");
        ctx.init(new KeyManager[0], new TrustManager[] { new DefaultTrustManager() }, new SecureRandom());
        SSLContext.setDefault(ctx);
        HttpsURLConnection conn = (HttpsURLConnection) url.openConnection();
        conn.setHostnameVerifier(new HostnameVerifier() {
            public boolean verify(final String arg0, final SSLSession arg1) {
                return true;
            }
        });
        List<Date> expiryDates = new ArrayList<Date>();
        conn.getResponseCode();
        Certificate[] certs = conn.getServerCertificates();
        for (Certificate cert : certs) {
            X509Certificate x509 = (X509Certificate) cert;
            Date expiry = x509.getNotAfter();
            expiryDates.add(expiry);
        }

        conn.disconnect();
        Date today = new Date();
        for (Date date : expiryDates) {
            int diffInDays = (int) ((date.getTime() - today.getTime()) / (1000 * 60 * 60 * 24));
            metrics.add(new Metric("certificate", "", new BigDecimal(diffInDays), null, null));
        }
    }

    /**
     * The trustall trust manager .
     */
    private static class DefaultTrustManager implements X509TrustManager {

        /*
         * (non-Javadoc)
         * 
         * @see
         * javax.net.ssl.X509TrustManager#checkClientTrusted(java.security.cert
         * .X509Certificate[], java.lang.String)
         */
        public void checkClientTrusted(final X509Certificate[] arg0, final String arg1) throws CertificateException {

        }

        /*
         * (non-Javadoc)
         * 
         * @see
         * javax.net.ssl.X509TrustManager#checkServerTrusted(java.security.cert
         * .X509Certificate[], java.lang.String)
         */
        public void checkServerTrusted(X509Certificate[] arg0, String arg1) throws CertificateException {

        }

        /*
         * (non-Javadoc)
         * 
         * @see javax.net.ssl.X509TrustManager#getAcceptedIssuers()
         */
        public X509Certificate[] getAcceptedIssuers() {
            return null;
        }
    }

}
