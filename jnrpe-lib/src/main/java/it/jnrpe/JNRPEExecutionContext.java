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
package it.jnrpe;

import it.jnrpe.commands.CommandRepository;
import it.jnrpe.plugins.IPluginRepository;

import java.nio.charset.Charset;

/**
 * A context to be passed to all the object managing the communication.
 * 
 * @author Massimiliano Ziccardi
 */
public class JNRPEExecutionContext implements IJNRPEExecutionContext {

    /**
     * The event bus.
     */
    private final IJNRPEEventBus eventBus;
    
    /**
     * Configured charset.
     */
    private final Charset charset;

//    /**
//     * The plugin repository.
//     */
//    private final IPluginRepository pluginRepository;
//    
//    /**
//     * The command repository.
//     */
//    private final CommandRepository commandRepository;
    
    /**
     * Builds and initializes the context.
     * 
     * @param bus
     *            the event bus
     * @param currentCharset
     *            the configured charset
     */
    JNRPEExecutionContext(final IJNRPEEventBus bus, 
                    final Charset currentCharset,
                    final IPluginRepository pluginRepo,
                    final CommandRepository commandRepo) {
        this.eventBus = bus;
        this.charset = currentCharset;
//        this.pluginRepository = pluginRepo;
//        this.commandRepository = commandRepo;
    }

    /* (non-Javadoc)
     * @see it.jnrpe.IJNRPEExecutionContext#getEventBus()
     */
    public final IJNRPEEventBus getEventBus() {
        return eventBus;
    }

    /* (non-Javadoc)
     * @see it.jnrpe.IJNRPEExecutionContext#getCharset()
     */
    public final Charset getCharset() {
        return charset;
    }

    /* (non-Javadoc)
     * @see it.jnrpe.IJNRPEExecutionContext#getPluginRepository()
     */
//    public IPluginRepository getPluginRepository() {
//        return pluginRepository;
//    }
//
//    /* (non-Javadoc)
//     * @see it.jnrpe.IJNRPEExecutionContext#getCommandRepository()
//     */
//    public CommandRepository getCommandRepository() {
//        return commandRepository;
//    }
}
