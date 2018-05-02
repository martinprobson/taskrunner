package net.martinprobson.jobrunner.jdbctask;

import com.github.dexecutor.core.task.TaskExecutionException;
import com.google.inject.Guice;
import com.google.inject.Injector;
import net.martinprobson.jobrunner.*;
import net.martinprobson.jobrunner.configurationservice.ConfigurationService;
import nl.jqno.equalsverifier.EqualsVerifier;
import org.apache.commons.configuration2.CombinedConfiguration;
import org.junit.BeforeClass;
import org.junit.Test;

import static org.junit.Assert.*;

public class JDBCTaskTest {

    private static TaskFactory taskFactory;

    @BeforeClass
    public static void setUpBeforeClass() {
        ConfigurationService.load(new ConfigurationService(new JobRunnerConfigurationProvider("test_global_config.xml")));
        Injector injector = Guice.createInjector(new JobRunnerModule());
        taskFactory = injector.getInstance(TaskFactory.class);

    }

    @Test
    public void getSql() {
        String sql = "create table foo (bar varchar(1));";
        JDBCTask jdbcTask = taskFactory.createJDBCTask("test",sql);
        assert jdbcTask.getSql().equals(sql);
    }

    @Test
    public void TestExecuteSuccess() {
        String sql = "create table foo (bar varchar(1));";
        JDBCTask jdbcTask = taskFactory.createJDBCTask("TestExecuteSuccess 1",sql);
        TaskResult result = jdbcTask.execute();
        assertTrue(result.succeeded());
        String sql2 = "insert into foo values ('1'); insert into foo values ('2');drop table foo;";
        JDBCTask jdbcTask2 = taskFactory.createJDBCTask("TestExecuteSuccess 2",sql2);
        TaskResult result2 = jdbcTask2.execute();
        assertTrue(result2.succeeded());
    }

    @Test
    public void TestExecuteFailure() {
        String sql = "insert into bob values ('1');";
        JDBCTask jdbcTask = taskFactory.createJDBCTask("TestExecuteFailure",sql);
        try {
            TaskResult result = jdbcTask.execute();
        } catch (TaskExecutionException e) {
            assertTrue(jdbcTask.getTaskResult().failed());
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
        sb.append("drop table bar;");
        JDBCTask jdbcTask = taskFactory.createJDBCTask("TestExecuteSelect 1",sb.toString());
        TaskResult result = jdbcTask.execute();
        assertTrue(result.succeeded());
    }

    @Test
    public void equalsContract() {
        EqualsVerifier.forClass(JDBCTask.class)
                .usingGetClass()
                .withIgnoredFields("taskExecutor","configuration","id","considerExecutionError","result","templateService")
                .withPrefabValues(CombinedConfiguration.class,new CombinedConfiguration(),new CombinedConfiguration())
                .verify();
    }

}