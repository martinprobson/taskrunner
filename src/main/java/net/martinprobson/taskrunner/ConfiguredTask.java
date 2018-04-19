package net.martinprobson.taskrunner;

import com.github.dexecutor.core.task.Task;
import net.martinprobson.taskrunner.configurationservice.ConfigurationService;
import net.martinprobson.taskrunner.configurationservice.Configured;
import org.apache.commons.configuration2.CombinedConfiguration;
import org.apache.commons.configuration2.Configuration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

abstract class ConfiguredTask<T, R> extends Task<T, R> implements Configured {

    private final CombinedConfiguration configuration;

    private ConfiguredTask(Configuration taskConfiguration) {
        // First get the global configuration ....
        Configuration conf = ConfigurationService.getConfiguration();
        // add it to our CombinedConfiguration....
        configuration = new CombinedConfiguration();
        configuration.addConfiguration(conf);
        // and then add the task specific configuration....
        configuration.addConfiguration(taskConfiguration);
    }

    ConfiguredTask(String taskConfiguration) {
        this(TaskRunnerConfigurationProvider.getXmlConfig(taskConfiguration));
    }

    public Configuration getConfiguration() {
        return configuration;
    }

}
