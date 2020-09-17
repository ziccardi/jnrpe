package it.jnrpe.server.commands.plugins;

import it.jnrpe.engine.plugins.PluginRepository;
import it.jnrpe.engine.services.commands.ExecutionResult;
import it.jnrpe.engine.services.plugins.CommandLine;
import it.jnrpe.engine.services.plugins.IPlugin;

import java.util.List;

@CommandLine.Command(
        name = "execute",
        description = "Executes a given plugin",
        aliases = {"exec"})
public class ExecCommand implements Runnable {
    @CommandLine.Parameters(index = "0")
    private String pluginName;

    @CommandLine.Parameters(index = "1..*")
    private List<String> pluginParameters;

    private void execute(IPlugin plugin) {
        CommandLine cl = new CommandLine(plugin);
        try {
            cl.parseArgs(pluginParameters.toArray(new String[pluginParameters.size()]));

            ExecutionResult res = plugin.execute();
            final String label;
            switch (res.getStatus()) {
                case OK:
                    label = String.format("[%s - OK]", pluginName);
                    break;
                case WARNING:
                    label = String.format("[%s - WARNING]", pluginName);
                    break;
                case CRITICAL:
                    label = String.format("[%s - CRITICAL]", pluginName);
                    break;
                default:
                    label = String.format("[%s - UNKNOWN]", pluginName);
            }
            System.out.printf("%s - %s\n", label, res.getMessage());
        } catch(Exception e) {
            System.out.println("Error executing plugin: " + e.getMessage());
            cl.usage(System.out);
        }
    }

    @Override
    public void run() {
        PluginRepository.getInstance().getPlugin(pluginName)
                .ifPresentOrElse(
                        this::execute,
                        () -> System.out.printf("No plugin named [%s] has been found\n", pluginName)
                );
    }
}
