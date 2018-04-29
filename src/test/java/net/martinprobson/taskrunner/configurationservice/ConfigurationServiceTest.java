package net.martinprobson.taskrunner.configurationservice;

import net.martinprobson.taskrunner.TaskRunnerConfigurationProvider;
import net.martinprobson.taskrunner.TaskRunnerException;
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
            thrown.expectMessage(startsWith("TaskRunner requires a global"));
            ConfigurationService.clear();
            ConfigurationService.getConfiguration();
    }

    @Test
    public void getConfigurationTest2() {
        ConfigurationService.load(new ConfigurationService(new TaskRunnerConfigurationProvider("configprovider_test_1.xml")));
        assertEquals("foo",ConfigurationService.getConfiguration().getString("test"));
    }

}