package net.martinprobson.main;

import com.github.dexecutor.core.ExecutionConfig;
import net.martinprobson.taskrunner.*;
import net.martinprobson.taskrunner.configurationservice.ConfigurationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class Main {
    private static final Logger log = LoggerFactory.getLogger(Main.class);

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

}
