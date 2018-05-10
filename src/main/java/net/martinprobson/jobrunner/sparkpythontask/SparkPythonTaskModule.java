package net.martinprobson.jobrunner.sparkpythontask;

import com.google.inject.AbstractModule;
import com.google.inject.assistedinject.FactoryProvider;
import com.google.inject.multibindings.MapBinder;
import com.google.inject.name.Names;
import net.martinprobson.jobrunner.TaskFactory;
import net.martinprobson.jobrunner.common.TaskExecutor;
import net.martinprobson.jobrunner.template.FreeMarkerTemplateService;
import net.martinprobson.jobrunner.template.TemplateService;

/**
 * <h3>{@code SparkPythonTaskModule}</h3>
 * <p>Google Guice dependency injection for {@code SparkPythonTask}.</p>
 * <p>This Task is injected into list of available task types (via the
 * {@code MapBinder}).</p>
 *
 */
public class SparkPythonTaskModule extends AbstractModule {

    @Override
    public void configure() {
        MapBinder<String, TaskFactory> mapBinder = MapBinder.newMapBinder(binder(), String.class, TaskFactory.class);
        mapBinder.addBinding("spark-python").toProvider(FactoryProvider.newFactory(TaskFactory.class, SparkPythonTask.class));

        // JDBCTask needs a Task Executor and TemplateService implementation.
        bind(TemplateService.class).to(FreeMarkerTemplateService.class);
        bind(TaskExecutor.class).annotatedWith(Names.named("spark-python")).to(SparkPythonTaskExecutor.class);
    }

}
