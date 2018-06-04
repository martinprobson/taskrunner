package net.martinprobson.jobrunner;

import static com.google.common.base.MoreObjects.toStringHelper;

/**
 * <p>
 * A TaskResult holds the results of a Task execution.
 * A TaskResult is immutable.
 * </p><p>
 *
 * {@code TaskResult} must be constructed via a {@link Builder}
 * </p>
 * @author martinr
 */
final public class TaskResult {
    /** Any exception thrown during execution of the task */
    private final Throwable exception;

    /** The result {@code TaskResult.Result} of executing the task */
    private final Result result;

    /** Exit value from external command (if relevant). */
    private final int exitValue;

    /** Contents of stderr of task output (if relevant). */
    private final String error;

    /** Contents of stdout of task output (if relevant). */
    private final String output;

    /** The command line executed if external command */
    private final String procString;

    /**
     * <p>
     * A TasKResult Builder.
     * </p>
     */
    public static class Builder {

        public Builder(TaskResult.Result result) {
            this.result = result;
        }

        public Builder exception(Throwable exception) {
            this.exception = exception;
            return this;
        }
        public Builder exitValue(int exitValue) {
            this.exitValue = exitValue;
            return this;
        }
        public Builder error(String error) {
            this.error = error;
            return this;
        }
        public Builder output(String output) {
            this.output = output;
            return this;
        }
        public Builder procString(String procString) {
            this.procString = procString;
            return this;
        }
        public TaskResult build() {
            return new TaskResult(this);
        }

        private final Result result;
        private Throwable exception;
        private int exitValue;
        private String error;
        private String output;
        private String procString;
    }

    private TaskResult(Builder builder) {
        this.exception = builder.exception;
        this.result = builder.result;
        this.exitValue = builder.exitValue;
        this.error = builder.error;
        this.output = builder.output;
        this.procString = builder.procString;
    }

    @Override
    public String toString() {
        return toStringHelper(this)
                .add("exception", exception)
                .add("result", result)
                .add("exitValue", exitValue)
                .add("error", error)
                .add("output", output)
                .add("procString", procString)
                .toString();
    }

    public Throwable getException() {
        return exception;
    }

    public int getExitValue() {
        return exitValue;
    }

    public String getError() {
        return error;
    }

    public String getOutput() {
        return output;
    }

    public String getProcString() {
        return procString;
    }

    public Result getResult() {
        return result;
    }

    /**
     * Has this task succeeded?
     * @return <code>true</code> if task succeeded, <code>false</code> otherwise.
     */
    public Boolean succeeded() {
        return result == Result.SUCCESS;
    }

    /**
     * Has this task failed?
     * @return <code>true</code> if task failed, <code>false</code> otherwise.
     */
    public Boolean failed() {
        return result == Result.FAILED;
    }

    public enum Result {
        SUCCESS, RUNNING, FAILED, NOT_EXECUTED
    }

}
