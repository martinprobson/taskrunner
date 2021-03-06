package net.martinprobson.jobrunner.common;

import com.google.inject.Inject;
import net.martinprobson.jobrunner.TaskResult;
import net.martinprobson.jobrunner.auth.Kerberos;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.List;

/**
 * A helper class for executing an external process.<p></p>
 * <p>Any {@code TaskExecutor} that requires an external command to be
 * run and monitored can inherit from this class.
 * </p>
 *
 * @author martinr
 */
public abstract class AbstractExternalCmdExecutor implements TaskExecutor {

    /**
     * Executes the Hive QL script in a {@code HiveTask}.
     * The results of the execution are set on the {@code TaskResult} object within the
     * HiveTask itself.
     *
     * @param task The task to execute.
     * @throws JobRunnerException on execution error.
     */
    @Override
    public TaskResult executeTask(BaseTask task) throws JobRunnerException {
        TaskResult taskResult;
        try {
            taskResult = execute(task);
        } catch (JobRunnerException e) {
            task.setTaskResult(new TaskResult.Builder(TaskResult.Result.FAILED)
                    .exception(e)
                    .build());
            throw e;
        }
        return task.setTaskResult(taskResult);
    }

    /**
     * <p>Check the environment is capable of executing the command.</p>
     * <p>For example, do the required environment variables exist? etc</p>
     *
     * @param task The task environment to check.
     * @throws JobRunnerException If there is an issue with the environment.
     */
    public abstract void checkEnv(BaseTask task) throws JobRunnerException;

    /**
     * <p>Check the environment is capable of executing the command.</p>
     * <p>For example, do the required environment variables exist? etc</p>
     *
     * @param envVars A list of environment variables to check (can be empty).
     * @throws JobRunnerException If there is an issue with the environment.
     */
    protected void checkEnv(List<String> envVars) throws JobRunnerException {
        for (String env : envVars)
            if (System.getenv(env) == null)
                throw new JobRunnerException("Environment variable: " + env + " is not set");
    }

    /**
     * <p>Get the timeout interval in milliseconds.</p>
     */
    protected abstract long getTimeOutMs(BaseTask task);

    /**
     * <p>Get the command to run.</p>
     *
     */
    protected abstract String getCmd();

    /**
     * <p>Get the command arguments.</p>
     */
    protected abstract String[] getArgs(BaseTask task) throws JobRunnerException;

    /**
     * <p>Get temp file name prefix</p>
     */
    protected String getTempFilePrefix() {
        return "";
    }

    /**
     * <p>Get temp file name suffix</p>
     */
    protected String getTempFileSuffix() {
        return "";
    }

    private TaskResult execute(BaseTask task) throws JobRunnerException {
        log.info(getClass().getName() + " executeTask - " + task.getId());
        checkEnv(task);
        Kerberos.auth();
        cmd.setCmd(getCmd())
                .withArgs(getArgs(task))
                .withTimeoutMillis(getTimeOutMs(task));
        log.trace("About to execute: " + task.getId());
        log.trace("Cmd: " + getCmd());
        for (String arg : getArgs(task))
            log.trace("Arg: " + arg);
        return cmd.run();
    }

    protected File createTempFile(BaseTask task) throws JobRunnerException {
        File temp;
        try {
            temp = File.createTempFile(getTempFilePrefix(), getTempFileSuffix());
            String content = task.getRenderedTaskContents();
            FileUtils.write(temp, content + "\n", Charset.defaultCharset());
        } catch (IOException e) {
            throw new JobRunnerException("Error creating temp file", e);
        }
        temp.deleteOnExit();
        return temp;
    }

    @Inject
    private ExternalCommandBuilder cmd;
    private static final Logger log = LoggerFactory.getLogger(AbstractExternalCmdExecutor.class);

}
