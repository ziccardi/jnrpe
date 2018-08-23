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
package it.jnrpe.plugin.tomcat;

import it.jnrpe.plugin.utils.Utils;

import java.io.StringReader;
import java.net.URL;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathFactory;

import org.apache.commons.codec.binary.Base64;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

class TomcatDataProvider implements IAppServerDataProvider {

    private String uri;
    private int port;
    private String username;
    private String password;
    private String hostname;
    private boolean useSSL;
    
    private String tomcatXML = null;
    
    private MemoryData jvmMemoryUsage = null;
    private Map<String, MemoryPoolData> memoryPoolData = new HashMap<String, MemoryPoolData>();
    private Map<String, ThreadData> connectorThreadData = new HashMap<String, ThreadData>();
    
    public TomcatDataProvider() {
        
    }
    
    public void init(String hostname, String uri, int port, String username, String password, boolean useSSL, int timeout) throws Exception {
        
        this.uri = uri;
        this.port = port;
        this.username = username;
        this.password = password;
        this.hostname = hostname;
        this.useSSL = useSSL;
        
        // Retrieve tomcat xml...
        String encoded = Base64.encodeBase64String((username + ":" + password).getBytes("UTF-8"));
        Properties props = new Properties();
        props.setProperty("Authorization", "Basic " + encoded);
        String response = Utils.getUrl(new URL(getUrl()), props, timeout * 1000);

        if (response == null) {
            throw new Exception("Null response received from tomcat");
        }
        tomcatXML = response;
        
        parseMemoryData();
        parseMemoryPools();
        parseConnectorsThreadData();
    }
    
    private void parseMemoryData() throws Exception {
        
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        
        InputSource is = new InputSource(new StringReader(tomcatXML));
        
        Document doc = builder.parse(is);
        XPathFactory xPathfactory = XPathFactory.newInstance();
        XPath xpath = xPathfactory.newXPath();
        //Element root = (Element) xpath.compile("//status").evaluate(doc.getDocumentElement(), XPathConstants.NODE);
        Element memory = (Element) xpath.compile("//status/jvm/memory").evaluate(doc.getDocumentElement(), XPathConstants.NODE);
        
        long freeMem = Long.parseLong(memory.getAttribute("free"));
        long totalMem = Long.parseLong(memory.getAttribute("total"));
        long maxMem = Long.parseLong(memory.getAttribute("max"));

        jvmMemoryUsage = new MemoryData(freeMem, maxMem, totalMem);
    }
    
    private void parseMemoryPools() throws Exception {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        
        InputSource is = new InputSource(new StringReader(tomcatXML));
        
        Document doc = builder.parse(is);
        XPathFactory xPathfactory = XPathFactory.newInstance();
        XPath xpath = xPathfactory.newXPath();
        
        NodeList memoryPoolList = (NodeList) xpath.compile("//status/jvm/memorypool").evaluate(doc.getDocumentElement(), XPathConstants.NODESET);
        
        for (int i = 0; i < memoryPoolList.getLength(); i++) {
            Node poolNode = memoryPoolList.item(i);
            NamedNodeMap atts = poolNode.getAttributes();
            final String poolName = atts.getNamedItem("name").getNodeValue();
            long usageInit = Long.parseLong(atts.getNamedItem("usageInit").getNodeValue());
            long usageCommitted = Long.parseLong(atts.getNamedItem("usageCommitted").getNodeValue());
            long usageMax = Long.parseLong(atts.getNamedItem("usageMax").getNodeValue());
            long usageUsed = Long.parseLong(atts.getNamedItem("usageUsed").getNodeValue());
            
            memoryPoolData.put(poolName, new MemoryPoolData(poolName, usageInit, usageCommitted, usageMax, usageUsed));
        }
    }
    
    private void parseConnectorsThreadData() throws Exception {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        
        InputSource is = new InputSource(new StringReader(tomcatXML));
        
        Document doc = builder.parse(is);
        XPathFactory xPathfactory = XPathFactory.newInstance();
        XPath xpath = xPathfactory.newXPath();
        
        NodeList connectorsNodeList = (NodeList) xpath.compile("//status/connector").evaluate(doc.getDocumentElement(), XPathConstants.NODESET);
        
        for (int i = 0; i < connectorsNodeList.getLength(); i++) {
            Node connector = connectorsNodeList.item(i);
            NodeList connectorChildren = connector.getChildNodes();
            
            final String connectorName = connector.getAttributes().getNamedItem("name").getNodeValue();
            for (int j = 0; j < connectorChildren.getLength(); j++) {
                Node node = connectorChildren.item(j);
                if (node.getNodeName().equalsIgnoreCase("threadInfo")) {
                    
                    NamedNodeMap atts = node.getAttributes();
                    
                    long maxThreads = Long.parseLong(atts.getNamedItem("maxThreads").getNodeValue());
                    long currentThreadsBusy = Long.parseLong(atts.getNamedItem("currentThreadsBusy").getNodeValue());
                    long currentThreadCount = Long.parseLong(atts.getNamedItem("currentThreadCount").getNodeValue());
                    
                    connectorThreadData.put(connectorName, new ThreadData(connectorName, currentThreadCount, currentThreadsBusy, maxThreads));
                }
            }
        }
    }
    
    private String getUrl() {

        String path = uri;
        
        if (!path.startsWith("/")) {
            path = "/" + uri;
        }
        
        String protocol;
        String credentials;

        if (useSSL) {
            protocol = "https://";
        } else {
            protocol = "http://";
        }

        if (password != null) {
            credentials = username + ":" + password;
        } else {
            credentials = username + ":";
        }

        String url = protocol + credentials + "@" + hostname + ":" + port + path;

        return url;
    }
    
    public MemoryData getJVMMemoryUsage() {
        return jvmMemoryUsage;
    }

    public Collection<MemoryPoolData> getMemoryPoolData() {
        return memoryPoolData.values();
    }

    public Collection<ThreadData> getThreadData() {
        return connectorThreadData.values();
    }

}
