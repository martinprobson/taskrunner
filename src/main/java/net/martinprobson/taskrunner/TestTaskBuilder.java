package net.martinprobson.taskrunner;

import net.martinprobson.taskrunner.jdbctask.JDBCTask;

import java.util.HashMap;
import java.util.Map;

/**
 * ManualTaskBuilder - Build a list of tasks manually.
 */
class TestTaskBuilder extends TaskBuilder {

    public Map<String, DependentTask> build() {
        HashMap<String, DependentTask> tasks = new HashMap<>();
        JDBCTask task = new JDBCTask("drop_table1.hql", "DROP TABLE IF EXISTS test1 ;", "drop_table1.xml");
        tasks.put(task.getId(), task);

        return tasks;
    }
}
