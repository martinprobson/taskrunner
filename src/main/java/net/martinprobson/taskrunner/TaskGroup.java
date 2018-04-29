/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package net.martinprobson.taskrunner;

import com.github.dexecutor.core.task.Task;
import com.github.dexecutor.core.task.TaskProvider;

import java.util.Iterator;
import java.util.Map;

/**
 * A {@code TaskGroup} holds a group of tasks that are related and may have dependencies on
 * each other.
 * The constructor is passed a {@link TaskBuilder} that supplies the list of Tasks for the group.
 * The {@code provideTask} method is called by the execution framework when the task needs to be
 * executed.
 *
 * @author martinr
 */
public class TaskGroup implements Iterable<DependentTask>, TaskProvider<String, TaskResult> {
    private final Map<String, DependentTask> tasks;

    /**
     * Construct a new TaskGroup with the passed TaskBuilder.
     * @param taskBuilder TaskBuilder instance responsible for providing collection of Tasks.
     * @throws TaskRunnerException
     */
    public TaskGroup(TaskBuilder taskBuilder) throws TaskRunnerException {
        tasks = taskBuilder.build();
    }

    /**
     * Does this TaskGroup contain a Task with the given id?
     * @param id Task id
     * @return <code>true</code> if task id exists in this group, <code>false</code> otherwise.
     */
    public Boolean hasId(String id) {
        return tasks.containsKey(id);
    }

    /**
     * Provide the Task to the execution framework when asked.
     * @param key - Task id.
     * @return Task, or null if the Task does not exist in this group.
     */
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

    /**
     *
     * @return No. of Tasks in this {@code TaskGroup}
     */
    public int size() {
        return tasks.size();
    }

}
