package net.martinprobson.taskrunner;

public class TaskRunnerException extends Exception {

    public TaskRunnerException(final String msg) {
        super(msg);
    }

    public TaskRunnerException(String msg, Throwable cause) {
        super(msg, cause);
    }

}
