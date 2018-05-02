package net.martinprobson.jobrunner.configurationservice;

import net.martinprobson.jobrunner.JobRunnerConfigurationProvider;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.util.ServiceConfigurationError;

import static org.hamcrest.CoreMatchers.startsWith;
import static org.junit.Assert.*;

public class ConfigurationServiceTest {

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Test
    public void getConfigurationTest1() {
            thrown.expect(ServiceConfigurationError.class);
            thrown.expectMessage(startsWith("JobRunner requires a global"));
            ConfigurationService.clear();
            ConfigurationService.getConfiguration();
    }

    @Test
    public void getConfigurationTest2() {
        ConfigurationService.load(new ConfigurationService(new JobRunnerConfigurationProvider("configprovider_test_1.xml")));
        assertEquals("foo",ConfigurationService.getConfiguration().getString("test"));
    }

}