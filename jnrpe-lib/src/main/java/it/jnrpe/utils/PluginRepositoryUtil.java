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
package it.jnrpe.utils;

import it.jnrpe.plugins.IPluginInterface;
import it.jnrpe.plugins.IPluginRepository;
import it.jnrpe.plugins.PluginConfigurationException;
import it.jnrpe.plugins.PluginDefinition;
import it.jnrpe.plugins.PluginOption;
import it.jnrpe.plugins.PluginRepository;
import it.jnrpe.plugins.annotations.Option;
import it.jnrpe.plugins.annotations.Plugin;
import it.jnrpe.plugins.annotations.PluginOptions;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.commons.lang.StringUtils;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.DOMReader;

/**
 * An utility class that allows to define the plugin repository in an XML file
 * instead that using Java Code.
 * 
 * @author Massimiliano Ziccardi
 */
public final class PluginRepositoryUtil {

    /**
     *
     */
    private PluginRepositoryUtil() {

    }

    /**
     * Loads a full repository definition from an XML file.
     * 
     * @param repo
     *            The repository that must be loaded
     * @param cl
     *            The classloader to be used to instantiate the plugin classes
     * @param in
     *            The stream to the XML file
     * @throws PluginConfigurationException
     *             -
     */
    public static void loadFromXmlPluginPackageDefinitions(final IPluginRepository repo, final ClassLoader cl, final InputStream in)
            throws PluginConfigurationException {
        for (PluginDefinition pd : loadFromXmlPluginPackageDefinitions(cl, in)) {
            repo.addPluginDefinition(pd);
        }
    }

    /**
     * Loads the plugins definitions from the jnrpe_plugins.xml file.
     * 
     * @param cl
     *            Classloader to be used to load classes
     * @param in
     *            InputStream to the jnrpe_plugins.xml file
     * @return a collection of all the declared plugins
     * @throws PluginConfigurationException
     *             on any error reading the plugin configuration
     */
    @SuppressWarnings("unchecked")
    public static Collection<PluginDefinition> loadFromXmlPluginPackageDefinitions(final ClassLoader cl, final InputStream in)
            throws PluginConfigurationException {

        List<PluginDefinition> res = new ArrayList<PluginDefinition>();

        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();

        Document document;

        try {
            DocumentBuilder loader = factory.newDocumentBuilder();
            DOMReader reader = new DOMReader();

            document = reader.read(loader.parse(in));
        } catch (Exception e) {
            throw new PluginConfigurationException(e);
        }

        Element plugins = document.getRootElement();

        // TODO : validate against schema

        // iterate through child elements of root
        for (Iterator<Element> i = plugins.elementIterator(); i.hasNext();) {
            Element plugin = i.next();

            PluginDefinition pd = parsePluginDefinition(cl, plugin);
            res.add(pd);
        }

        return res;
    }

    /**
     * Loads the definition of a single plugin from an XML file.
     * 
     * @param cl
     *            The classloader to be used to instantiate the plugin class
     * @param in
     *            The stream to the XML file
     * @return The plugin definition
     * @throws PluginConfigurationException
     *             -
     */
    public static PluginDefinition parseXmlPluginDefinition(final ClassLoader cl, final InputStream in) throws PluginConfigurationException {

        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();

        Document document;

        try {
            DocumentBuilder loader = factory.newDocumentBuilder();
            DOMReader reader = new DOMReader();

            document = reader.read(loader.parse(in));
        } catch (Exception e) {
            throw new PluginConfigurationException(e);
        }

        Element plugin = document.getRootElement();

        // TODO : validate against schema

        return parsePluginDefinition(cl, plugin);
    }

    /**
     * Returns the value of the given attribute name of element. If mandatory is
     * <code>true</code> and the attribute is blank or null, an exception is
     * thrown.
     * 
     * @param element
     *            The element whose attribute must be read
     * @param attributeName
     *            The attribute name
     * @param mandatory
     *            <code>true</code> if the attribute is mandatory
     * @return the attribute value
     * @throws PluginConfigurationException
     *             if the attribute can't be found or is blank
     */
    private static String getAttributeValue(final Element element, final String attributeName, final boolean mandatory)
            throws PluginConfigurationException {
        String returnValue = element.attributeValue(attributeName);

        if (mandatory) {
            if (StringUtils.isBlank(returnValue)) {
                throw new PluginConfigurationException("Error loading plugin package : mandatory attribute " + attributeName + " not found");

            }
        }

        return returnValue;
    }

