package it.jnrpe.engine.commands;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class CommandRegistry {

    private Map<String, Command> commandEntryMap = new HashMap<>();

    static class Command {
        private final String pluginName;
        private final String[] params;

        public Command(final String pluginName, final String[] params) {
            this.pluginName = pluginName;
            this.params = Arrays.copyOf(params, params.length);
        }

        public String getPluginName() {
            return pluginName;
        }

        public String[] getParams() {
            return params;
        }
    }

    public Command getCommand(final String commandName) {
        return commandEntryMap.get(commandName);
    }

}
