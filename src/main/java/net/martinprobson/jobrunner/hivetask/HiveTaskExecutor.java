package net.martinprobson.jobrunner.hivetask;

import net.martinprobson.jobrunner.common.AbstractExternalCmdExecutor;
import net.martinprobson.jobrunner.common.BaseTask;
import net.martinprobson.jobrunner.common.JobRunnerException;
import net.martinprobson.jobrunner.common.TaskExecutor;

import java.io.File;

/**
 * <p>{@code HiveTaskExecutor}</p>
 *
 * <p>Responsible for executing Hive QL script via Hive cli.</p>
 *
 * @author martinr
 */
class HiveTaskExecutor extends AbstractExternalCmdExecutor implements TaskExecutor {

    /**
     * <p>Check the environment is capable of executing the command.</p>
     * <p>For example, do the required environment variables exist? etc</p>
     * @throws JobRunnerException If there is an issue with the environment.
     */
    @Override
    public void checkEnv(BaseTask task) throws JobRunnerException {
        checkEnv(task.getConfig().getStringList("hive.environment"));
    }

    /**
     * <p>Get the timeout interval in milliseconds.</p>
     */
    @Override
    protected long getTimeOutMs(BaseTask task) {
        return task.getConfig().getLong("hive.timeoutms");
    }

    /**
     * <p>Get the command to run.</p>
     */
    protected String getCmd() {
        return System.getenv("HIVE_HOME") +
                File.separatorChar +
                "bin" +
                File.separatorChar +
                "hive";
    }

    /**
     * <p>Get the command arguments.</p>
     * @throws JobRunnerException If there is an issue with the environment.
     */
    @Override
    protected String[] getArgs(BaseTask task) throws JobRunnerException {
        return new String[]{"-f", super.createTempFile(task).getAbsolutePath()};
    }

    /**
     * <p>Get temp file name prefix</p>
     */
    @Override
    protected String getTempFilePrefix() { return "hive";}

    /**
     * <p>Get temp file name suffix</p>
     */
    @Override
    protected String getTempFileSuffix() { return ".hql";}

}
