package net.martinprobson.jobrunner;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * <h3>{@code DummyTaskExecutor}</h3>
 * <p>A {@code DummyTaskExecutor} always returns SUCCESS when executed.</p>
 */
public class DummyTaskExecutor implements TaskExecutor {

    @Override
    public void executeTask(BaseTask task) throws JobRunnerException {
        log.trace("DummyTaskExecutor - executeTask - " + task.getId());
        task.setTaskResult(new TaskResult(TaskResult.Result.SUCCESS));
    }

    private static final Logger log = LoggerFactory.getLogger(DummyTaskExecutor.class);

}
