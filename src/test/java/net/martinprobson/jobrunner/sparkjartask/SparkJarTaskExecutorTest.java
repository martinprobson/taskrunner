package net.martinprobson.jobrunner.sparkjartask;

import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import net.martinprobson.jobrunner.TaskProvider;
import net.martinprobson.jobrunner.TaskResult;
import net.martinprobson.jobrunner.common.BaseTask;
import net.martinprobson.jobrunner.common.JobRunnerException;
import org.junit.BeforeClass;
import org.junit.Test;

import static org.junit.Assert.*;

public class SparkJarTaskExecutorTest {

    private static TaskProvider taskProvider;

    @BeforeClass
    public static void setUpBeforeClass() throws Exception {
        taskProvider = TaskProvider.getInstance();
    }

    @Test
    public void checkEnv() throws Exception {
        Config config = ConfigFactory.parseString("spark-jar { environment = ['dummy'] }");
        BaseTask task = taskProvider.createTask("spark-jar","test","", config);
        SparkJarTaskExecutor executor = new SparkJarTaskExecutor();
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
        BaseTask task = taskProvider.createTask("spark-jar","test","");
        TaskResult taskResult = task.execute();
        assertTrue(taskResult.getProcString().endsWith("/bin/spark-submit --master local[*] --num-executors 2 --queue default "));
    }
}