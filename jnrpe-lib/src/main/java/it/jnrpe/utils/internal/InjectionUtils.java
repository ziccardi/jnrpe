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
package it.jnrpe.utils.internal;

import it.jnrpe.IJNRPEExecutionContext;
import it.jnrpe.plugins.IPluginInterface;
import it.jnrpe.plugins.Inject;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;

/**
 * Utility to perform injection of code in JNRPE plugin classes.
 * 
 * @author Massimiliano Ziccardi
 */
public final class InjectionUtils {
    
    /**
     * Private constructor to avoid instantiations.
     */
    private InjectionUtils() {
        
    }
    
    /**
     * Perform the real injection.
     * 
     * @param c the object class
     * @param plugin teh plugin instance
     * @param context the context to be injected
     * @throws IllegalAccessException on any error accessing the field
     */
    @SuppressWarnings("rawtypes")
    private static void inject(final Class c, final IPluginInterface plugin, final IJNRPEExecutionContext context) throws IllegalAccessException {

        Field[] fields = c.getDeclaredFields();

        for (Field f : fields) {
            Annotation an = f.getAnnotation(Inject.class);
            if (an != null) {
                boolean isAccessible = f.isAccessible();

                if (!isAccessible) {
                    // Change accessible flag if necessary
                    f.setAccessible(true);
                }

                f.set(plugin, context);

                if (!isAccessible) {
                    // Restore accessible flag if necessary
                    f.setAccessible(false);
                }
            }
        }
    }

    /**
     * Inject JNRPE code inside the passed in plugin.
     * The annotation is searched in the whole hierarchy.
     * 
     * @param plugin plugin to be injected 
     * @param context the jnrpe context
     */
    @SuppressWarnings("rawtypes")
    public static void inject(final IPluginInterface plugin, final IJNRPEExecutionContext context) {

        try {
            Class c = plugin.getClass();
            inject(c, plugin, context);
            while (IPluginInterface.class.isAssignableFrom(c.getSuperclass())) {
                c = c.getSuperclass();
                inject(c, plugin, context);
            }
        } catch (Exception e) {
            throw new Error(e);
        }

    }

}
