package it.jnrpe.utils;

import java.util.HashMap;
import java.util.Map;

class LoadedClassCache {
	private static final Map<ClassLoader, ClassesData> alreadyLodedPlugins = new HashMap<ClassLoader, ClassesData>();

	private final static class ClassesData {
		private final Map<String, Class> loadedClasses = new HashMap<String, Class>();

		public Class getClass(String name) {
			return loadedClasses.get(name);
		}

		public void addClass(Class clazz) {
			loadedClasses.put(clazz.getName(), clazz);
		}
	}

	private static boolean isClassAlreadyLoaded(ClassLoader cl, String name) {
		if (alreadyLodedPlugins.get(cl) == null) {
			alreadyLodedPlugins.put(cl, new ClassesData());
		}

		return alreadyLodedPlugins.get(cl).getClass(name) != null;
	}

	private static void saveClass(ClassLoader cl, Class c) {
		if (alreadyLodedPlugins.get(cl) == null) {
			alreadyLodedPlugins.put(cl, new ClassesData());
		}

		ClassesData cd = alreadyLodedPlugins.get(cl);
		cd.addClass(c);
	}

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
