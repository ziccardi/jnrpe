package it.jnrpe.plugins;

import it.jnrpe.plugin.service.IPlugin;

public class HelloWorldPlugin implements IPlugin {
    @Override
    public String getName() {
        return "HelloWorld";
    }
}
