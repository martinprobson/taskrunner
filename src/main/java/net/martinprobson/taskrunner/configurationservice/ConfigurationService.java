package net.martinprobson.taskrunner.configurationservice;


import org.apache.commons.configuration2.Configuration;

public class ConfigurationService {

    private static ConfigurationService service;
    private final Configuration configuration;

    public ConfigurationService(ConfigurationProvider<Configuration> configurationProvider) {
        this.configuration = configurationProvider.getConfiguration();
    }

    public static Configuration getConfiguration() {
        return service.configuration;
    }

    public static void load(ConfigurationService configurationService) {
        ConfigurationService.service = configurationService;
    }

}
