package net.martinprobson.jobrunner.hivetask;

import com.github.dexecutor.core.task.TaskExecutionException;
import net.martinprobson.jobrunner.ExceptionTaskExecutor;
import net.martinprobson.jobrunner.FailureTaskExecutor;
import net.martinprobson.jobrunner.TaskProvider;
import net.martinprobson.jobrunner.TaskResult;
import net.martinprobson.jobrunner.common.BaseTask;
import net.martinprobson.jobrunner.configurationservice.GlobalConfigurationProvider;
import net.martinprobson.jobrunner.dummytask.DummyTaskExecutor;
import net.martinprobson.jobrunner.template.DummyTemplateService;
import nl.jqno.equalsverifier.EqualsVerifier;
import org.apache.commons.io.FileUtils;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.File;
import java.nio.charset.Charset;

import static org.junit.Assert.*;

public class HiveTaskTest {

    private static TaskProvider taskProvider;

    private static File createFile(String content) throws Exception {
        File file = File.createTempFile("JDBCTaskTest","");
        FileUtils.write(file, content, Charset.defaultCharset());
        return file;
    }

    @BeforeClass
    public static void setUpBeforeClass() {
        taskProvider = TaskProvider.getInstance();
    }

    @Test
    public void getTaskContents() throws Exception {
        BaseTask task = taskProvider.createTask("hive","test",createFile("DUMMY"));
        assertEquals(task.getTaskContents(),"DUMMY");
    }

    @Test
    public void getTaskId() throws Exception {
        BaseTask task = taskProvider.createTask("hive","test",new File(""));
        assertEquals(task.getId(),"test");
    }

    @Test
    public void TestExecuteSuccess() throws Exception {
        BaseTask task = new HiveTask(new DummyTemplateService(),
                new DummyTaskExecutor(),
                "test",
                createFile("DUMMY"),
                GlobalConfigurationProvider.get().getConfiguration());
        TaskResult result = task.execute();
        assertTrue(result.succeeded());
    }

    @Test
    public void TestExecuteFailure() throws Exception {
        BaseTask task = new HiveTask(new DummyTemplateService(),
                new FailureTaskExecutor(),
                "test",
                createFile("DUMMY"),
                GlobalConfigurationProvider.get().getConfiguration());
        TaskResult result = task.execute();
        assertTrue(result.failed());
    }

    @Test
    public void TestExecuteException() throws Exception {
        BaseTask task = new HiveTask(new DummyTemplateService(),
                new ExceptionTaskExecutor(),
                "test",
                createFile("DUMMY"),
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
        EqualsVerifier.forClass(HiveTask.class)
                .usingGetClass()
                .withIgnoredFields("taskExecutor","config","id","considerExecutionError","result","templateService")
                .verify();
    }

}