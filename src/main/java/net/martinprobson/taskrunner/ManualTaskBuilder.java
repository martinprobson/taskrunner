package net.martinprobson.taskrunner;

import java.util.HashMap;
import java.util.Map;

/**
 * ManualTaskBuilder - Build a list of tasks manually.
 */
class ManualTaskBuilder extends TaskBuilder {

    public Map<String, DependentTask> build() {
        HashMap<String, DependentTask> tasks = new HashMap<>();
        String taskNames[] = {"drop_table1", "drop_table2", "create_table1", "create_table2",
                "insert1", "insert2", "create_table3"};
        for (String task : taskNames) {
            DependentTask t;
            if (task.equals("insert1"))
                t = new DummyTask(task + ".hql", task + ".xml");
            else
                t = new DummyTask(task + ".hql", task + ".xml");
            tasks.put(t.getId(), t);
        }

        return tasks;
    }
}
