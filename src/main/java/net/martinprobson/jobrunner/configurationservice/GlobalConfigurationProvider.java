package net.martinprobson.jobrunner.configurationservice;

import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;

public class GlobalConfigurationProvider implements ConfigurationProvider<Config> {

    @Override
    public Config getConfiguration() {
        return ConfigFactory.load();
    }
}
