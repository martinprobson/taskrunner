package net.martinprobson.jobrunner;

import com.github.dexecutor.core.task.Task;
import com.google.inject.Guice;
import com.google.inject.Injector;
import net.martinprobson.jobrunner.configurationservice.ConfigurationService;
import org.apache.commons.configuration2.CombinedConfiguration;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.*;

public class JobTest {

    private static Job job;
    private static Map<String,BaseTask> expectedTasks;
    private static TaskProvider taskProvider;

    private static Map<String,BaseTask> getTasks() throws JobRunnerException {
        HashMap<String, BaseTask> tasks = new HashMap<>();
        String taskNames[] = {"t1", "t2", "t3", "t4","t5","t6","t7"};
        for (String task : taskNames)
            //@TODO Fix
            tasks.put(task, taskProvider.createTask("dummy",task,""));
        return tasks;
    }

    @BeforeClass
    public static void setUpBeforeClass() throws Exception {
        ConfigurationService.load(new ConfigurationService(new JobRunnerConfigurationProvider("test_global_config.xml")));
        taskProvider = TaskProvider.getInstance();
        job = new Job( () -> getTasks() );
        expectedTasks = getTasks();
    }

    @Test
    public void hasId() {
        for (String k: expectedTasks.keySet())
            assertTrue(job.hasId(k));
        assertEquals(expectedTasks.size(), job.size());
    }

    @Test
    public void provideTask() {
        for (String k: expectedTasks.keySet()) {
            assertEquals(expectedTasks.get(k), job.provideTask(k));
        }
    }

    @Test
    public void iterator() {
        int count = 0;
        for (Task task: job) {
            assertEquals(expectedTasks.get(task.getId()), task);
            count++;
        }
        assertEquals(expectedTasks.size(),count);

    }

    @Test
    public void testToString() {
        String actual = job.toString();
        for (String k: expectedTasks.keySet()) {
            assertTrue(actual.toLowerCase().contains("id: [" + k));
        }
    }
}