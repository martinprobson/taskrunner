package net.martinprobson.taskrunner;

import com.github.dexecutor.core.task.Task;

public abstract class TaskExecutor {
    public abstract TaskResult executeTask(Task task) throws TaskRunnerException;
}
