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

/**
 * A TaskResult holds the results of a Task execution.
 *
 * @author martinr
 */
public class TaskResult {
    public final Throwable exception;
    private final Result result;
    private final String message;

    /**
     * Construct an empty TaskResult.
     */
    public TaskResult() {
        this(Result.NOT_EXECUTED,"",null);
    }

    /**
     * Construct a TaskResult with a result, message and exception.
     * @param result    The {@code Result} of the Task.
     * @param message   Message returned.
     * @param t         Exception returned by the task.
     */
    private TaskResult(Result result, String message, Throwable t) {
        this.result = result;
        this.message = message;
        this.exception = t;
    }

    /**
     * Construct a TaskResult with a result and exception.
     * @param result    The {@code Result} of the Task.
     * @param t         Exception returned by the task.
     */
    public TaskResult(Result result, Throwable t) {
        this(result, "", t);
    }

    /**
     * Construct a TaskResult with a result and exception.
     * @param result    The {@code Result} of the Task.
     */
    public TaskResult(Result result) {
        this(result, "", null);
    }

    /**
     * Has this task succeeded?
     * @return <code>true</code> if task succeeded, <code>false</code> otherwise.
     */
    public Boolean succeeded() {
        return result == Result.SUCCESS;
    }

    /**
     * Has this task failed?
     * @return <code>true</code> if task failed, <code>false</code> otherwise.
     */
    public Boolean failed() {
        return result == Result.FAILED;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("[ ");
        sb.append(result).append(" ");
        if (message != null)
            sb.append(message).append(" ");
        if (exception != null)
            sb.append(exception).append(" ").append(exception.getCause());
        sb.append("]");
        return sb.toString();
    }

    /**
     *
     * @return The result held by this TaskResult.
     */
    public Result getResult() {
        return result;
    }

    public enum Result {
        SUCCESS, RUNNING, FAILED, NOT_EXECUTED
    }

}
