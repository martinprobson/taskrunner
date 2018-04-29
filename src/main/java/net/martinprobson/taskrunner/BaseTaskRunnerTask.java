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

import java.util.Objects;

/**
 * <p>{@code BaseTaskRunnerTask}</p>
 *
 * <p>The base class for all {@code TaskRunner} tasks.</p>
 * <p>Holds a task id, task contents and a {@link TaskResult} object.</p>
 *
 * @param <C> The type of task.
 * @param <T> The type of the task id.
 *
 * @author martinr
 */
public abstract class BaseTaskRunnerTask<C, T> extends Task<T, TaskResult> {

    /**
     * The task id
     */
    private final T taskId;

    /**
     * The task contents
     */
    private final C task;

    /**
     * The TaskResult
     */
    private TaskResult result;

    /**
     * Construct a new Task with the given id and contents.
     * @param id    task id.
     * @param task  task contents.
     */
    BaseTaskRunnerTask(T id, C task) {
        super.setId(id);
        this.taskId = id;
        this.task = task;
        this.result = new TaskResult();
    }

    @Override
    public String toString() {
        return "BaseTaskRunnerTask{id = " + taskId +
                ",task='" + task + '\'' +
                ",result=" + result + "}";
    }

    /**
     * @return the task id.
     */
    @Override
    public T getId() {
        return taskId;
    }

    /**
     * @return the task contents.
     */
    protected C getTask() {
        return task;
    }

    /**
     *
     * @return - The {@code TaskResult}
     */
    public TaskResult getTaskResult() { return result; }

    /**
     * Set the {@code TaskResult} for this task.
     * @param t - TaskResult
     */
    public void setTaskResult(TaskResult t) {
        result = t;
    }

    /**
     * Tasks are considered equal if they have the same task id, task contents.
     *
     * @param other the other object to compare.
     * @return {@code true} if tasks match on {@code task, taskId and result}, {@code false}</code>
     * otherwise.
     */
    @Override
    public boolean equals(Object other) {
        if (other == null) {
            return false;
        }
        if (other == this) {
            return true;
        }
        if (other.getClass() != getClass()) {
            return false;
        }
        BaseTaskRunnerTask o = (BaseTaskRunnerTask) other;
        return Objects.equals(taskId, o.getId()) &&
                Objects.equals(task, o.getTask());
    }

    @Override
    public int hashCode() {
        return Objects.hash(taskId, task);
    }
}
