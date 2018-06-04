package net.martinprobson.jobrunner.sparkjartask;

import com.google.inject.assistedinject.Assisted;
import com.google.inject.assistedinject.AssistedInject;
import com.google.inject.name.Named;
import com.typesafe.config.Config;
import net.martinprobson.jobrunner.common.BaseTask;
import net.martinprobson.jobrunner.common.TaskExecutor;
import net.martinprobson.jobrunner.template.TemplateService;

import java.io.File;


/**
 * <p>{@code SparkJarTask}</p>
 *
 * <p>A task that holds a reference to a jar file
 * that will be executed in Spark via {@code spark-submit}.</p>
 *
 * @author martinr
 */
class SparkJarTask extends BaseTask {

    @SuppressWarnings("unused")
    private static TaskExecutor taskExecutor = null;

    /**
     *
     * Creates a SparkJarTask with the specified id, jar file name  and task configuration.
     * @param taskId             Task Id
     * @param taskFile            Name of the jar file that will be submitted via spark-submit.
     * @param taskConfiguration  Task configuration filename.
     */
    @AssistedInject
    SparkJarTask(@Named("spark-jar")   TemplateService templateService,
                         @Named("spark-jar")   TaskExecutor SparkJarTaskExecutor,
                         @Assisted             String taskId,
                         @Assisted             File taskFile,
                         @Assisted Config      taskConfiguration) {
        super(taskId,taskFile,taskConfiguration,templateService,SparkJarTaskExecutor);
    }

}
