package net.martinprobson.taskrunner.main;

import com.google.inject.Inject;
import net.martinprobson.taskrunner.BaseTask;
import net.martinprobson.taskrunner.TaskBuilder;
import net.martinprobson.taskrunner.TaskFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

/**
 * ManualTaskBuilder - Build a list of tasks manually.
 */
class ManualTaskBuilder implements TaskBuilder {


    private TaskFactory taskFactory;

    @Inject
    public ManualTaskBuilder(TaskFactory taskFactory) {
        this.taskFactory = taskFactory;
    }

    public Map<String, BaseTask> build() {
        HashMap<String, BaseTask> tasks = new HashMap<>();
        String taskNames[] = {"drop_table1", "drop_table2", "create_table1", "create_table2",
                "insert1", "insert2", "create_table3"};
        for (String task : taskNames) {
            BaseTask t;
            //@TODO FIx
            t = taskFactory.createDummyTask(task + ".hql",TaskBuilder.getConfig(task + ".xml"));
            tasks.put(t.getId(), t);
        }

        return tasks;
    }

    /**
     * Logger
     */
    private static final Logger log = LoggerFactory.getLogger(ManualTaskBuilder.class);
}
