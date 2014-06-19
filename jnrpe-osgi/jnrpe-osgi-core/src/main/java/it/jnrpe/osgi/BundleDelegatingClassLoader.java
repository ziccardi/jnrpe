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
package it.jnrpe.osgi;

import java.io.IOException;
import java.net.URL;
import java.util.Enumeration;

import org.osgi.framework.Bundle;

/**
 * A classloader that delegates the class loading to the bundle.
 * 
 * @author Massimiliano Ziccardi
 *
 */
public class BundleDelegatingClassLoader extends ClassLoader {

    /**
     * The delegate bundle.
     */
    private final Bundle bundle;

    /**
     * Builds and initializes the classloader.
     * @param b The bundle to be delegated
     */
    public BundleDelegatingClassLoader(final Bundle b) {
        bundle = b;
    }

    @Override
    protected final Class<?> findClass(final String className) throws ClassNotFoundException {
        return bundle.loadClass(className);
    }

    @Override
    protected final URL findResource(final String resName) {
        return bundle.getResource(resName);
    }

    @SuppressWarnings("unchecked")
    @Override
    protected final Enumeration<URL> findResources(final String resName) throws IOException {
        return bundle.getResources(resName);
    }

}