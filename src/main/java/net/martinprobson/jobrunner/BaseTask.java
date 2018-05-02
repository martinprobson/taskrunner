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

import com.github.dexecutor.core.task.ExecutionResult;
import com.github.dexecutor.core.task.ExecutionResults;
import com.github.dexecutor.core.task.Task;
import com.github.dexecutor.core.task.TaskExecutionException;
import net.martinprobson.jobrunner.configurationservice.ConfigurationService;
import net.martinprobson.jobrunner.template.TemplateException;
import net.martinprobson.jobrunner.template.TemplateService;
import org.apache.commons.configuration2.CombinedConfiguration;
import org.apache.commons.configuration2.Configuration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * <h3><p>{@code BaseTask}</p></h3>
 *
 * <p>The base class for all {@code JobRunner} tasks.</p><p></p>
 * <p>A Task consists of : -</p>
 * <ol><li>A <code>Task id</code> - String (that uniquely identifies a task within a task group).</li>
 * <li>The task contents (for example SQL code) held as a String.</li>
 * <li>An optional, Task specific configuration, ( as defined by {@link org.apache.commons.configuration2.Configuration}). Either
 * supplied via a <code>Configuration</code> class or loaded from the file system as an XML configuration file. The task specific
 * configuration can contain task dependency information and/or template parameters.
 * </li>
 * </ol>
 * <p></p>
 * <p>Internally this class also maintains references to the following service objects: -</p>
 * <ol><li>A template service provider, that is called to apply template fields against the task contents.</li>
 * <li>An execution service reponsible for actually executing the task (for example execute SQL against a JDBC connection).</li>
 * <li>A {@link TaskResult} holding the execution result of the task.</li></ol>
 * <p>Sub-classes are expected to supply a specific execution service for the type of task they are defining.</p>
 *
 *
 * @author martinr
 */
public abstract class BaseTask extends Task<String, TaskResult> {

    /**
     * The task id
     */
    private final String taskId;

    /**
     * The task contents
     */
    private final String task;

    /**
     * The TaskResult
     */
    private TaskResult result;

    /**
     * The task configuration.
     */
    private final CombinedConfiguration configuration;
    /**
     * Template service provider.
     */
    private final TemplateService templateService;
    /**
     * ExecutionService.
     */
    private final TaskExecutor taskExecutor;

    /**
     * Construct a new Task with the given id and contents.
     * @param id    task id.
     * @param task  task contents.
     * @param templateService The template service provider.
     * @param taskExecutor The task executor service responsible for executing tasks of this type.
     */
    protected BaseTask(String id, String task, TemplateService templateService, TaskExecutor taskExecutor) {
        super.setId(id);
        this.taskId = id;
        this.task = task;
        this.configuration = new CombinedConfiguration();
        this.configuration.addConfiguration(ConfigurationService.getConfiguration(), "global");
        this.templateService = templateService;
        this.taskExecutor = taskExecutor;
        this.result = new TaskResult();
    }

    /**
     * Construct a new Task with the given id and contents.
     * @param id    task id.
     * @param task  task contents.
     * @param taskConfig The task specific configuration.
     * @param templateService The template service provider.
     * @param taskExecutor The task executor service responsible for executing tasks of this type.
     */
    protected BaseTask(String id, String task, Configuration taskConfig, TemplateService templateService, TaskExecutor taskExecutor) {
        this(id,task,templateService,taskExecutor);
        this.configuration.addConfiguration(taskConfig, "task");
    }

    /**
     * Construct a new Task with the given id and contents.
     * @param id    task id.
     * @param task  task contents.
     * @param taskConfig Full path of XML file from which task specific configuration will be loaded.
     * @param templateService The template service provider.
     * @param taskExecutor The task executor service responsible for executing tasks of this type.
     */
    //@TODO Get rid of this constructor and dependancy on JobRunnerConfigurationProvider
    protected BaseTask(String id, String task, String taskConfig, TemplateService templateService, TaskExecutor taskExecutor) {
        this(id,task,templateService,taskExecutor);
        this.configuration.addConfiguration(JobRunnerConfigurationProvider.getXmlConfig(taskConfig), "task");
    }

    @Override
    public String toString() {
        return "BaseTask{id = " + taskId +
                ",task='" + task + '\'' +
                ",result=" + result + "}";
    }

    /**
     * @return the task id.
     */
    @Override
    public String getId() {
        return taskId;
    }

    /**
     * @return the task contents.
     */
    public String getTask() {
        return task;
    }

    /**
     * @return the task contents after template has been applied
     */
    public String getTemplatedTask() throws JobRunnerException {
        try {
            return templateService.apply(getId(),getTask(),getConfiguration());
        } catch (TemplateException e) {
            throw new JobRunnerException("Template error",e);
        }
    }


    /**
     *
     * @return - The {@code TaskResult}
     */
    public TaskResult getTaskResult() { return result; }

    /**
     * Return the (Combined) Configuration for this task.
     *
     * @return Configuration {@link org.apache.commons.configuration2.CombinedConfiguration}
     */
    public Configuration getConfiguration() {
        return configuration;
    }

    /**
     * Set the {@code TaskResult} for this task.
     * @param t - TaskResult
     */
    public void setTaskResult(TaskResult t) {
        result = t;
    }

    /**
     * Returns the list of {@code TaskIds} that this Task depends on. All of the Tasks in this
     * list must have a successful status in order for this Task to run.
     * @return The list of {@code taskIds} that this Task is dependent on. List returned can be empty.
     */
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
    /**
     * <h3>{@code execute}</h3>
     *
     * <p>Executes this task via a {@codeTaskExecutor}.</p>
     *
     * @return set to {@code SUCCESSFUL} if execution successful, {@code FAILED} if error occurs.
     * @throws TaskExecutionException thrown when a execution problem is encountered.
     */
    public TaskResult execute() throws TaskExecutionException {
        log.trace("About to execute task id: " + this.getId());
        setTaskResult(new TaskResult(TaskResult.Result.RUNNING));
        //randomDelay(3,10);
        //@TODO Set status to running.....
        try {
            taskExecutor.executeTask(this);
        } catch (JobRunnerException e) {
            setTaskResult(new TaskResult(TaskResult.Result.FAILED,e));
            throw new TaskExecutionException("Task: " + getId() + " failed with " + e.getMessage(),e);
        }
        if (getTaskResult().failed())
            log.error("Task: " + this.getId() + " Result: " + getTaskResult());
        else
            log.trace("Task: " + this.getId() + " Result: " + getTaskResult());
        return getTaskResult();
    }

    /**
     * Tasks are considered equal if they have the same task id, and task contents.
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
        BaseTask o = (BaseTask) other;
        return Objects.equals(taskId, o.getId()) &&
                Objects.equals(task, o.getTask());
    }

    @Override
    public int hashCode() {
        return Objects.hash(taskId, task);
    }

    /**
     * The configuration key used to lookup task dependencies.
     */
    private static final String TASK_DEPENDENCY_KEY = ConfigurationService.getConfiguration().getString("dependency-key");
    /**
     * Logger
     */
    private static final Logger log = LoggerFactory.getLogger(DummyTask.class);

    private static void randomDelay(float min, float max){
        int random = (int)(max * Math.random() + min);
        try {
            Thread.sleep(random * 1000);
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
}
