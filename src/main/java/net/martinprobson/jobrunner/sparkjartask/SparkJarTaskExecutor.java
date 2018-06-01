package net.martinprobson.jobrunner.sparkjartask;

import net.martinprobson.jobrunner.common.*;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * <p>{@code SparkJarTaskExecutor}</p>
 *
 * <p>Responsible for executing a jar file.</p>
 * <p>Code is executed via {@code spark-submit}</p>
 *
 * @author martinr
 */
class SparkJarTaskExecutor extends AbstractExternalCmdExecutor implements TaskExecutor {


    /**
     * <p>Check the environment is capable of executing the command.</p>
     * <p>For example, do the required environment variables exist? etc</p>
     *
     * @throws JobRunnerException If there is an issue with the environment.
     */
    @Override
    public void checkEnv(BaseTask task) throws JobRunnerException {
        checkEnv(task.getConfig().getStringList("spark-jar.environment"));
    }

    /**
     * <p>Get the timeout interval in milliseconds.</p>
     */
    @Override
    protected long getTimeOutMs(BaseTask task) {
        return task.getConfig().getLong("spark-jar.timeoutms");
    }

    /**
     * <p>Get the command to run.</p>
     */
    protected String getCmd() {
        return System.getenv("SPARK_HOME")   +
                File.separatorChar +
                "bin" +
                File.separatorChar +
                "spark-submit";
    }

    /**
     * <p>Get the command arguments.</p>
     */
    @Override
    protected String[] getArgs(BaseTask task) {
        List<String> args = new ArrayList<>();
        args.add("--master");
        args.add(task.getConfig().getString("spark-jar.master"));
        args.add("--num-executors");
        args.add(task.getConfig().getString("spark-jar.num-executors"));
        args.add("--queue");
        args.add(task.getConfig().getString("spark-jar.queue"));
        if (!task.getConfig().getIsNull("spark-jar.driver-java-options")) {
            args.add("--driver-java-options");
            args.add(task.getConfig().getString("spark-jar.driver-java-options"));
        }
        args.add(task.getTaskContents());
        return args.toArray(new String[0]);
    }
}
