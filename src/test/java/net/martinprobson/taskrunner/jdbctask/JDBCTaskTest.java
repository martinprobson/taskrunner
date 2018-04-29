package net.martinprobson.taskrunner.jdbctask;

import com.github.dexecutor.core.task.TaskExecutionException;
import net.martinprobson.taskrunner.*;
import net.martinprobson.taskrunner.configurationservice.ConfigurationService;
import nl.jqno.equalsverifier.EqualsVerifier;
import org.apache.commons.configuration2.CombinedConfiguration;
import org.junit.BeforeClass;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.helpers.NOPLogger;

import static org.junit.Assert.*;

public class JDBCTaskTest {

    @BeforeClass
    public static void setUpBeforeClass() {
        ConfigurationService.load(new ConfigurationService(new TaskRunnerConfigurationProvider("test_global_config.xml")));
    }

    @Test
    public void getSql() {
        String sql = "create table foo (bar varchar(1));";
        JDBCTask jdbcTask = new JDBCTask("test",sql);
        assert jdbcTask.getSql().equals(sql);
    }

    @Test
    public void TestExecuteSuccess() {
        String sql = "create table foo (bar varchar(1));";
        JDBCTask jdbcTask = new JDBCTask("TestExecuteSuccess 1",sql);
        TaskResult result = jdbcTask.execute();
        assertTrue(result.succeeded());
        String sql2 = "insert into foo values ('1'); insert into foo values ('2');";
        JDBCTask jdbcTask2 = new JDBCTask("TestExecuteSuccess 2",sql2);
        TaskResult result2 = jdbcTask2.execute();
        assertTrue(result2.succeeded());
    }

    @Test
    public void TestExecuteFailure() {
        String sql = "insert into bob values ('1');";
        JDBCTask jdbcTask = new JDBCTask("TestExecuteFailure",sql);
        try {
            TaskResult result = jdbcTask.execute();
        } catch (TaskExecutionException e) {
            assertTrue(jdbcTask.getTaskResult().failed() &&
                    jdbcTask.getTaskResult().exception.getCause().getMessage().toLowerCase().contains("table/view 'bob' does not exist."));
            return;
        }
        fail("Expected a TaskExecutionException");
    }

    @Test
    //@TODO For POC
    public void TestExecuteSelect() {
        StringBuilder sb = new StringBuilder("create table bar (foo varchar(1));");
        sb.append("insert into bar values ('1');");
        sb.append("insert into bar values ('2');");
        sb.append("select * from bar;");
        JDBCTask jdbcTask = new JDBCTask("TestExecuteSelect 1",sb.toString());
        TaskResult result = jdbcTask.execute();
        assertTrue(result.succeeded());
    }

    @Test
    public void equalsContract() {
        EqualsVerifier.forClass(JDBCTask.class)
                .usingGetClass()
                .withIgnoredFields("configuration","id","considerExecutionError","result")
                .withPrefabValues(CombinedConfiguration.class,new CombinedConfiguration(),new CombinedConfiguration())
                .verify();
    }

}