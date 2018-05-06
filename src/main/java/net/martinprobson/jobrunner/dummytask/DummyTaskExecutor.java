package net.martinprobson.jobrunner.dummytask;

import net.martinprobson.jobrunner.common.BaseTask;
import net.martinprobson.jobrunner.common.TaskExecutor;
import net.martinprobson.jobrunner.TaskResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * <h3>{@code DummyTaskExecutor}</h3>
 * <p>A {@code DummyTaskExecutor} always returns SUCCESS when executed.</p>
 */
class DummyTaskExecutor implements TaskExecutor {

    @Override
    public void executeTask(BaseTask task)  {
        log.trace("DummyTaskExecutor - executeTask - " + task.getId());
        task.setTaskResult(new TaskResult(TaskResult.Result.SUCCESS));
    }

    private static final Logger log = LoggerFactory.getLogger(DummyTaskExecutor.class);

}
