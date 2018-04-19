package net.martinprobson.taskrunner;

import com.github.dexecutor.core.DexecutorConfig;

import java.util.concurrent.ExecutorService;

class TaskRunnerConfig extends DexecutorConfig<String, TaskResult> {

    private final TaskGroup taskGroup;

    TaskRunnerConfig(final ExecutorService executorService, final TaskGroup taskGroup) {
        super(executorService, taskGroup);
        this.taskGroup = taskGroup;
    }

    TaskGroup getTaskGroup() {
        return taskGroup;
    }
}
