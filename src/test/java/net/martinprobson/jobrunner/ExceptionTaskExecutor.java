package net.martinprobson.jobrunner;

import net.martinprobson.jobrunner.common.BaseTask;
import net.martinprobson.jobrunner.common.JobRunnerException;
import net.martinprobson.jobrunner.common.TaskExecutor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * <h3>{@code ExceptionTaskExecutor}</h3>
 * <p>A {@code ExceptionTaskExecutor} always returns FAIL when executed.</p>
 */
public class ExceptionTaskExecutor implements TaskExecutor {

    @Override
    public TaskResult executeTask(BaseTask task) throws JobRunnerException  {
        log.trace("executeTask - " + task.getId());
        task.setTaskResult(new TaskResult.Builder(TaskResult.Result.FAILED).build());
        throw new JobRunnerException("Failed");
    }

    private static final Logger log = LoggerFactory.getLogger(ExceptionTaskExecutor.class);

}
