package net.martinprobson.taskrunner;

import com.github.dexecutor.core.DexecutorConfig;

import java.util.concurrent.ExecutorService;

public class TaskRunnerConfig extends DexecutorConfig<String, TaskResult> {

    private final TaskGroup taskGroup;

    public TaskRunnerConfig(final ExecutorService executorService, final TaskGroup taskGroup) {
        super(executorService, taskGroup);
        this.taskGroup = taskGroup;
    }

    public TaskGroup getTaskGroup() {
        return taskGroup;
    }
}
