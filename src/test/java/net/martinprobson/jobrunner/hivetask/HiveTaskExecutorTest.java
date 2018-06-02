package net.martinprobson.jobrunner.hivetask;

import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import net.martinprobson.jobrunner.TaskProvider;
import net.martinprobson.jobrunner.TaskResult;
import net.martinprobson.jobrunner.common.BaseTask;
import net.martinprobson.jobrunner.common.JobRunnerException;
import org.junit.BeforeClass;
import org.junit.Test;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

public class HiveTaskExecutorTest {

    private static TaskProvider taskProvider;

    @BeforeClass
    public static void setUpBeforeClass() throws Exception {
        taskProvider = TaskProvider.getInstance();
    }

    @Test
    public void checkEnv() throws Exception {
        Config config = ConfigFactory.parseString("hive { environment = ['dummy'] }");
        BaseTask task = taskProvider.createTask("hive","test","", config);
        HiveTaskExecutor executor = new HiveTaskExecutor();
        try {
            executor.checkEnv(task);
        } catch (JobRunnerException e) {
            assertTrue(e.getMessage().contains("not set"));
            return;
        }
        fail("Expected a JobRunnerException");
    }

    @Test
    public void execute() throws Exception {
        BaseTask task = taskProvider.createTask("hive","test","");
        TaskResult taskResult = task.execute();
        assertTrue(taskResult.getProcString().contains("/bin/hive"));
        assertTrue(taskResult.getProcString().contains(".hql"));
    }
}