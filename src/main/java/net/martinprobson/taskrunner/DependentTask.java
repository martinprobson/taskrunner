package net.martinprobson.taskrunner;

import com.github.dexecutor.core.task.ExecutionResult;
import com.github.dexecutor.core.task.ExecutionResults;
import net.martinprobson.taskrunner.configurationservice.ConfigurationService;

import java.util.ArrayList;
import java.util.List;

/**
 * A dependent Task is a task that has zero or more dependencies.
 * The TaskId is a String.
 */
public abstract class DependentTask extends ConfiguredTask<String, TaskResult> {

    private final String TASK_DEPENDENCY_KEY = ConfigurationService.getConfiguration().getString("dependency-key");

    protected DependentTask(String taskConfiguration) {
        super(taskConfiguration);
    }

    List<String> getDependencies() {
        return getConfiguration().getList(String.class, TASK_DEPENDENCY_KEY, new ArrayList<>());
    }

    public boolean shouldExecute(ExecutionResults<String, TaskResult> parentResults) {
        for (ExecutionResult<String, TaskResult> result : parentResults.getAll()) {
            if (result.getResult().failed())
                return false;
        }
        return true;
    }
}
