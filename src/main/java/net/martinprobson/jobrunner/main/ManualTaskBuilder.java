package net.martinprobson.jobrunner.main;

import net.martinprobson.jobrunner.*;
import net.martinprobson.jobrunner.common.BaseTask;
import net.martinprobson.jobrunner.common.JobRunnerException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

/**
 * ManualTaskBuilder - Build a list of tasks manually.
 */
class ManualTaskBuilder implements TaskBuilder {

    public Map<String, BaseTask> build() throws JobRunnerException {
        TaskProvider taskProvider = TaskProvider.getInstance();
        HashMap<String, BaseTask> tasks = new HashMap<>();
        String taskNames[] = {"drop_table1", "drop_table2", "create_table1", "create_table2",
                "insert1", "insert2", "create_table3"};
        for (String task : taskNames) {
            BaseTask t;
            //@TODO FIx
            t = taskProvider.createTask("dummy",task + ".hql","",TaskBuilder.getConfig(task + ".xml"));
            tasks.put(t.getId(), t);
        }

        return tasks;
    }


    /**
     * Logger
     */
    private static final Logger log = LoggerFactory.getLogger(ManualTaskBuilder.class);
}
