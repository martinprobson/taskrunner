package net.martinprobson.taskrunner;

import com.github.dexecutor.core.task.Task;
import com.github.dexecutor.core.task.TaskProvider;

import java.util.Iterator;
import java.util.Map;

class TaskGroup implements Iterable<DependentTask>, TaskProvider<String, TaskResult> {
    private final Map<String, DependentTask> tasks;

    public TaskGroup(TaskBuilder taskBuilder) {
        tasks = taskBuilder.build();
    }

    public Boolean hasId(String id) {
        return tasks.containsKey(id);
    }

    public Task<String, TaskResult> provideTask(String key) {
        return tasks.get(key);
    }

    @Override
    public Iterator<DependentTask> iterator() {
        return tasks.values().iterator();
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (Task task : this) {
            sb.append("id: ").append(task.getId()).append("\n");
        }
        return sb.toString();
    }

}
