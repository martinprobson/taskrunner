package net.martinprobson.jobrunner;

public class TaskProviderMappingException extends Exception {

    public TaskProviderMappingException(final String msg) {
        super(msg);
    }

    public TaskProviderMappingException(String msg, Throwable cause) {
        super(msg, cause);
    }

}
