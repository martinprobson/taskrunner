package net.martinprobson.jobrunner.common;

import net.martinprobson.jobrunner.TaskResult;
import net.martinprobson.jobrunner.auth.Kerberos;
import org.apache.commons.io.FileUtils;
import org.buildobjects.process.ExternalProcessFailureException;
import org.buildobjects.process.ProcBuilder;
import org.buildobjects.process.TimeoutException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;

/**
 * A helper class for executing an external process.<p></p>
 * <p>Any {@code TaskExecutor} that requires an external command to be
 * run and monitored can inherit from this class.
 *</p>
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
    public void executeTask(BaseTask task) throws JobRunnerException {
        try {
            execute(task);
        } catch (JobRunnerException e) {
            task.setTaskResult(new TaskResult(TaskResult.Result.FAILED, e));
            throw e;
        }
        task.setTaskResult(new TaskResult(TaskResult.Result.SUCCESS));
    }

    /**
     * <p>Check the environment is capable of executing the command.</p>
     * <p>For example, do the required environment variables exist? etc</p>
     * @throws JobRunnerException If there is an issue with the environment.
     */
    protected abstract void checkEnv() throws JobRunnerException;

    /**
     * <p>Get the timeout interval in milliseconds.</p>
     */
    protected abstract long getTimeOutMs(BaseTask task);

    /**
     * <p>Get the command to run.</p>
     * @throws JobRunnerException If there is an issue with the environment.
     */
    protected abstract String getCmd() throws JobRunnerException;

    /**
     * <p>Get the command arguments.</p>
     */
    protected abstract String[] getArgs(BaseTask task) throws JobRunnerException;

    /**
     * <p>Get temp file name prefix</p>
     */
    protected abstract String getTempFilePrefix();

    /**
     * <p>Get temp file name suffix</p>
     */
    protected abstract String getTempFileSuffix();

    protected void execute(BaseTask task) throws JobRunnerException {
        log.trace(getClass().getName() + " executeTask - " + task.getId());
        checkEnv();
        Kerberos.auth();
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        ProcBuilder procBuilder = new ProcBuilder(getCmd())
                .withArgs(getArgs(task))
                .withOutputStream(output)
                .withTimeoutMillis(getTimeOutMs(task));
        try {
            procBuilder.run();
        } catch (ExternalProcessFailureException e) {
            throw new JobRunnerException("Task: " + task.getId() + " failed", e);
        } catch (TimeoutException e) {
            throw new JobRunnerException("Task: " + task.getId() + " timeout", e);
        } catch (Exception e) {
            throw new JobRunnerException("Task: " + task.getId(), e);
        }
    }

    protected File createTempFile(BaseTask task) throws JobRunnerException {
        File temp;
        try {
            temp = File.createTempFile(getTempFilePrefix(),getTempFileSuffix());
            String content = task.getRenderedTaskContents();
            FileUtils.write(temp, content + "\n", Charset.defaultCharset());
        } catch (IOException e) {
            throw new JobRunnerException("Error creating temp file", e);
        }
        temp.deleteOnExit();
        return temp;
    }

    private static final Logger log = LoggerFactory.getLogger(AbstractExternalCmdExecutor.class);

}
