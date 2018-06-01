package net.martinprobson.jobrunner.common;

import net.martinprobson.jobrunner.TaskResult;

/**
 * A TaskExecutor can execute a passed Task.
 *
 * @author martinr
 */
public interface TaskExecutor {
    TaskResult executeTask(BaseTask task) throws JobRunnerException;
}
