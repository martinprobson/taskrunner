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
     * @param taskBuilder TaskBuilder instance responsible for providing collection of Tasks.
     * @throws JobRunnerException
     */
    public Job(TaskBuilder taskBuilder) throws JobRunnerException {
        tasks = taskBuilder.build();
    }

    /**
     * Does this Job contain a Task with the given id?
     * @param id Task id
     * @return <code>true</code> if task id exists in this group, <code>false</code> otherwise.
     */
    public Boolean hasId(String id) {
        return tasks.containsKey(id);
    }

    /**
     * Get the task with the corresponding id.
     * @param id Task id
     * @return BaseTask or null if not found.
     */
    public BaseTask getId(String id) {
        return tasks.get(id);
    }

    /**
     * Provide the Task to the execution framework when asked.
     * @param key - Task id.
     * @return Task, or null if the Task does not exist in this group.
     */
    @Override
    public BaseTask provideTask(String key) {
//    public BaseTask<String, TaskResult> provideTask(String key) {
        return tasks.get(key);
    }

    @Override
    public Iterator<BaseTask> iterator() {
        return tasks.values().iterator();
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (BaseTask task : this) {
            sb.append("id: [")
                    .append(task.getId())
                    .append("] Task Status: [")
                    .append(task.getTaskResult().getResult())
                    .append("]\n");
        }
        return sb.toString();
    }

    /**
     *
     * @return No. of Tasks in this {@code Job}
     */
    public int size() {
        return tasks.size();
    }

}
