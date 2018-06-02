package net.martinprobson.jobrunner.sparkpythontask;

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

import static org.junit.Assert.*;

public class SparkPythonTaskTest {

    private static TaskProvider taskProvider;

    @BeforeClass
    public static void setUpBeforeClass() {
        taskProvider = TaskProvider.getInstance();
    }

    @Test
    public void getTaskContents() throws JobRunnerException {
        String jar = "DUMMY";
        BaseTask task = taskProvider.createTask("spark-python","test",jar);
        assertEquals(task.getTaskContents(),jar);
    }

    @Test
    public void getTaskId() throws JobRunnerException {
        BaseTask task = taskProvider.createTask("spark-python","test","");
        assertEquals(task.getId(),"test");
    }

    @Test
    public void TestExecuteSuccess() throws JobRunnerException {
        BaseTask task = new SparkPythonTask(new DummyTemplateService(),
                new DummyTaskExecutor(),
                "test",
                "DUMMY",
                GlobalConfigurationProvider.get().getConfiguration());
        TaskResult result = task.execute();
        assertTrue(result.succeeded());
    }

    @Test
    public void TestExecuteFailure() throws JobRunnerException {
        BaseTask task = new SparkPythonTask(new DummyTemplateService(),
                new FailureTaskExecutor(),
                "test",
                "DUMMY",
                GlobalConfigurationProvider.get().getConfiguration());
        TaskResult result = task.execute();
        assertTrue(result.failed());
    }

    @Test
    public void TestExecuteException() throws JobRunnerException {
        BaseTask task = new SparkPythonTask(new DummyTemplateService(),
                new ExceptionTaskExecutor(),
                "test",
                "DUMMY",
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
        EqualsVerifier.forClass(SparkPythonTask.class)
                .usingGetClass()
                .withIgnoredFields("taskExecutor","config","id","considerExecutionError","result","templateService")
                .verify();
    }
}