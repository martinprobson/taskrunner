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

package net.martinprobson.taskrunner.jdbctask;

import com.github.dexecutor.core.task.TaskExecutionException;
import net.martinprobson.taskrunner.DependentTask;
import net.martinprobson.taskrunner.TaskExecutor;
import net.martinprobson.taskrunner.TaskResult;
import net.martinprobson.taskrunner.TaskRunnerException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * <p>{@code JDBCTask}</p>
 *
 * <p>A task that hold SQL that will be executed against a JDBC connection.</p>
 *
 * @author martinr
 */
public class JDBCTask extends DependentTask {

    private static final Logger log = LoggerFactory.getLogger(JDBCTask.class);
    private static TaskExecutor taskExecutor = null;

    /**
     *
     * Creates a JDBCTask with the specified id,sql and task configuration.
     *
     * @param taskId             Task Id
     * @param sql                Sql contents
     * @param taskConfiguration  Task specification configuration XML filename.
     */
    public JDBCTask(String taskId, String sql, String taskConfiguration) {
        super(taskId, sql, taskConfiguration);
    }

    /**
     * Creates a JDBCTask with the specified id and sql contents.
     *
     * @param taskId  Task id
     * @param sql     Sql contents
     *
     */
    public JDBCTask(String taskId, String sql) {
        super(taskId, sql);
    }


    private static TaskExecutor getTaskExecutor() {
        if (taskExecutor == null)
            taskExecutor = new JDBCTaskExecutor();
        return taskExecutor;
    }

    /**
     * @return The sql that this task holds.
     */
    public String getSql() {
        return getTask();
    }

    /**
     * <p>{@code execute}</p>
     *
     * <p>Executes the SQL via JDBC.</p>
     *
     * @return set to {@code SUCCESSFUL} if execution successful, {@code FAILED} if error occurs.
     * @throws TaskExecutionException thrown when a execution problem is encountered.
     */
    public TaskResult execute() throws TaskExecutionException {
        log.trace("About to execute task id: " + this.getId());
        try {
            getTaskExecutor().executeTask(this);
        } catch (TaskRunnerException e) {
            setTaskResult(new TaskResult(TaskResult.Result.FAILED,e));
            throw new TaskExecutionException("Task: " + getId() + " failed with " + e.getMessage(),e);
        }
        if (getTaskResult().failed())
            log.error("Task: " + this.getId() + " Result: " + getTaskResult());
        else
            log.trace("Task: " + this.getId() + " Result: " + getTaskResult());
        return getTaskResult();
    }
}
