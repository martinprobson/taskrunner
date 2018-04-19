package net.martinprobson.taskrunner;

import net.martinprobson.taskrunner.configurationservice.ConfigurationProvider;
import org.apache.commons.configuration2.CompositeConfiguration;
import org.apache.commons.configuration2.Configuration;
import org.apache.commons.configuration2.XMLConfiguration;
import org.apache.commons.configuration2.builder.FileBasedConfigurationBuilder;
import org.apache.commons.configuration2.builder.fluent.Parameters;
import org.apache.commons.configuration2.ex.ConfigurationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 */
public class TaskRunnerConfigurationProvider implements ConfigurationProvider<Configuration> {
    private static final Logger log = LoggerFactory.getLogger(TaskRunnerConfigurationProvider.class);
    private final CompositeConfiguration configuration;

    public TaskRunnerConfigurationProvider(String firstConfig, String... others) {
        configuration = new CompositeConfiguration(getXmlConfig(firstConfig));
        for (String config : others) configuration.addConfiguration(getXmlConfig(config));
    }

    /**
     * Helper method to get a XmlConfiguration from the classpath.
     *
     * @param fileName - Name of the XML configuration file to load.
     * @return - The loaded XMLConfiguration.
     */
    static XMLConfiguration getXmlConfig(String fileName) {
        XMLConfiguration config = null;
        FileBasedConfigurationBuilder<XMLConfiguration> builder =
                new FileBasedConfigurationBuilder<>(XMLConfiguration.class)
                        .configure(new Parameters().xml().setFileName(fileName));
        try {
            config = builder.getConfiguration();
        } catch (ConfigurationException cex) {
            log.error("Loading of configuration failed", cex);
            System.exit(2);
        }
        log.debug("Loaded configuration file: " + fileName);
        return config;
    }

    @Override
    public Configuration getConfiguration() {
        return configuration;
    }

}

