package net.martinprobson.jobrunner.sparkjartask;

import com.github.dexecutor.core.task.TaskExecutionException;
import net.martinprobson.jobrunner.ExceptionTaskExecutor;
import net.martinprobson.jobrunner.FailureTaskExecutor;
import net.martinprobson.jobrunner.TaskProvider;
import net.martinprobson.jobrunner.TaskResult;
import net.martinprobson.jobrunner.common.BaseTask;
import net.martinprobson.jobrunner.common.JobRunnerException;
import net.martinprobson.jobrunner.configurationservice.GlobalConfigurationProvider;
import net.martinprobson.jobrunner.dummytask.DummyTaskExecutor;
import net.martinprobson.jobrunner.template.DummyTemplateService;
import nl.jqno.equalsverifier.EqualsVerifier;
import org.junit.BeforeClass;
import org.junit.Test;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

public class SparkJarTaskTest {

    private static TaskProvider taskProvider;

    @BeforeClass
    public static void setUpBeforeClass() {
        taskProvider = TaskProvider.getInstance();
    }

    @Test
    public void getTaskContents() throws JobRunnerException {
        String jar = "DUMMY";
        BaseTask jdbcTask = taskProvider.createTask("spark-jar","test",jar);
        assertEquals(jdbcTask.getTaskContents(),jar);
    }

    @Test
    public void getTaskId() throws JobRunnerException {
        BaseTask jdbcTask = taskProvider.createTask("spark-jar","test","");
        assertEquals(jdbcTask.getId(),"test");
    }

    @Test
    public void TestExecuteSuccess() throws JobRunnerException {
        String jar = "DUMMY.jar";
        BaseTask task = new SparkJarTask(new DummyTemplateService(),
                new DummyTaskExecutor(),
                "test",
                jar,
                GlobalConfigurationProvider.get().getConfiguration());
        TaskResult result = task.execute();
        assertTrue(result.succeeded());
    }

    @Test
    public void TestExecuteFailure() throws JobRunnerException {
        String jar = "DUMMY.jar";
        BaseTask task = new SparkJarTask(new DummyTemplateService(),
                new FailureTaskExecutor(),
                "test",
                jar,
                GlobalConfigurationProvider.get().getConfiguration());
        TaskResult result = task.execute();
        assertTrue(result.failed());
    }

    @Test
    public void TestExecuteException() throws JobRunnerException {
        String jar = "DUMMY.jar";
        BaseTask task = new SparkJarTask(new DummyTemplateService(),
                new ExceptionTaskExecutor(),
                "test",
                jar,
                GlobalConfigurationProvider.get().getConfiguration());
        try {
            TaskResult result = task.execute();
        } catch (TaskExecutionException e) {
            assertTrue(task.getTaskResult().failed());
            return;
        }
        fail("Expected a TaskExecutionException");
    }

    @Test
    public void equalsContract() {
        EqualsVerifier.forClass(SparkJarTask.class)
                .usingGetClass()
                .withIgnoredFields("taskExecutor","config","id","considerExecutionError","result","templateService")
                .verify();
    }

}