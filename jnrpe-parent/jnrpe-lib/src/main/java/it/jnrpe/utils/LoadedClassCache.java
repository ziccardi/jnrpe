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

import java.util.HashMap;
import java.util.Map;
import java.util.WeakHashMap;

/**
 * This class stores the already loaded plugin class so that no attempts will be
 * performed to load the same class twice.
 * 
 * @author Massimiliano Ziccardi
 * 
 */
@SuppressWarnings("rawtypes")
class LoadedClassCache {

	/**
	 * Stores data about each created class loader.
	 */
	private static final Map<ClassLoader, ClassesData> alreadyLodedPlugins = new WeakHashMap<ClassLoader, ClassesData>();

	/**
	 * This class stores data about all the classes loaded by a classloader.
	 * 
	 * @author Massimiliano Ziccardi
	 * 
	 */
	private final static class ClassesData {

		/**
		 * Maps classname with corresponding Class object.
		 */
		private final Map<String, Class> loadedClasses = new HashMap<String, Class>();

		/**
		 * @param name
		 *            the class name
		 * @return the cached class object (if any)
		 */
		public Class getClass(String name) {
			return loadedClasses.get(name);
		}

		/**
		 * Adds a class to the cache.
		 * 
		 * @param clazz
		 *            the class to be added
		 */
		public void addClass(Class clazz) {
			loadedClasses.put(clazz.getName(), clazz);
		}
	}

	/**
	 * Stores a class in the cache.
	 * 
	 * @param cl
	 *            The classloader
	 * @param c
	 *            the class to be stored
	 */
	private static void saveClass(ClassLoader cl, Class c) {
		if (alreadyLodedPlugins.get(cl) == null) {
			alreadyLodedPlugins.put(cl, new ClassesData());
		}

		ClassesData cd = alreadyLodedPlugins.get(cl);
		cd.addClass(c);
	}

	/**
	 * Returns a class object. If the class is new, a new Class object is
	 * created, otherwise the cached object is returned.
	 * 
	 * @param cl
	 *            the classloader
	 * @param className
	 *            the class name
	 * @return the class object associated to the given class name
	 * @throws ClassNotFoundException
	 *             if the class can't be loaded
	 */
	public static Class getClass(ClassLoader cl, String className)
			throws ClassNotFoundException {
		if (alreadyLodedPlugins.get(cl) == null) {
			alreadyLodedPlugins.put(cl, new ClassesData());
		}

		ClassesData cd = alreadyLodedPlugins.get(cl);
		Class clazz = cd.getClass(className);
		if (clazz == null) {
			clazz = cl.loadClass(className);
			saveClass(cl, clazz);
		}

		return clazz;
	}
}
