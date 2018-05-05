package net.martinprobson.jobrunner;

import com.github.dexecutor.core.ExecutionConfig;
import net.martinprobson.jobrunner.common.BaseTask;
import net.martinprobson.jobrunner.common.JobRunnerException;
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
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static net.martinprobson.jobrunner.TaskResult.Result.NOT_EXECUTED;
import static org.junit.Assert.*;

@RunWith(Parameterized.class)
public class JobRunnerTest {

    public JobRunnerTest(int testNumber, String testBaseDir, ExecutorService executorService) {
        this.testNumber = testNumber;
        this.testDir = new File(Objects.requireNonNull(getClass().getClassLoader().getResource(testBaseDir)).getFile()).getAbsolutePath();
        this.executorService = executorService;
    }

    private static final Logger log = LoggerFactory.getLogger(JobRunnerTest.class);

    private int testNumber;
    private String testDir;
    private ExecutorService executorService;
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
                {  1, "taskrunner_test1" ,Executors.newSingleThreadExecutor()},
                {  2, "taskrunner_test1" ,Executors.newFixedThreadPool(4)},
                {  3, "taskrunner_test2" ,Executors.newSingleThreadExecutor()},
                {  4, "taskrunner_test2" ,Executors.newFixedThreadPool(4)},
                {  5, "taskrunner_test3" ,Executors.newSingleThreadExecutor()},
                {  6, "taskrunner_test3" ,Executors.newFixedThreadPool(40)},
                {  7, "taskrunner_test4" ,Executors.newFixedThreadPool(40)},
                {  8, "taskrunner_test5" ,Executors.newSingleThreadExecutor()},
                {  9, "taskrunner_test5" ,Executors.newFixedThreadPool(2)},
                {  10, "taskrunner_test6",Executors.newSingleThreadExecutor()},
                {  11, "taskrunner_test6",Executors.newFixedThreadPool(2)},
                {  12, "taskrunner_test7",Executors.newSingleThreadExecutor()},
                {  13, "taskrunner_test7",Executors.newFixedThreadPool(2)},

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
        System.out.println(expectedResults);
    }

    @After
    public void tearDown() {
        try {
            executorService.shutdownNow();
            executorService.awaitTermination(1, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            log.error("Interrupt",e);
        }
    }

    @Test
    public void execute() {
        JobRunnerConfig config = null;
        try {
            config = new JobRunnerConfig(executorService,
                                  new Job(LocalFileSystemTaskBuilder.create(testDir,testDir)));

            JobRunner jobRunner = new JobRunner(config);
            jobRunner.execute(ExecutionConfig.NON_TERMINATING);
        } catch (JobRunnerException e) {
            fail("Unexpected Exception: " + e);
        }
        // Check expected results against actual task results.
        //
        for (BaseTask task: config.getJob()) {
            TaskResult.Result expected = expectedResults.get(task.getId()).getExpResult();
            TaskResult.Result actual   = task.getTaskResult().getResult();
            assertEquals("Task: " + task.getId() + " expected: " + expected + " actual: " + actual,
                    expected,actual);
        }
        //
        // and the other way around...
        // check expected results against actual results.
        //
        for (String e: expectedResults.keySet()) {
            TaskResult.Result expected = expectedResults.get(e).getExpResult();
            if (expected.equals(NOT_EXECUTED)) continue;
            TaskResult.Result actual = null;
            try {
                actual = config.getJob().getId(e).getTaskResult().getResult();
            } catch (NullPointerException npe) {
                fail("Actual result missing for expected result: " + e + " " + expected);
            }
            assertEquals("Task expected result: " + expected + "  actual: " + actual,
                    expected,actual);
        }

    }
}