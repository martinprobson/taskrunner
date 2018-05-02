package net.martinprobson.jobrunner;

public class JobRunnerException extends Exception {

    public JobRunnerException(final String msg) {
        super(msg);
    }

    public JobRunnerException(String msg, Throwable cause) {
        super(msg, cause);
    }

}
