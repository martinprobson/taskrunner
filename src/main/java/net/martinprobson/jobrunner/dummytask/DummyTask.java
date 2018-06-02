package net.martinprobson.jobrunner.dummytask;


import com.google.inject.assistedinject.Assisted;
import com.google.inject.assistedinject.AssistedInject;
import com.google.inject.name.Named;
import com.typesafe.config.Config;
import net.martinprobson.jobrunner.common.BaseTask;
import net.martinprobson.jobrunner.common.TaskExecutor;
import net.martinprobson.jobrunner.template.TemplateService;

/**
 * A {@code DummyTask} A task with no content that always returns SUCCESS when executed.
 *
 * @author martinr
 */
class DummyTask extends BaseTask {

    @AssistedInject
    private DummyTask(  @Assisted("id") String id,
                        @Assisted("task") String task,
                        @Assisted Config taskConfiguration,
                        TemplateService templateService,
                        @Named("dummy") TaskExecutor taskExecutor) {
        super(id,task,taskConfiguration,templateService,taskExecutor);
    }

}
