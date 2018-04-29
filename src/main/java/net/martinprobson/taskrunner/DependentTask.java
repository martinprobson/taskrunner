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

import com.github.dexecutor.core.task.ExecutionResult;
import com.github.dexecutor.core.task.ExecutionResults;
import net.martinprobson.taskrunner.configurationservice.ConfigurationService;

import java.util.ArrayList;
import java.util.List;

/**
 * <p><code>DependentTask</code></p>
 *
 * <p>A <code>DependentTask</code> is a task that has zero or more dependencies. </p>
 *
 * @author martinr
 */
public abstract class DependentTask extends ConfiguredTask<String, String> {

    /**
     * The configuration key used to lookup task dependencies.
     */
    private static final String TASK_DEPENDENCY_KEY = ConfigurationService.getConfiguration().getString("dependency-key");

    /**
     * Construct a {@code DependentTask} with the given task specific configuration.
     *
     * @param id                The id of the task.
     * @param task              The task contents.
     */
    protected DependentTask(String id, String task) {
        super(id,task);
    }

    /**
     * Construct a {@code DependentTask} with the given task specific configuration.
     *
     * @param id                The id of the task.
     * @param task              The task contents.
     * @param taskConfiguration The task specific configuration.
     */
    protected DependentTask(String id, String task,String taskConfiguration) {
        super(id,task,taskConfiguration);
    }

    List<String> getDependencies() {
        return getConfiguration().getList(String.class, TASK_DEPENDENCY_KEY, new ArrayList<>());
    }

    /**
     * {@code shouldExecute} Is this task executable?
     *
     * <p>This method is called to determine if the task can execute. A task is considered executable
     * if all its parent dependencies are not in a failed state.</p>
     *
     * @param parentResults List of parent {@code TaskResults}, keyed by {@code taskid}
     * @return {@code true} if the task can execute, {@code false otherwise}
     */
    public boolean shouldExecute(ExecutionResults<String, TaskResult> parentResults) {
        for (ExecutionResult<String, TaskResult> result : parentResults.getAll()) {
            if (!result.isSuccess())
                return false;
        }
        return true;
    }

}
