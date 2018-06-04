package net.martinprobson.jobrunner;

import org.junit.Test;

import static org.junit.Assert.*;

public class TaskProviderMappingExceptionTest {
    @Test
    public void TaskProviderMappingExceptionTest1() {
        TaskProviderMappingException e = new TaskProviderMappingException("Test");
        assertEquals("Test",e.getMessage());
    }

    @Test
    public void TaskProviderMappingExceptionTest2() {
        Exception ex = new Exception("Nested Exception");
        TaskProviderMappingException e = new TaskProviderMappingException("Test",ex);
        assertEquals("Test",e.getMessage());
        assertEquals("Nested Exception",e.getCause().getMessage());
    }
}