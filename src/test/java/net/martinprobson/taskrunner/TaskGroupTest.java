package net.martinprobson.taskrunner;

import com.github.dexecutor.core.task.Task;
import net.martinprobson.taskrunner.configurationservice.ConfigurationService;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.*;

public class TaskGroupTest {

    private static TaskGroup taskGroup;
    private static Map<String,DependentTask> expectedTasks;

    private static Map<String,DependentTask> getTasks() {
        HashMap<String, DependentTask> tasks = new HashMap<>();
        String taskNames[] = {"t1", "t2", "t3", "t4","t5","t6","t7"};
        for (String task : taskNames)
            tasks.put(task,new DummyTask(task));
        return tasks;
    }

    @BeforeClass
    public static void setUpBeforeClass() throws Exception {
        ConfigurationService.load(new ConfigurationService(new TaskRunnerConfigurationProvider("test_global_config.xml")));
        taskGroup = new TaskGroup( () -> getTasks());
        expectedTasks = getTasks();
    }

    @Test
    public void hasId() {
        for (String k: expectedTasks.keySet())
            assertTrue(taskGroup.hasId(k));
        assertEquals(expectedTasks.size(),taskGroup.size());
    }

    @Test
    public void provideTask() {
        for (String k: expectedTasks.keySet()) {
            assertEquals(expectedTasks.get(k),taskGroup.provideTask(k));
        }
    }

    @Test
    public void iterator() {
        int count = 0;
        for (Task task: taskGroup) {
            assertEquals(expectedTasks.get(task.getId()), task);
            count++;
        }
        assertEquals(expectedTasks.size(),count);

    }

    @Test
    public void testToString() {
        String actual = taskGroup.toString();
        for (String k: expectedTasks.keySet()) {
            assertTrue(actual.toLowerCase().contains("id: " + k));
        }
    }
}