package net.martinprobson.taskrunner;

import org.junit.Test;

import static org.junit.Assert.*;

public class TaskResultTest {

    @Test
    public void succeeded() {
        TaskResult t = new TaskResult(TaskResult.Result.SUCCESS);
        assertTrue(t.succeeded());
        assertFalse(t.failed());
        TaskResult t2 = new TaskResult(TaskResult.Result.FAILED);
        assertTrue(t2.failed());
        assertFalse(t2.succeeded());
    }

    @Test
    public void failed() {
        TaskResult t = new TaskResult(TaskResult.Result.SUCCESS);
        assertTrue(t.succeeded());
        assertFalse(t.failed());
        TaskResult t2 = new TaskResult(TaskResult.Result.FAILED);
        assertTrue(t2.failed());
        assertFalse(t2.succeeded());
    }
}