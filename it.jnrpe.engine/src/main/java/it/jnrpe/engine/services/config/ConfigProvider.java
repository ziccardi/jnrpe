package it.jnrpe.engine.services.config;

import java.util.Optional;

public class ConfigProvider implements IConfigProvider {
    private static ConfigProvider instance;

    private JNRPEConfig config;

    private ConfigProvider(JNRPEConfig config) {
        this.config = config;
    }

    @Override
    public Optional<JNRPEConfig> getConfig() {
        return Optional.of(config);
    }

    public static ConfigProvider getInstance() {
        if (instance == null) {
            for (var confProvider: IConfigProvider.getInstances()) {
                Optional<JNRPEConfig> conf = confProvider.getConfig();
                if (conf.isPresent()) {
                    instance = new ConfigProvider(conf.get());
                    break;
                }
            }
            if (instance == null) {
                instance = new ConfigProvider(null);
            }
        }
        return instance;
    }
}
