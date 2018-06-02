package net.martinprobson.jobrunner.common;

import com.google.inject.ImplementedBy;
import net.martinprobson.jobrunner.TaskResult;

/**
 * <p>
 * {@code ExternalCommandBuilder} interface, provides
 * methods to construct an external command
 * with arguments and a time out value.
 * </p><p>
 * The {@code run()} method actually executes the command.
 * </p>
 *
 * @author martinr
 */
@ImplementedBy(DefaultExternalCommandBuilder.class)
public interface ExternalCommandBuilder {
    ExternalCommandBuilder setCmd(String cmd);
    ExternalCommandBuilder withArgs(String ...args);
    void withTimeoutMillis(long timeoutMillis);
    TaskResult run();

}
