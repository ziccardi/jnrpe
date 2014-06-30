package it.jnrpe.test.utils;

import it.jnrpe.IJNRPEEventBus;
import it.jnrpe.IJNRPEExecutionContext;
import it.jnrpe.commands.CommandRepository;
import it.jnrpe.plugins.IPluginRepository;

import java.nio.charset.Charset;

import com.google.common.eventbus.EventBus;

public class TestContext implements IJNRPEExecutionContext {

    private final IJNRPEEventBus eventBus;
    
    /**
     * Configured charset.
     */
    private final Charset charset;

    private final IPluginRepository pluginRepository;
    private final CommandRepository commandRepository;
    
    /**
     * Builds and initializes the context.
     * 
     * @param eventListeners
     *            the list of listeners
     * @param currentCharset
     *            the configured charset
     */
    public TestContext(final IJNRPEEventBus bus, 
                    final Charset currentCharset,
                    final IPluginRepository pluginRepo,
                    final CommandRepository commandRepo) {
        this.eventBus = bus;
        this.charset = currentCharset;
        this.pluginRepository = pluginRepo;
        this.commandRepository = commandRepo;
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
    public IPluginRepository getPluginRepository() {
        return pluginRepository;
    }

    /* (non-Javadoc)
     * @see it.jnrpe.IJNRPEExecutionContext#getCommandRepository()
     */
    public CommandRepository getCommandRepository() {
        return commandRepository;
    }
    
}