package net.martinprobson.jobrunner.hivetask;

import com.google.inject.assistedinject.Assisted;
import com.google.inject.assistedinject.AssistedInject;
import com.google.inject.name.Named;
import com.typesafe.config.Config;
import net.martinprobson.jobrunner.common.BaseTask;
import net.martinprobson.jobrunner.common.TaskExecutor;
import net.martinprobson.jobrunner.template.TemplateService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;


/**
 * <p>{@code HiveTask}</p>
 *
 * <p>A task that holds Hive QL code that will be executed in Hive via the hive cli.</p>
 *
 * @author martinr
 */
class HiveTask extends BaseTask {

    private static final Logger log = LoggerFactory.getLogger(HiveTask.class);
    @SuppressWarnings("unused")
    private static TaskExecutor taskExecutor = null;

    /**
     *
     * Creates a HiveTask with the specified id,QL and task configuration.
     *
     * @param taskId             Task Id
     * @param taskFile           File containing the Hive QL code to run.
     * @param taskConfiguration  Task configuration filename.
     */
    @AssistedInject
    HiveTask(TemplateService templateService,
             @Named("hive") TaskExecutor hiveTaskExecutor,
             @Assisted String taskId,
             @Assisted File taskFile,
             @Assisted Config taskConfiguration) {
        super(taskId,taskFile,taskConfiguration,templateService,hiveTaskExecutor);
        log.trace("Built a new HiveTask: " + this);
    }

}
