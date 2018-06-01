package net.martinprobson.jobrunner.sparkjartask;

import com.google.inject.AbstractModule;
import com.google.inject.assistedinject.FactoryProvider;
import com.google.inject.multibindings.MapBinder;
import com.google.inject.name.Names;
import net.martinprobson.jobrunner.TaskFactory;
import net.martinprobson.jobrunner.common.TaskExecutor;
import net.martinprobson.jobrunner.template.DummyTemplateService;
import net.martinprobson.jobrunner.template.TemplateService;

/**
 * <h3>{@code SparkJarTaskModule}</h3>
 * <p>Google Guice dependency injection for {@code SparkJarTask}.</p>
 * <p>This Task is injected into list of available task types (via the
 * {@code MapBinder}
 * - See <a href="https://github.com/google/guice/wiki/Multibindings">Guice Multi-bindings</a>)
 * </p>
 *
 */
public class SparkJarTaskModule extends AbstractModule {

    @Override
    public void configure() {
        MapBinder<String, TaskFactory> mapBinder = MapBinder.newMapBinder(binder(), String.class, TaskFactory.class);
        mapBinder.addBinding("spark-jar").toProvider(FactoryProvider.newFactory(TaskFactory.class, SparkJarTask.class));

        // This task executes jars, so it does not need a template service - bind a dummy one.
        bind(TemplateService.class).annotatedWith(Names.named("spark-jar")).to(DummyTemplateService.class);
        // We do need an executor service however...
        bind(TaskExecutor.class).annotatedWith(Names.named("spark-jar")).to(SparkJarTaskExecutor.class);
    }

}
