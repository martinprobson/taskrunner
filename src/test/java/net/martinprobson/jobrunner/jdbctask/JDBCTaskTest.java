package net.martinprobson.jobrunner.jdbctask;

import com.github.dexecutor.core.task.TaskExecutionException;
import net.martinprobson.jobrunner.*;
import net.martinprobson.jobrunner.common.BaseTask;
import nl.jqno.equalsverifier.EqualsVerifier;
import org.apache.commons.io.FileUtils;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.File;
import java.nio.charset.Charset;

import static org.junit.Assert.*;

public class JDBCTaskTest {

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
    public void getTask() throws Exception {
        String content = "create table foo (bar varchar(1));\n";
        BaseTask jdbcTask = taskProvider.createTask("jdbc","test",createFile(content));
        assert jdbcTask.getTaskContents().equals(content);
    }

    @Test
    public void TestExecuteSuccess() throws Exception {
        String sql1 = "create table foo (bar varchar(1));\n";
        BaseTask jdbcTask = taskProvider.createTask("jdbc","TestExecuteSuccess 1",createFile(sql1));
        TaskResult result = jdbcTask.execute();
        assertTrue(result.succeeded());
        String sql2 = "insert into foo values ('1'); insert into foo values ('2');drop table foo;";
        BaseTask jdbcTask2 = taskProvider.createTask("jdbc","TestExecuteSuccess 2",createFile(sql2));
        TaskResult result2 = jdbcTask2.execute();
        assertTrue(result2.succeeded());
    }

    @Test
    public void TestExecuteFailure() throws Exception {
        String sql = "insert into bob values ('1');";
        BaseTask jdbcTask = taskProvider.createTask("jdbc","TestExecuteFailure",createFile(sql));
        try {
            TaskResult result = jdbcTask.execute();
        } catch (TaskExecutionException e) {
            assertTrue(jdbcTask.getTaskResult().failed());
            return;
        }
        fail("Expected a TaskExecutionException");
    }

    @Test
    public void TestExecuteSelect() throws Exception {
        StringBuilder sb = new StringBuilder("create table bar (foo varchar(1));");
        sb.append("insert into bar values ('1');");
        sb.append("insert into bar values ('2');");
        sb.append("select * from bar;");
        sb.append("drop table bar;");
        BaseTask jdbcTask = taskProvider.createTask("jdbc","TestExecuteSelect 1",createFile(sb.toString()));
        TaskResult result = jdbcTask.execute();
        assertTrue(result.succeeded());
    }

    @Test
    public void equalsContract() {
        EqualsVerifier.forClass(JDBCTask.class)
                .usingGetClass()
                .withIgnoredFields("taskExecutor","config","id","considerExecutionError","result","templateService")
                .verify();
    }

}