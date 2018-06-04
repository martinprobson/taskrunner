package net.martinprobson.jobrunner.common;

import net.martinprobson.jobrunner.TaskResult;
import org.buildobjects.process.*;

/**
 * <p>
 * {@code DefaultExternalCommandBuilder} The default
 * implementation of the {@code ExternalCommandBuilder}
 * interface.
 * </p><p>
 * Uses the <a href="https://github.com/fleipold/jproc">jproc library</a> to
 * implement external command execution.
 * </p>
 *
 * @author martinr
 */
public class DefaultExternalCommandBuilder implements ExternalCommandBuilder {
    @Override
    public ExternalCommandBuilder setCmd(String cmd) {
        procBuilder = new ProcBuilder(cmd);
        return this;
    }

    @Override
    public ExternalCommandBuilder withArgs(String... args) {
        procBuilder.withArgs(args);
        return this;
    }

    @Override
    public void withTimeoutMillis(long timeoutMillis) {
        procBuilder.withTimeoutMillis(timeoutMillis);
    }

    @Override
    public TaskResult run() throws JobRunnerException {
        TaskResult taskResult;
        try {
            ProcResult procResult = procBuilder.run();
            taskResult = new TaskResult.Builder(TaskResult.Result.SUCCESS)
                    .error(procResult.getErrorString())
                    .exitValue(procResult.getExitValue())
                    .output(procResult.getOutputString())
                    .procString(procResult.getProcString())
                    .build();

        } catch (Exception e) {
            throw new JobRunnerException("failure",e);
        }
        return taskResult;
    }

    private ProcBuilder procBuilder;
}
