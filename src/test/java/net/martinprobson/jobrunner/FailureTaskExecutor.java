package net.martinprobson.jobrunner;

import net.martinprobson.jobrunner.common.BaseTask;
import net.martinprobson.jobrunner.common.TaskExecutor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * <h3>{@code FailureTaskExecutor}</h3>
 * <p>A {@code FailureTaskExecutor} always returns FAIL when executed.</p>
 */
public class FailureTaskExecutor implements TaskExecutor {

    @Override
    public TaskResult executeTask(BaseTask task)  {
        log.trace("DummyTaskExecutor - executeTask - " + task.getId());
        return task.setTaskResult(new TaskResult.Builder(TaskResult.Result.FAILED).build());
    }

    private static final Logger log = LoggerFactory.getLogger(net.martinprobson.jobrunner.FailureTaskExecutor.class);

}
