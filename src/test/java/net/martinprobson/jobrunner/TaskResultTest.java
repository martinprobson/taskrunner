package net.martinprobson.jobrunner;

import com.github.dexecutor.core.task.Task;
import org.junit.Test;

import static org.junit.Assert.*;

public class TaskResultTest {

    @Test
    public void testToString() {
        TaskResult tr = new TaskResult.Builder(TaskResult.Result.NOT_EXECUTED)
                .exception(new Exception("Test Exception"))
                .error("Error")
                .exitValue(1)
                .output("Output")
                .procString("Proc")
                .build();
        assertEquals(
                "TaskResult{exception=java.lang.Exception: Test Exception, result=NOT_EXECUTED, exitValue=1, error=Error, output=Output, procString=Proc}",
                tr.toString());
    }

    @Test
    public void getException() {
        Exception e = new Exception("Test");
        TaskResult tr = new TaskResult.Builder(TaskResult.Result.NOT_EXECUTED).exception(e).build();
        assertEquals(e,tr.getException());
    }

    @Test
    public void getExitValue() {
        assertEquals(-100,
                new TaskResult.Builder(TaskResult.Result.NOT_EXECUTED)
                        .exitValue(-100)
                        .build()
                        .getExitValue());
    }

    @Test
    public void getError() {
        assertEquals("This is an error",
                new TaskResult.Builder(TaskResult.Result.NOT_EXECUTED)
                        .error("This is an error")
                        .build()
                        .getError());
    }

    @Test
    public void getOutput() {
        assertEquals("This is output",
                new TaskResult.Builder(TaskResult.Result.NOT_EXECUTED)
                        .output("This is output")
                        .build()
                        .getOutput());
    }

    @Test
    public void getProcString() {
        assertEquals("Command",
                new TaskResult.Builder(TaskResult.Result.NOT_EXECUTED)
                        .procString("Command")
                        .build()
                        .getProcString());
    }

    @Test
    public void getResult() {
        assertEquals(TaskResult.Result.SUCCESS,
                new TaskResult.Builder(TaskResult.Result.SUCCESS)
                        .build()
                        .getResult());
    }

    @Test
    public void succeeded() {
        TaskResult r = new TaskResult.Builder(TaskResult.Result.SUCCESS).build();
        assertTrue(r.succeeded());
    }

    @Test
    public void failed() {
        TaskResult r = new TaskResult.Builder(TaskResult.Result.FAILED).build();
        assertTrue(r.failed());
    }
}