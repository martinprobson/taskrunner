package net.martinprobson.jobrunner.sparkpythontask;

import com.google.inject.AbstractModule;
import com.google.inject.multibindings.MapBinder;
import net.martinprobson.jobrunner.DummyExternalCommandBuilder;
import net.martinprobson.jobrunner.TaskFactory;
import net.martinprobson.jobrunner.common.ExternalCommandBuilder;
import net.martinprobson.jobrunner.common.TaskExecutor;
import net.martinprobson.jobrunner.template.DummyTemplateService;
import net.martinprobson.jobrunner.template.TemplateService;

import static com.google.inject.name.Names.named;

@SuppressWarnings( "deprecation" )
public class TestSparkPythonTaskModule extends AbstractModule {

    @Override
    public void configure() {
        MapBinder<String, TaskFactory> mapBinder = MapBinder.newMapBinder(binder(), String.class, TaskFactory.class);
        mapBinder.addBinding("spark-python").toProvider(com.google.inject.assistedinject.FactoryProvider.newFactory(TaskFactory.class, SparkPythonTask.class));

        // This task executes jars, so it does not need a template service - bind a dummy one.
        bind(TemplateService.class).annotatedWith(named("spark-python")).to(DummyTemplateService.class);
        // We do need an executor service however...
        bind(TaskExecutor.class).annotatedWith(named("spark-python")).to(SparkPythonTaskExecutor.class);
        bind(ExternalCommandBuilder.class).to(DummyExternalCommandBuilder.class);
    }

}
