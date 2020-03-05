package it.jnrpe.engine.plugins;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class PluginRepositoryTests {

    @Test
    public void testPluginsLoading() {
        PluginRepository pr = new PluginRepository();
        assertEquals(1, pr.getAllPlugins().size());
    }
}
