package net.martinprobson.taskrunner;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DummyTask extends DependentTask {

    private static final Logger log = LoggerFactory.getLogger(DummyTask.class);

    public DummyTask(String id, String taskConfiguration) {
        super(taskConfiguration);
        setId(id);
    }

    public TaskResult execute() {
        log.trace("About to execute: " + this.getId());
        return new TaskResult(TaskResult.Result.SUCCESS);
    }

}
