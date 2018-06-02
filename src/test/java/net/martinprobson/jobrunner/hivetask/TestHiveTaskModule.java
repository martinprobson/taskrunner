package net.martinprobson.jobrunner.hivetask;

import com.google.inject.AbstractModule;
import com.google.inject.multibindings.MapBinder;
import com.google.inject.name.Names;
import net.martinprobson.jobrunner.DummyExternalCommandBuilder;
import net.martinprobson.jobrunner.TaskFactory;
import net.martinprobson.jobrunner.common.ExternalCommandBuilder;
import net.martinprobson.jobrunner.common.TaskExecutor;
import net.martinprobson.jobrunner.template.DummyTemplateService;
import net.martinprobson.jobrunner.template.TemplateService;

@SuppressWarnings( "deprecation" )
public class TestHiveTaskModule extends AbstractModule {

    @Override
    public void configure() {
        MapBinder<String, TaskFactory> mapBinder = MapBinder.newMapBinder(binder(), String.class, TaskFactory.class);
        mapBinder.addBinding("hive").toProvider(com.google.inject.assistedinject.FactoryProvider.newFactory(TaskFactory.class, HiveTask.class));

        // This task executes jars, so it does not need a template service - bind a dummy one.
        bind(TemplateService.class).annotatedWith(Names.named("hive")).to(DummyTemplateService.class);
        // We do need an executor service however...
        bind(TaskExecutor.class).annotatedWith(Names.named("hive")).to(HiveTaskExecutor.class);
        bind(ExternalCommandBuilder.class).to(DummyExternalCommandBuilder.class);
    }

}
