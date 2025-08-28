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
package it.jnrpe.services.plugin;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.ServiceLoader;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.stream.Stream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import it.jnrpe.engine.events.EventManager;
import it.jnrpe.engine.services.events.LogEvent;
import it.jnrpe.engine.services.plugins.IPlugin;
import it.jnrpe.engine.services.plugins.IPluginRepository;

/**
 * The class for the Java service plugin repository.
 *
 * <p>
 * This class implements the {@link IPluginRepository} interface and provides a repository for Java
 * service plugins i.e. for plugin implemented as Java9 Services providers..
 */
public class JavaServicePluginRepository implements IPluginRepository {

  private final File pluginsFolder;
  private final Map<String, IPlugin> pluginsMap = new HashMap<>();
  private final List<URLClassLoader> pluginClassLoaders = new ArrayList<>();

  public JavaServicePluginRepository() {
    this(null);
  }

  public JavaServicePluginRepository(final File pluginsFolder) {
    this.pluginsFolder = pluginsFolder;
    this.initialize();
  }

  /** Loads all the plugins into the repo */
  private void initialize() {

    if (pluginsFolder != null) {
      loadPluginsFromFolder();
      return;
    }

    // Load the plugins only from the system classloader
    ServiceLoader<IPlugin> serviceLoader = ServiceLoader.load(IPlugin.class);
    serviceLoader.forEach(plugin -> pluginsMap.put(plugin.getName(), plugin));
  }

  /**
   * Loads plugins from ZIP files in the specified folder. Each ZIP file should contain JAR files
   * with modular plugins.
   */
  private void loadPluginsFromFolder() {
    if (!pluginsFolder.exists() || !pluginsFolder.isDirectory()) {
      EventManager.warn("Plugins folder does not exist or is not a directory: %s",
          pluginsFolder.getAbsolutePath());
      return;
    }

    File[] zipFiles = pluginsFolder.listFiles((dir, name) -> name.toLowerCase().endsWith(".zip"));
    if (zipFiles == null || zipFiles.length == 0) {
      EventManager.info("No ZIP files found in plugins folder: %s",
          pluginsFolder.getAbsolutePath());
      return;
    }

    EventManager.info("Found %d ZIP file(s) in plugins folder", zipFiles.length);

    for (File zipFile : zipFiles) {
      try {
        loadPluginsFromZipFile(zipFile);
      } catch (Exception e) {
        EventManager.emit(LogEvent.ERROR,
            "Failed to load plugins from ZIP file: " + zipFile.getName(), e);
      }
    }
  }

  /**
   * Loads plugins from a single ZIP file. Creates an isolated ClassLoader for all JAR files in the
   * ZIP.
   */
  private void loadPluginsFromZipFile(File zipFile) throws IOException {
    EventManager.info("Processing ZIP file: %s", zipFile.getName());

    // Create temporary directory for extracting JARs
    Path tempDir =
        Files.createTempDirectory("jnrpe-plugins-" + zipFile.getName().replace(".zip", ""));

    try {
      // Extract all JAR files from the ZIP
      List<Path> jarPaths = extractJarsFromZip(zipFile, tempDir);

      if (jarPaths.isEmpty()) {
        EventManager.warn("No JAR files found in ZIP: %s", zipFile.getName());
        return;
      }

      // Create isolated ClassLoader for this ZIP with ALL JAR files
      URLClassLoader zipClassLoader = createIsolatedClassLoader(jarPaths);
      pluginClassLoaders.add(zipClassLoader);

      // Filter JARs that contain module-info.class to identify service providers
      List<Path> modularJars = filterModularJars(jarPaths);

      if (modularJars.isEmpty()) {
        EventManager.warn("No modular JAR files found in ZIP: %s", zipFile.getName());
        return;
      }

      // Load plugins using ServiceLoader with the isolated ClassLoader
      loadPluginsFromClassLoader(zipClassLoader, zipFile.getName());

    } finally {
      // Clean up temporary directory
      deleteDirectory(tempDir);
    }
  }

