package net.martinprobson.jobrunner;

import net.martinprobson.jobrunner.common.ExternalCommandBuilder;
import java.util.ArrayList;
import java.util.List;

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
public class DummyExternalCommandBuilder implements ExternalCommandBuilder {

    private List<String> args = new ArrayList<>();
    private String cmd;

    @Override
    public ExternalCommandBuilder setCmd(String cmd) {
        this.cmd = cmd;
        return this;
    }

    @Override
    public ExternalCommandBuilder withArgs(String... args) {
        for (String a: args) this.args.add(a);
        return this;
    }

    @Override
    public void withTimeoutMillis(long timeoutMillis) {
    }

    @Override
    /**
     * Just send back the command that would have been executed.....
     */
    public TaskResult run() {
        args.add(0,cmd);
        return new TaskResult.Builder(TaskResult.Result.NOT_EXECUTED)
                .procString(String.join(" ",args))
                .build();
     }

}
