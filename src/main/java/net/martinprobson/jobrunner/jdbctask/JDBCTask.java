package net.martinprobson.jobrunner.jdbctask;

import com.google.inject.assistedinject.Assisted;
import com.google.inject.assistedinject.AssistedInject;
import com.google.inject.name.Named;
import com.typesafe.config.Config;
import net.martinprobson.jobrunner.common.BaseTask;
import net.martinprobson.jobrunner.common.TaskExecutor;
import net.martinprobson.jobrunner.template.TemplateService;

import java.io.File;


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
     * @param taskFile File containing the SQL to execute.
     * @param taskConfiguration  Task specification configuration XML filename.
     */
    @AssistedInject
    private JDBCTask(TemplateService templateService,
                     @Named("jdbc") TaskExecutor jdbcTaskExecutor,
                     @Assisted String id,
                     @Assisted File taskFile,
                     @Assisted Config taskConfiguration) {
        super(id,taskFile,taskConfiguration,templateService,jdbcTaskExecutor);
    }
}
