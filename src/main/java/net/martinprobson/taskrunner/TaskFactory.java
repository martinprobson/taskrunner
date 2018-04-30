package net.martinprobson.taskrunner;

import com.google.inject.assistedinject.Assisted;
import com.google.inject.name.Named;
import net.martinprobson.taskrunner.jdbctask.JDBCTask;
import org.apache.commons.configuration2.Configuration;

/**
 * <h3>{@code TaskFactory}</h3>
 * <p>A TaskFactory used to construct Tasks.</p>
 * <p>Note this is just an interface, the actual factory is created by
 * the Google Guice DI framework.</p>
 */
public interface TaskFactory {

    /**
     * Construct a new {@link DummyTask}.
     * <p>A {@code DummyTask} always returns success when executed.</p>
     * @param taskid The taskId to create.
     * @param taskConfiguration The task specific configuration for this task.
     * @return A DummyTask.
     */
    @Named("dummy") DummyTask createDummyTask(@Assisted("taskid") String taskid,
                                              Configuration taskConfiguration);

    /**
     * Construct a new {@link DummyTask}.
     * <p>A {@code DummyTask} always returns success when executed.</p>
     * @param taskid The taskId to create.
     * @return A DummyTask.
     */
    @Named("dummy") DummyTask createDummyTask(@Assisted("taskid") String taskid);

    /**
     * Construct a new {@link JDBCTask}.
     * <p>A {@code JDBCTask} will execute SQL against a JDBC connection.</p>
     * @param taskid The taskId to create.
     * @param content The SQL that will be executed by this Task.
     * @param taskConfiguration The task specific configuration for this task.
     * @return A JDBCTask.
     */
    @Named("jdbc")
    JDBCTask createJDBCTask(@Assisted("taskid") String taskid,
                            @Assisted("content") String content,
                            Configuration taskConfiguration);

    /**
     * Construct a new {@link JDBCTask}.
     * <p>A {@code JDBCTask} will execute SQL against a JDBC connection.</p>
     * @param taskid The taskId to create.
     * @param content The SQL that will be executed by this Task.
     * @return A JDBCTask.
     */
    @Named("jdbc")
    JDBCTask createJDBCTask(@Assisted("taskid") String taskid,
                            @Assisted("content") String content);

}
