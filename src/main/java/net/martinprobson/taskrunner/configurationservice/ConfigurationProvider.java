package net.martinprobson.taskrunner.configurationservice;

import org.apache.commons.configuration2.ImmutableConfiguration;

public interface ConfigurationProvider<T extends ImmutableConfiguration> {
    T getConfiguration();
}
