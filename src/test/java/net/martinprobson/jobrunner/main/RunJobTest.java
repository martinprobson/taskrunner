package net.martinprobson.jobrunner.main;

import com.github.dexecutor.core.ExecutionConfig;
import net.martinprobson.jobrunner.*;
import net.martinprobson.jobrunner.configurationservice.ConfigurationService;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileInputStream;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

@RunWith(Parameterized.class)
public class RunJobTest {

    public RunJobTest(int testNumber, String testBaseDir) {
        this.testNumber = testNumber;
        this.testDir = new File(Objects.requireNonNull(getClass().getClassLoader().getResource(testBaseDir)).getFile()).getAbsolutePath();
    }

    private static final Logger log = LoggerFactory.getLogger(RunJobTest.class);

    private int testNumber;
    private String testDir;
    private Map<String,ExpectedResult> expectedResults;

    static class ExpectedResult {
        final TaskResult.Result taskResult;
        ExpectedResult(TaskResult.Result status) {
            this.taskResult = status;
        }
        TaskResult.Result getExpResult() {
            return taskResult;
        }
    }

    @Parameters(name = "{index}: execute(TestNumber:{0})={1}")
    public static Collection<Object[]> data() {
        return Arrays.asList(new Object[][] {
                {  1, "taskrunner_test1"},
                {  2, "taskrunner_test2"},
                {  3, "taskrunner_test3"},
                {  4, "taskrunner_test4"},
                {  5, "taskrunner_test5"},
                {  6, "taskrunner_test6"},
                {  7, "taskrunner_test7"},
        });
    }

    private static Map<String,ExpectedResult> getExpectedResults(String testDir) throws Exception {
        Map<String,ExpectedResult> expectedResults = new HashMap<>();
        Properties props = new Properties();
        props.load(new FileInputStream(testDir + File.separatorChar + "expectedResults.properties" ));
        for (String key : props.stringPropertyNames()) {
            String executionStatus = props.getProperty(key).toUpperCase();
            expectedResults.put(key,new ExpectedResult(TaskResult.Result.valueOf(executionStatus)));
        }
        return expectedResults;
    }

    @Before
    public void setUp() throws Exception {
        // Wire up our configuration to our global configuration service.
        ConfigurationService.load(new ConfigurationService(new JobRunnerConfigurationProvider("test_global_config.xml")));
        expectedResults = getExpectedResults(testDir);
    }

    @Test
    public void execute() {
        String cfg = Paths.get("src","test","resources","test_global_config.xml").toFile().getAbsolutePath();
        int rc = RunJob.run(testDir,testDir,cfg);
        assertEquals(0,rc);
        // Check expected results against actual task results.
        //
        for (BaseTask task: RunJob.getJob()) {
            TaskResult.Result expected = expectedResults.get(task.getId()).getExpResult();
            TaskResult.Result actual   = task.getTaskResult().getResult();
            assertEquals("Task: " + task.getId() + " expected: " + expected + " actual: " + actual,
                    expected,actual);
        }
    }
}