    /**
     * Parse an XML plugin definition.
     * 
     * @param cl
     *            The classloader to be used to load classes
     * @param plugin
     *            The plugin XML element
     * @return the parsed plugin definition
     * @throws PluginConfigurationException
     *             -
     */
    @SuppressWarnings("rawtypes")
    private static PluginDefinition parsePluginDefinition(final ClassLoader cl, final Element plugin) throws PluginConfigurationException {

        // Check if the plugin definition is inside its own file
        if (getAttributeValue(plugin, "definedIn", false) != null) {
            StreamManager sm = new StreamManager();

            String sFileName = getAttributeValue(plugin, "definedIn", false);

            try {
                InputStream in = sm.handle(cl.getResourceAsStream(sFileName));

                return parseXmlPluginDefinition(cl, in);
            } finally {
                sm.closeAll();
            }
        }

        String pluginClass = getAttributeValue(plugin, "class", true);

        Class c;
        try {
            c = LoadedClassCache.getClass(cl, pluginClass);

            if (!IPluginInterface.class.isAssignableFrom(c)) {
                throw new PluginConfigurationException("Specified class '" + c.getName() + "' in the plugin.xml file does not implement "
                        + "the IPluginInterface interface");
            }

            if (isAnnotated(c)) {
                return loadFromPluginAnnotation(c);
            }

        } catch (ClassNotFoundException e) {
            throw new PluginConfigurationException(e);
        }

        // The class is not annotated not has an external definition file...
        // Loading from current xml file...

        String sDescription = getAttributeValue(plugin, "description", false);

        @SuppressWarnings("unchecked")
        PluginDefinition pluginDef = new PluginDefinition(getAttributeValue(plugin, "name", true), sDescription, c);

        parseCommandLine(pluginDef, plugin);
        return pluginDef;
    }

    /**
     * Updates the plugin definition with the commandline read from the xml
     * file.
     * 
     * @param pluginDef
     *            The plugin definition to be updated
     * @param xmlPluginElement
     *            the xml element to be parsed
     */
    @SuppressWarnings("rawtypes")
    private static void parseCommandLine(final PluginDefinition pluginDef, final Element xmlPluginElement) {
        Element commandLine = xmlPluginElement.element("command-line");

        if (commandLine != null) {
            // The plugin has a command line...
            Element options = commandLine.element("options");

            if (options == null) {
                // The command line is empty...
                return;
            }

            for (Iterator i = options.elementIterator(); i.hasNext();) {
                Element option = (Element) i.next();

                PluginOption po = parsePluginOption(option);

                pluginDef.addOption(po);
            }
        }
    }

    /**
     * Returns <code>true</code> if the class contains plugin annotations.
     * 
     * @param clazz
     *            The plugin class
     * @return <code>true</code> if the class contains plugin
     */
    @SuppressWarnings({ "rawtypes", "unchecked" })
    private static boolean isAnnotated(final Class clazz) {
        Plugin plugin = (Plugin) clazz.getAnnotation(Plugin.class);
        return plugin != null;
    }

    /**
     * Parse a plugin from class annotations.
     * 
     * @param clazz
     *            the plugin class
     * @return PluginDefinition
     */
    @SuppressWarnings({ "rawtypes", "unchecked" })
    public static PluginDefinition loadFromPluginAnnotation(final Class clazz) {
        Plugin plugin = (Plugin) clazz.getAnnotation(Plugin.class);
        PluginOptions options = (PluginOptions) clazz.getAnnotation(PluginOptions.class);
        String name = plugin.name();
        String description = plugin.description();
        PluginDefinition def = new PluginDefinition(name, description, clazz);
        for (Option option : options.value()) {
            def.addOption(parsePluginOption(option));
        }
        return def;
    }

    /**
     * Parses a plugin option XML definition.
     * 
     * @param option
     *            The plugin option XML definition
     * @return The parsed plugin option
     */
    private static PluginOption parsePluginOption(final Element option) {
        PluginOption po = new PluginOption();
        po.setArgName(option.attributeValue("argName"));
        po.setArgsCount(Integer.valueOf(option.attributeValue("argsCount", "1")));
        po.setArgsOptional(Boolean.valueOf(option.attributeValue("optionalArgs", "false")));
        po.setDescription(option.attributeValue("description"));
        po.setHasArgs(Boolean.parseBoolean(option.attributeValue("hasArgs", "false")));
        po.setLongOpt(option.attributeValue("longName"));
        po.setOption(option.attributeValue("shortName"));
        po.setRequired(Boolean.parseBoolean(option.attributeValue("required", "false")));
        po.setType(option.attributeValue("type"));
        po.setValueSeparator(option.attributeValue("separator"));

        return po;
    }

    /**
     * Parses a plugin option from the annotation definition.
     * 
     * @param option
     *            the plugin option
     * @return PluginOption
     */
    private static PluginOption parsePluginOption(final Option option) {
        PluginOption po = new PluginOption();
        po.setArgName(option.argName());
        po.setArgsOptional(option.optionalArgs());
        po.setDescription(option.description());
        po.setHasArgs(option.hasArgs());
        po.setLongOpt(option.longName());
        po.setOption(option.shortName());
        po.setRequired(option.required());
        return po;

    }
}
