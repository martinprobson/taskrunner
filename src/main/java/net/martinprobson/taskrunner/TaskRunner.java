package net.martinprobson.taskrunner;

import com.github.dexecutor.core.DefaultDexecutor;
import com.github.dexecutor.core.ExecutionConfig;
import net.martinprobson.taskrunner.configurationservice.ConfigurationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

class TaskRunner extends DefaultDexecutor<String, TaskResult> {

    private static final Logger log = LoggerFactory.getLogger(TaskRunner.class);
    private final TaskRunnerConfig config;

    TaskRunner(final TaskRunnerConfig config) throws TaskRunnerException {
        super(config);
        this.config = config;
        setDependencies();
    }

    //@TODO Test cases for all built classes so far.
    //@TODO Test JDBCTaskExecutor.
    //@TODO Can the configuration around DExecutor be put in the configuration provider as well?
    //@TODO Add LocalFileSystemTaskBuilder.
    public static void main(String args[]) {
        // Wire up our configuration to our global configuration service.
        ConfigurationService.load(new ConfigurationService(new TaskRunnerConfigurationProvider("global_config.xml", "user_password.xml")));

        TaskBuilder taskBuilder = new ManualTaskBuilder();
//      TaskBuilder taskBuilder =new LocalFileSystemTaskBuilder(new TaskBuilder.Resource("DUMMY"),"global_config.xml");
        ExecutorService executorService = Executors.newFixedThreadPool(2);
        try {
            TaskGroup taskGroup = new TaskGroup(taskBuilder);
            TaskRunnerConfig config = new TaskRunnerConfig(executorService, taskGroup);
            TaskRunner hqlExecutor = new TaskRunner(config);
            hqlExecutor.execute(ExecutionConfig.NON_TERMINATING);
        } catch (TaskRunnerException e) {
            log.error("TaskRunnerException", e);
        } finally {
            try {
                executorService.shutdownNow();
                executorService.awaitTermination(1, TimeUnit.SECONDS);
            } catch (InterruptedException e) {
                log.error("Interrupt");
            }
        }

    }

    private void setDependencies() throws TaskRunnerException {
        for (DependentTask task : config.getTaskGroup()) {
            for (String dep : task.getDependencies()) {
                if (config.getTaskGroup().hasId(dep)) {
                    this.addDependency(dep, task.getId());
                    log.trace(task.getId() + " depends on " + dep);
                } else
                    throw new TaskRunnerException(task.getId() + " - setDependencies: There is no Task with an id of: " + dep);
            }
            if (task.getDependencies().size() == 0) this.addIndependent(task.getId());
        }
    }

}
