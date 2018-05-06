package net.martinprobson.jobrunner.main;

import net.martinprobson.jobrunner.*;
import net.martinprobson.jobrunner.common.BaseTask;
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

import static net.martinprobson.jobrunner.TaskResult.Result.NOT_EXECUTED;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

@RunWith(Parameterized.class)
public class RunJobTest {

    public RunJobTest(int testNumber, String testBaseDir) {
        this.testNumber = testNumber;
        this.testDir = new File(Objects.requireNonNull(getClass().getClassLoader().getResource(testBaseDir)).getFile()).getAbsolutePath();
    }

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
        expectedResults = getExpectedResults(testDir);
    }

    @Test
    public void execute() {
        int rc = RunJob.run(testDir,testDir);
        assertEquals(0,rc);
        //
        // Check actual task results against expected results
        //
        for (BaseTask task: RunJob.getJob()) {
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
                actual = RunJob.getJob().getId(e).getTaskResult().getResult();
            } catch (NullPointerException npe) {
                fail("Actual result missing for expected result: " + e + " " + expected);
            }
            assertEquals("Task expected result: " + expected + "  actual: " + actual,
                    expected,actual);
        }


    }
}