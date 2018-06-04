
package net.martinprobson.jobrunner.common;

import com.github.dexecutor.core.task.ExecutionResult;
import com.github.dexecutor.core.task.ExecutionResults;
import com.github.dexecutor.core.task.Task;
import com.github.dexecutor.core.task.TaskExecutionException;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import net.martinprobson.jobrunner.TaskResult;
import net.martinprobson.jobrunner.configurationservice.GlobalConfigurationProvider;
import net.martinprobson.jobrunner.template.TemplateException;
import net.martinprobson.jobrunner.template.TemplateService;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * <h3><p>{@code BaseTask}</p></h3>
 *
 * <p>The base class for all {@code JobRunner} tasks.</p><p></p>
 * <p>A Task consists of : -</p>
 * <ol><li>A <code>Task id</code> - String (that uniquely identifies a task within a task group).</li>
 * <li>A taskFile - The name of the file containing the task contents.</li>
 * <li>An optional, Task specific config. Either
 * supplied via a <code>Config</code> class or loaded from the file system as a config file. The task specific
 * config can contain task dependency information and/or template parameters.
 * </li>
 * </ol>
 * <p></p>
 * <p>Internally this class also maintains references to the following service objects: -</p>
 * <ol><li>A template service provider, that is called to apply template fields against the task contents.</li>
 * <li>An execution service responsible for actually executing the task (for example execute SQL against a JDBC connection).</li>
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
     * The name of the file containing the task contents
     */
    private final File taskFile;

    /**
     * The TaskResult
     */
    private TaskResult result;

    /**
     * The task config.
     */
    private final Config config;
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
     * @param taskFile  File containing the task contents.
     * @param taskConfig The task specific config.
     * @param templateService The template service provider.
     * @param taskExecutor The task executor service responsible for executing tasks of this type.
     */
    protected BaseTask(String id, File taskFile, Config taskConfig, TemplateService templateService, TaskExecutor taskExecutor) {
        super.setId(id);
        this.taskId = id;
        this.taskFile = taskFile;
        this.config = taskConfig.withFallback(GlobalConfigurationProvider.get().getConfiguration());
        this.templateService = templateService;
        this.taskExecutor = taskExecutor;
        this.result = new TaskResult.Builder(TaskResult.Result.NOT_EXECUTED).build();
        log.trace("Built a new " + this.getClass() + " - " + this.getId());
    }

    /**
     * Construct a new Task with the given id and contents.
     * @param id    task id.
     * @param taskFile  Name of the file containing the task contents.
     * @param taskConfig File from which task specific config will be loaded.
     * @param templateService The template service provider.
     * @param taskExecutor The task executor service responsible for executing tasks of this type.
     */
    private BaseTask(String id, File taskFile, File taskConfig, TemplateService templateService, TaskExecutor taskExecutor) {
        this(id,taskFile,ConfigFactory.parseFile(taskConfig),templateService,taskExecutor);
    }

    @Override
    public String toString() {
        return  getClass().getName() + "{id = " + taskId +
                ",taskFile='" + taskFile.getAbsolutePath() + '\'' +
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
     * @return the taskFile.
     */
    public File getTaskFile() { return taskFile; }

    /**
     * @return the task contents.
     */
    public String getTaskContents() throws JobRunnerException {
        try {
            return FileUtils.readFileToString(taskFile, Charset.defaultCharset());
        } catch (IOException e) {
            throw new JobRunnerException("Error reading file.",e);
        }
    }

    /**
     * @return the taskFile contents after template has been applied
     */
    public String getRenderedTaskContents() throws JobRunnerException {
        try {
            return templateService.apply(getId(), getTaskContents(), getConfig());
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
     * Return the Configuration for this taskFile.
     *
     * @return Config {@link com.typesafe.config.Config}
     */
    public Config getConfig() {
        return config;
    }

    /**
     * Set the {@code TaskResult} for this taskFile.
     * @param t - TaskResult
     * @return The TaskResult just set.
     */
    public TaskResult setTaskResult(TaskResult t) {
        result = t;
        return t;
    }

    /**
     * Returns the list of {@code TaskIds} that this Task depends on. All of the Tasks in this
     * list must have a successful status in order for this Task to run.
     * @return The list of {@code taskIds} that this Task is dependent on. List returned can be empty.
     */
    public List<String> getDependencies() {
        return getConfig().hasPath("depends-on.id") ? getConfig().getStringList("depends-on.id")
                : Collections.emptyList();
    }

    /**
     * {@code shouldExecute} Is this taskFile executable?
     *
     * <p>This method is called to determine if the taskFile can execute. A taskFile is considered executable
     * if all its parent dependencies are not in a failed state.</p>
     *
     * @param parentResults List of parent {@code TaskResults}, keyed by {@code taskid}
     * @return {@code true} if the taskFile can execute, {@code false otherwise}
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
     * <p>Executes this taskFile via a {@code TaskExecutor}.</p>
     *
     * @return set to {@code SUCCESSFUL} if execution successful, {@code FAILED} if error occurs.
     * @throws TaskExecutionException thrown when a execution problem is encountered.
     */
    public TaskResult execute() throws TaskExecutionException {
        log.trace("About to execute taskFile id: " + this.getId());
        setTaskResult(new TaskResult.Builder(TaskResult.Result.RUNNING).build());
        TaskResult taskResult;
        try {
            taskResult = taskExecutor.executeTask(this);
        } catch (Exception e) {
            setTaskResult(new TaskResult.Builder(TaskResult.Result.FAILED).exception(e).build());
            throw new TaskExecutionException("Task: " + getId() + " failed with " + e.getMessage(),e);
        }
        if (taskResult.failed())
            log.error("Task: " + this.getId() + " Result: " + taskResult);
        else
            log.trace("Task: " + this.getId() + " Result: " + taskResult);
        return setTaskResult(taskResult);
    }

    /**
     * Tasks are considered equal if they have the same taskFile id, and taskFile contents.
     *
     * @param other the other object to compare.
     * @return {@code true} if tasks match on {@code taskFile, taskId and result}, {@code false}</code>
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
                Objects.equals(getTaskFile(), o.getTaskFile());
    }

    @Override
    public int hashCode() {
        return Objects.hash(taskId, taskFile);
    }

    /**
     * Logger
     */
    private static final Logger log = LoggerFactory.getLogger(BaseTask.class);

}
