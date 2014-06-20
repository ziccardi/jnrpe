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
package it.jnrpe.plugin.jmx;

import it.jnrpe.Status;
import it.jnrpe.plugins.PluginBase;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.management.MBeanServerConnection;
import javax.management.ObjectName;
import javax.management.openmbean.CompositeDataSupport;
import javax.management.openmbean.CompositeType;
import javax.management.remote.JMXConnector;
import javax.management.remote.JMXConnectorFactory;
import javax.management.remote.JMXServiceURL;

/**
 *
 * JMXQuery is used for local or remote request of JMX attributes It requires
 * JRE 1.5 to be used for compilation and execution. Look method main for
 * description how it can be invoked.
 *
 */
public abstract class JMXQuery extends PluginBase {

    private String url;
    private int verbatim;
    private JMXConnector connector;
    private MBeanServerConnection connection;
    private String warning, critical;
    private String attribute, info_attribute;
    private String attribute_key, info_key;
    private String object;
    private String username, password;

    private Object checkData;
    private Object infoData;

    private static final String OK_STRING = "JMX OK -";
    private static final String WARNING_STRING = "JMX WARNING -";
    private static final String CRITICAL_STRING = "JMX CRITICAL -";
    private static final int RETURN_UNKNOWN = 3; // Invalid command line
                                                 // arguments were supplied to
                                                 // the plugin or low-level
                                                 // failures internal to the
                                                 // plugin (such as unable to
                                                 // fork, or open a tcp socket)
                                                 // that prevent it from
                                                 // performing the specified
                                                 // operation. Higher-level
                                                 // errors (such as name
                                                 // resolution errors, socket
                                                 // timeouts, etc) are outside
                                                 // of the control of plugins
                                                 // and should generally NOT be
                                                 // reported as UNKNOWN states.
    private static final String UNKNOWN_STRING = "JMX UNKNOWN";

    protected void connect() throws IOException {
        JMXServiceURL jmxUrl = new JMXServiceURL(url);

        if (username != null) {
            Map<String, String[]> m = new HashMap<String, String[]>();
            m.put(JMXConnector.CREDENTIALS, new String[] { username, password });
            connector = JMXConnectorFactory.connect(jmxUrl, m);
        } else {
            connector = JMXConnectorFactory.connect(jmxUrl);
        }
        connection = connector.getMBeanServerConnection();
    }

    protected void disconnect() throws IOException {
        if (connector != null) {
            connector.close();
            connector = null;
        }
    }

    // /**
    // * @param args
    // */
    // public static void main(String[] args) {
    //
    // JMXQuery query = new JMXQuery();
    // Status status;
    // try{
    // query.parse(args);
    // query.connect();
    // query.execute();
    // status = query.report(System.out);
    //
    // }catch(Exception ex){
    // status = query.report(ex, System.out);
    // }finally{
    // try {
    // query.disconnect();
    // } catch (IOException e) {
    // status = query.report(e, System.out);
    // }
    // }
    // System.exit(status.intValue());
    // }

    protected Status report(Exception ex, PrintStream out) {
        if (ex instanceof ParseError) {
            out.print(UNKNOWN_STRING + " ");
            reportException(ex, out);
            out.println(" Usage: check_jmx -help ");
            return Status.UNKNOWN;
        } else {
            out.print(CRITICAL_STRING + " ");
            reportException(ex, out);
            out.println();
            return Status.CRITICAL;
        }
    }

    protected void reportException(Exception ex, PrintStream out) {

        if (verbatim < 2)
            out.print(rootCause(ex).getMessage());
        else {
            out.print(ex.getMessage() + " connecting to " + object + " by URL " + url);
        }

        if (verbatim >= 3)
            ex.printStackTrace(out);

    }

    private static Throwable rootCause(Throwable ex) {
        if (ex.getCause() == null)
            return ex;
        return rootCause(ex.getCause());
    }

