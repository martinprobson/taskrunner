package net.martinprobson.jobrunner;

import com.google.inject.AbstractModule;
import com.google.inject.assistedinject.FactoryModuleBuilder;
import com.google.inject.name.Names;
import net.martinprobson.jobrunner.jdbctask.JDBCTask;
import net.martinprobson.jobrunner.jdbctask.JDBCTaskExecutor;
import net.martinprobson.jobrunner.template.FreeMarkerTemplateService;
import net.martinprobson.jobrunner.template.TemplateService;

/**
 * <h3>{@code JobRunnerModule}</h3>
 * <p>Google Guice dependency injection for {@code JobRunner} framework.</p>
 * <p>Individual task executors as well as a template service are injected into the
 * private Task constructors, tasks are constructed via a {@link TaskFactory}</p>
 */
public class JobRunnerModule extends AbstractModule {

    @Override
    public void configure() {
        bind(TemplateService.class).to(FreeMarkerTemplateService.class);
        bind(TaskExecutor.class).annotatedWith(Names.named("dummy")).to(DummyTaskExecutor.class);
        bind(TaskExecutor.class).annotatedWith(Names.named("jdbc")).to(JDBCTaskExecutor.class);
        install(new FactoryModuleBuilder()
                .implement(BaseTask.class,Names.named("dummy"),DummyTask.class)
                .implement(BaseTask.class,Names.named("jdbc"),JDBCTask.class)
                .build(TaskFactory.class));
        install(new FactoryModuleBuilder().build(LocalFileSystemTaskBuilderFactory.class));
    }
}