  /**
   * Extracts all JAR files from a ZIP to a temporary directory.
   */
  private List<Path> extractJarsFromZip(File zipFile, Path tempDir) throws IOException {
    List<Path> jarPaths = new ArrayList<>();

    try (ZipFile zip = new ZipFile(zipFile)) {
      Enumeration<? extends ZipEntry> entries = zip.entries();

      while (entries.hasMoreElements()) {
        ZipEntry entry = entries.nextElement();

        if (!entry.isDirectory() && entry.getName().toLowerCase().endsWith(".jar")) {
          Path jarPath = tempDir.resolve(Paths.get(entry.getName()).getFileName());
          Files.copy(zip.getInputStream(entry), jarPath);
          jarPaths.add(jarPath);
          EventManager.debug("Extracted JAR: %s", entry.getName());
        }
      }
    }

    return jarPaths;
  }

  /**
   * Filters JAR files to keep only those that contain module-info.class.
   */
  private List<Path> filterModularJars(List<Path> jarPaths) {
    List<Path> modularJars = new ArrayList<>();

    for (Path jarPath : jarPaths) {
      try (JarFile jar = new JarFile(jarPath.toFile())) {
        JarEntry moduleInfo = jar.getJarEntry("module-info.class");
        if (moduleInfo != null) {
          modularJars.add(jarPath);
          EventManager.debug("Found modular JAR: %s", jarPath.getFileName());
        } else {
          EventManager.debug("Skipping non-modular JAR: %s", jarPath.getFileName());
        }
      } catch (IOException e) {
        EventManager.warn("Failed to check JAR for module-info: %s", jarPath, e);
      }
    }

    return modularJars;
  }

  /**
   * Creates an isolated URLClassLoader for the given JAR files.
   */
  private URLClassLoader createIsolatedClassLoader(List<Path> jarPaths) {
    URL[] urls = jarPaths.stream().map(path -> {
      try {
        return path.toUri().toURL();
      } catch (Exception e) {
        throw new PluginLoadingException("Failed to convert path to URL: " + path, e);
      }
    }).toArray(URL[]::new);

    // Create isolated ClassLoader with system ClassLoader as parent
    return new URLClassLoader(urls, ClassLoader.getSystemClassLoader());
  }

  /**
   * Loads plugins from the given ClassLoader using ServiceLoader.
   */
  private void loadPluginsFromClassLoader(URLClassLoader classLoader, String source) {
    try {
      ServiceLoader<IPlugin> serviceLoader = ServiceLoader.load(IPlugin.class, classLoader);
      int pluginCount = 0;

      for (IPlugin plugin : serviceLoader) {
        String pluginName = plugin.getName();
        if (pluginsMap.containsKey(pluginName)) {
          EventManager.warn("Plugin name conflict detected: %s (from %s)", pluginName, source);
        } else {
          pluginsMap.put(pluginName, plugin);
          pluginCount++;
          EventManager.info("Loaded plugin: %s (from %s)", pluginName, source);
        }
      }

      EventManager.info("Loaded %d plugin(s) from %s", pluginCount, source);

    } catch (Exception e) {
      EventManager.error(
          EventManager.withMessage("Failed to load plugins from ClassLoader for: %s", source),
          EventManager.withException(e));
    }
  }

  /**
   * Recursively deletes a directory and its contents.
   */
  private void deleteDirectory(Path directory) {
    try (Stream<Path> paths = Files.walk(directory)) {
      paths.sorted(Comparator.reverseOrder()).map(Path::toFile).forEach(File::delete);
    } catch (IOException e) {
      EventManager.warn("Failed to delete temporary directory: %s", directory, e);
    }
  }

  /**
   * Cleanup method to close all plugin ClassLoaders. Should be called when the repository is no
   * longer needed.
   */
  public void cleanup() {
    for (URLClassLoader classLoader : pluginClassLoaders) {
      try {
        classLoader.close();
      } catch (IOException e) {
        EventManager.warn("Failed to close plugin ClassLoader", e);
      }
    }
    pluginClassLoaders.clear();
  }

  @Override
  public Collection<IPlugin> getAllPlugins() {
    return pluginsMap.values();
  }

  @Override
  public Optional<IPlugin> getPlugin(String pluginName) {
    return Optional.ofNullable(pluginsMap.get(pluginName));
  }
}