    protected Status report(PrintStream out) {
        Status status;
        if (compare(critical)) {
            status = Status.CRITICAL;
            out.print(CRITICAL_STRING);
        } else if (compare(warning)) {
            status = Status.WARNING;
            out.print(WARNING_STRING);
        } else {
            status = Status.OK;
            out.print(OK_STRING);
        }

        boolean shown = false;
        if (infoData == null || verbatim >= 2) {
            out.print(' ');
            if (attribute_key != null)
                out.print(attribute + '.' + attribute_key + " is " + checkData);
            else {
                out.print(attribute + " is " + checkData);
                shown = true;
            }

        }

        if (!shown && infoData != null) {
            if (infoData instanceof CompositeDataSupport)
                report((CompositeDataSupport) infoData, out);
            else
                out.print(infoData.toString());
        }

        out.println();
        return status;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public int getVerbatim() {
        return verbatim;
    }

    public void setVerbatim(int verbatim) {
        this.verbatim = verbatim;
    }

    public String getWarning() {
        return warning;
    }

    public void setWarning(String warning) {
        this.warning = warning;
    }

    public String getCritical() {
        return critical;
    }

    public void setCritical(String critical) {
        this.critical = critical;
    }

    public String getAttribute() {
        return attribute;
    }

    public void setAttribute(String attribute) {
        this.attribute = attribute;
    }

    public String getInfo_attribute() {
        return info_attribute;
    }

    public void setInfo_attribute(String info_attribute) {
        this.info_attribute = info_attribute;
    }

    public String getAttribute_key() {
        return attribute_key;
    }

    public void setAttribute_key(String attribute_key) {
        this.attribute_key = attribute_key;
    }

    public String getInfo_key() {
        return info_key;
    }

    public void setInfo_key(String info_key) {
        this.info_key = info_key;
    }

    public String getObject() {
        return object;
    }

    public void setObject(String object) {
        this.object = object;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Object getInfoData() {
        return infoData;
    }

    public void setInfoData(Object infoData) {
        this.infoData = infoData;
    }

    private void report(CompositeDataSupport data, PrintStream out) {
        CompositeType type = data.getCompositeType();
        out.print(",");
        for (Iterator it = type.keySet().iterator(); it.hasNext();) {
            String key = (String) it.next();
            if (data.containsKey(key))
                out.print(key + '=' + data.get(key));
            if (it.hasNext())
                out.print(';');
        }
    }

    private boolean compare(String level) {
        if (checkData instanceof Number) {
            Number check = (Number) checkData;
            if (check.doubleValue() == Math.floor(check.doubleValue())) {
                return check.doubleValue() >= Double.parseDouble(level);
            } else {
                return check.longValue() >= Long.parseLong(level);
            }
        }
        if (checkData instanceof String) {
            return checkData.equals(level);
        }
        if (checkData instanceof Boolean) {
            return checkData.equals(Boolean.parseBoolean(level));
        }
        throw new RuntimeException(level + "is not of type Number,String or Boolean");
    }

    protected void execute() throws Exception {
        Object attr = connection.getAttribute(new ObjectName(object), attribute);
        if (attr instanceof CompositeDataSupport) {
            CompositeDataSupport cds = (CompositeDataSupport) attr;
            if (attribute_key == null)
                throw new ParseError("Attribute key is null for composed data " + object);
            checkData = cds.get(attribute_key);
        } else {
            checkData = attr;
        }

        if (info_attribute != null) {
            Object info_attr = info_attribute.equals(attribute) ? attr : connection.getAttribute(new ObjectName(object), info_attribute);
            if (info_key != null && (info_attr instanceof CompositeDataSupport) && verbatim < 4) {
                CompositeDataSupport cds = (CompositeDataSupport) attr;
                infoData = cds.get(info_key);
            } else {
                infoData = info_attr;
            }
        }

    }

    private void parse(String[] args) throws ParseError {
        try {
            for (int i = 0; i < args.length; i++) {
                String option = args[i];
                if (option.equals("-help")) {
                    printHelp(System.out);
                    System.exit(RETURN_UNKNOWN);
                } else if (option.equals("-U")) {
                    this.url = args[++i];
                } else if (option.equals("-O")) {
                    this.object = args[++i];
                } else if (option.equals("-A")) {
                    this.attribute = args[++i];
                } else if (option.equals("-I")) {
                    this.info_attribute = args[++i];
                } else if (option.equals("-J")) {
                    this.info_key = args[++i];
                } else if (option.equals("-K")) {
                    this.attribute_key = args[++i];
                } else if (option.startsWith("-v")) {
                    this.verbatim = option.length() - 1;
                } else if (option.equals("-w")) {
                    this.warning = args[++i];
                } else if (option.equals("-c")) {
                    this.critical = args[++i];
                } else if (option.equals("-username")) {
                    this.username = args[++i];
                } else if (option.equals("-password")) {
                    this.password = args[++i];
                }
            }

            if (url == null || object == null || attribute == null)
                throw new Exception("Required options not specified");
        } catch (Exception e) {
            throw new ParseError(e);
        }

    }

    private void printHelp(PrintStream out) {
        InputStream is = JMXQuery.class.getClassLoader().getResourceAsStream("jmxquery/HELP");
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        try {
            while (true) {
                String s = reader.readLine();
                if (s == null)
                    break;
                out.println(s);
            }
        } catch (IOException e) {
            out.println(e);
        } finally {
            try {
                reader.close();
            } catch (IOException e) {
                out.println(e);
            }
        }
    }

}