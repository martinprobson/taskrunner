package net.martinprobson.taskrunner.jdbctask;

import net.martinprobson.taskrunner.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class JDBCTask extends DependentTask {

    private static final Logger log = LoggerFactory.getLogger(DummyTask.class);
    private static TaskExecutor taskExecutor = null;
    private final String hql;

    public JDBCTask(String taskId, String hql, String taskConfiguration) {
        super(taskConfiguration);
        setId(taskId);
        this.hql = hql;
    }

    private static TaskExecutor getTaskExecutor() {
        if (taskExecutor == null)
            taskExecutor = new JDBCTaskExecutor();
        return taskExecutor;
    }

    public String getHql() {
        return hql;
    }

    public TaskResult execute() {
        log.trace("About to execute task: " + this.getId());
        TaskResult result;
        try {
            result = getTaskExecutor().executeTask(this);
        } catch (TaskRunnerException e) {
            result = new TaskResult(TaskResult.Result.FAILURE, e);
        }
        log.trace("Task: " + this.getId() + " Result: " + result);
        return result;
    }

}
