package net.martinprobson.jobrunner.sparkpythontask;

import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import net.martinprobson.jobrunner.TaskProvider;
import net.martinprobson.jobrunner.TaskResult;
import net.martinprobson.jobrunner.common.BaseTask;
import net.martinprobson.jobrunner.common.JobRunnerException;
import org.apache.commons.io.FileUtils;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.File;
import java.nio.charset.Charset;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

public class SparkPythonTaskExecutorTest {

    private static TaskProvider taskProvider;

    private static File createFile(String content) throws Exception {
        File file = File.createTempFile("SparkPythonTaskTest",".py");
        FileUtils.write(file, content, Charset.defaultCharset());
        return file;
    }

    @BeforeClass
    public static void setUpBeforeClass() throws Exception {
        taskProvider = TaskProvider.getInstance();
    }

    @Test
    public void checkEnv() throws Exception {
        Config config = ConfigFactory.parseString("spark-python { environment = ['dummy'] }");
        BaseTask task = taskProvider.createTask("spark-python","test",new File(""), config);
        SparkPythonTaskExecutor executor = new SparkPythonTaskExecutor();
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
        BaseTask task = taskProvider.createTask("spark-python","test",createFile("DUMMY"));
        TaskResult taskResult = task.execute();
        assertTrue(taskResult.getProcString().contains("/bin/spark-submit --master local[*] --num-executors 2 --queue default "));
        assertTrue(taskResult.getProcString().contains(".py"));
    }
}