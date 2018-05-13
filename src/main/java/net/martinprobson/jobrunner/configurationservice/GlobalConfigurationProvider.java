package net.martinprobson.jobrunner.configurationservice;

import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;

import java.io.File;

public final class GlobalConfigurationProvider implements ConfigurationProvider<Config> {

    private static GlobalConfigurationProvider singleton;
    private final Config config;

    @Override
    public Config getConfiguration() {
        return config;
    }

    public static void setConfig(File globalConfigFile) {
        singleton = new GlobalConfigurationProvider(ConfigFactory.parseFile(globalConfigFile)
                .withFallback(ConfigFactory.load()));
    }

    public static ConfigurationProvider<Config> get() {
        if (singleton == null)
            singleton = new GlobalConfigurationProvider(ConfigFactory.load());
        return singleton;
    }


    private GlobalConfigurationProvider(Config config) {
        this.config = config;
    }
}
