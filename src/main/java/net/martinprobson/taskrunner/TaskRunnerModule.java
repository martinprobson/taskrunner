package net.martinprobson.taskrunner;

import com.google.inject.AbstractModule;
import com.google.inject.assistedinject.FactoryModuleBuilder;
import com.google.inject.name.Names;
import net.martinprobson.taskrunner.jdbctask.JDBCTask;
import net.martinprobson.taskrunner.jdbctask.JDBCTaskExecutor;
import net.martinprobson.taskrunner.template.DummyTemplateService;
import net.martinprobson.taskrunner.template.TemplateService;

/**
 * <h3>{@code TaskRunnerModule}</h3>
 * <p>Google Guice dependency injection for {@code TaskRunner} framework.</p>
 * <p>Individual task executors as well as a template service are injected into the
 * private Task constructors, tasks are constructed via a {@link TaskFactory}</p>
 */
public class TaskRunnerModule extends AbstractModule {

    @Override
    public void configure() {
        bind(TemplateService.class).to(DummyTemplateService.class);
        bind(TaskExecutor.class).annotatedWith(Names.named("dummy")).to(DummyTaskExecutor.class);
        bind(TaskExecutor.class).annotatedWith(Names.named("jdbc")).to(JDBCTaskExecutor.class);
        install(new FactoryModuleBuilder()
                .implement(BaseTask.class,Names.named("dummy"),DummyTask.class)
                .implement(BaseTask.class,Names.named("jdbc"),JDBCTask.class)
                .build(TaskFactory.class));
        install(new FactoryModuleBuilder().build(LocalFileSystemTaskBuilderFactory.class));
    }
}
