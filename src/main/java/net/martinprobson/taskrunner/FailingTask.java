package net.martinprobson.taskrunner;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

class FailingTask extends DependentTask {

    private static final Logger log = LoggerFactory.getLogger(FailingTask.class);
    private TaskResult result;

    public FailingTask(String id, String taskConfiguration) {
        super(taskConfiguration);
        setId(id);
    }

    public TaskResult execute() {
        log.trace("About to execute with FAILURE: " + this.getId());
        return new TaskResult(TaskResult.Result.FAILURE);
    }

}
