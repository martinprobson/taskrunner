package net.martinprobson.jobrunner.jdbctask;

import com.google.inject.AbstractModule;
import com.google.inject.multibindings.MapBinder;
import com.google.inject.name.Names;
import net.martinprobson.jobrunner.TaskFactory;
import net.martinprobson.jobrunner.common.TaskExecutor;
import net.martinprobson.jobrunner.template.FreeMarkerTemplateService;
import net.martinprobson.jobrunner.template.TemplateService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * <h3>{@code JDBCTaskModule}</h3>
 * <p>Google Guice dependency injection for {@code JDBCTask}.</p>
 * <p>This Task is injected into list of available task types (via the
 * {@code MapBinder}
 * - See <a href="https://github.com/google/guice/wiki/Multibindings">Guice Multi-bindings</a>)
 * </p>
 *
 *
 */
@SuppressWarnings( "deprecation" )
public class JDBCTaskModule extends AbstractModule {

    @Override
    public void configure() {
        log.debug("Configuring JDBCTask");
        MapBinder<String, TaskFactory> mapBinder = MapBinder.newMapBinder(binder(), String.class, TaskFactory.class);

        //TODO FactoryProvider is deprecated
        mapBinder.addBinding("jdbc").toProvider(com.google.inject.assistedinject.FactoryProvider.newFactory(TaskFactory.class, JDBCTask.class));

        // JDBCTask needs a Task Executor and TemplateService implementation.
        bind(TemplateService.class).to(FreeMarkerTemplateService.class);
        bind(TaskExecutor.class).annotatedWith(Names.named("jdbc")).to(JDBCTaskExecutor.class);
    }

    private static final Logger log = LoggerFactory.getLogger(JDBCTaskModule.class);

}
