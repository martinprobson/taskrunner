package net.martinprobson.taskrunner;

public class TaskResult {
    public final Throwable t;
    private final Result result;
    private final String message;

    public TaskResult(Result result, String message, Throwable t) {
        this.result = result;
        this.message = message;
        this.t = t;
    }

    public TaskResult(Result result, Throwable t) {
        this(result, "", t);
    }

    public TaskResult(Result result) {
        this(result, "", null);
    }

    public Boolean succeeded() {
        return result == Result.SUCCESS;
    }

    public Boolean failed() {
        return result == Result.FAILURE;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("[ ");
        sb.append(result).append(" ");
        if (message != null)
            sb.append(message).append(" ");
        if (t != null)
            sb.append(t).append(" ");
        sb.append("]");
        return sb.toString();
    }

    public enum Result {
        SUCCESS, FAILURE
    }

}
