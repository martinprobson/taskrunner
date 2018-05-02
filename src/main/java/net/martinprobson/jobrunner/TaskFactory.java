package net.martinprobson.jobrunner;

import com.google.inject.assistedinject.Assisted;
import com.google.inject.name.Named;
import net.martinprobson.jobrunner.jdbctask.JDBCTask;
import net.martinprobson.jobrunner.sparkscalatask.SparkScalaTask;
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
    @Named("dummy") BaseTask createDummyTask(@Assisted("taskid") String taskid,
                                              Configuration taskConfiguration);

    /**
     * Construct a new {@link DummyTask}.
     * <p>A {@code DummyTask} always returns success when executed.</p>
     * @param taskid The taskId to create.
     * @return A DummyTask.
     */
    @Named("dummy") BaseTask createDummyTask(@Assisted("taskid") String taskid);

    /**
     * Construct a new {@link JDBCTask}.
     * <p>A {@code JDBCTask} will execute SQL against a JDBC connection.</p>
     * @param taskid The taskId to create.
     * @param content The SQL that will be executed by this Task.
     * @param taskConfiguration The task specific configuration for this task.
     * @return A JDBCTask.
     */
    @Named("jdbc")
    BaseTask createJDBCTask(@Assisted("taskid") String taskid,
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
    BaseTask createJDBCTask(@Assisted("taskid") String taskid,
                            @Assisted("content") String content);

    /**
     * Construct a new {@link SparkScalaTask}.
     * <p>A {@code SparkScalaTask} will execute a Spark Scala script against a Spark connection.</p>
     * @param taskid The taskId to create.
     * @param content The Scala script that will be executed by this Task.
     * @param taskConfiguration The task specific configuration for this task.
     * @return A SparkScalaTask.
     */
    @Named("spark-scala")
    BaseTask createSparkScalaTask(@Assisted("taskid") String taskid,
                                        @Assisted("content") String content,
                                        Configuration taskConfiguration);

    /**
     * Construct a new {@link SparkScalaTask}.
     * <p>A {@code SparkScalaTask} will execute a Spark Scala script against a Spark connection.</p>
     * <p>A {@code SparkScalaTask} will execute SQL against a JDBC connection.</p>
     * @param taskid The taskId to create.
     * @param content The Scala script that will be executed by this Task.
     * @return A SparkScalaTask.
     */
    @Named("spark-scala")
    BaseTask createSparkScalaTask(@Assisted("taskid") String taskid,
                                        @Assisted("content") String content);

}
