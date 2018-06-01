package net.martinprobson.jobrunner.jdbctask;

import com.google.inject.assistedinject.Assisted;
import com.google.inject.assistedinject.AssistedInject;
import com.google.inject.name.Named;
import com.typesafe.config.Config;
import net.martinprobson.jobrunner.common.BaseTask;
import net.martinprobson.jobrunner.common.TaskExecutor;
import net.martinprobson.jobrunner.template.TemplateService;


/**
 * <p>{@code JDBCTask}</p>
 *
 * <p>A task that holds SQL that will be executed against a JDBC connection.</p>
 *
 * @author martinr
 */
class JDBCTask extends BaseTask {

    /**
     *
     * Creates a JDBCTask with the specified id,sql and task configuration.
     *
     * @param id Task Id
     * @param task Sql contents
     * @param taskConfiguration  Task specification configuration XML filename.
     */
    @AssistedInject
    private JDBCTask(TemplateService templateService,
                     @Named("jdbc") TaskExecutor jdbcTaskExecutor,
                     @Assisted("id") String id,
                     @Assisted("task") String task,
                     @Assisted Config taskConfiguration) {
        super(id,task,taskConfiguration,templateService,jdbcTaskExecutor);
    }
}
