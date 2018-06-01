package net.martinprobson.jobrunner.configurationservice;

/**
 * {@code ConfigurationProvider}
 *
 * @author martinr
 */
public interface ConfigurationProvider<T> {
    T getConfiguration();
}
