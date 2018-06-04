package net.martinprobson.jobrunner;

import com.github.dexecutor.core.task.TaskProvider;
import net.martinprobson.jobrunner.common.BaseTask;
import net.martinprobson.jobrunner.common.JobRunnerException;

import java.util.Iterator;
import java.util.Map;

/**
 * A {@code Job} holds a group of tasks that are related and may have dependencies on
 * each other.
 * The constructor is passed a {@link TaskBuilder} that supplies the list of Tasks for the group.
 * The {@code provideTask} method is called by the execution framework when the task needs to be
 * executed.
 *
 * @author martinr
 */
public class Job implements Iterable<BaseTask>, TaskProvider<String, TaskResult> {

    private final Map<String, BaseTask> tasks;

    /**
     * Construct a new Job with the passed TaskBuilder.
     *
     * @param taskBuilder TaskBuilder instance responsible for providing collection of Tasks.
     * @throws JobRunnerException If error occurs when building the tasks.
     */
    public Job(TaskBuilder taskBuilder) throws JobRunnerException {
        tasks = taskBuilder.build();
    }

    /**
     * Does this Job contain a Task with the given id?
     *
     * @param id Task id
     * @return <code>true</code> if task id exists in this group, <code>false</code> otherwise.
     */
    public Boolean hasId(String id) {
        return tasks.containsKey(id);
    }

    /**
     * Get the task with the corresponding id.
     *
     * @param id Task id
     * @return BaseTask or null if not found.
     */
    public BaseTask getId(String id) {
        return tasks.get(id);
    }

    /**
     * Provide the Task to the execution framework when asked.
     *
     * @param key - Task id.
     * @return Task, or null if the Task does not exist in this group.
     */
    @Override
    public BaseTask provideTask(String key) {
        return tasks.get(key);
    }

    @SuppressWarnings("NullableProblems")
    @Override
    public Iterator<BaseTask> iterator() {
        return tasks.values().iterator();
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("\n");
        for (BaseTask task : this) {
            sb.append("id: [")
                    .append(task.getId())
                    .append("]\n\t Task Status: [")
                    .append(task.getTaskResult())
                    .append("]\n");
        }
        return sb.toString();
    }

    public String status() {
        StringBuilder sb = new StringBuilder("\n");
        for (BaseTask task : this) {
            sb.append("id: [")
                    .append(task.getId())
                    .append("] Status: [")
                    .append(task.getTaskResult().getResult())
                    .append("]\n");
        }
        return sb.toString();
    }

    /**
     * @return No. of Tasks in this {@code Job}
     */
    public int size() {
        return tasks.size();
    }
}
