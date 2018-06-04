package net.martinprobson.jobrunner.dummytask;


import com.google.inject.assistedinject.Assisted;
import com.google.inject.assistedinject.AssistedInject;
import com.google.inject.name.Named;
import com.typesafe.config.Config;
import net.martinprobson.jobrunner.common.BaseTask;
import net.martinprobson.jobrunner.common.TaskExecutor;
import net.martinprobson.jobrunner.template.TemplateService;

import java.io.File;

/**
 * A {@code DummyTask} A task with no content that always returns SUCCESS when executed.
 *
 * @author martinr
 */
class DummyTask extends BaseTask {

    @AssistedInject
    private DummyTask(  @Assisted String id,
                        @Assisted File taskFile,
                        @Assisted Config taskConfiguration,
                        TemplateService templateService,
                        @Named("dummy") TaskExecutor taskExecutor) {
        super(id,taskFile,taskConfiguration,templateService,taskExecutor);
    }

}
