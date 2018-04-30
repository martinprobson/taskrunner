package net.martinprobson.main;

import com.github.dexecutor.core.DexecutorState;
import com.github.dexecutor.core.ExecutionConfig;
import net.martinprobson.taskrunner.*;
import net.martinprobson.taskrunner.configurationservice.ConfigurationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class Main {
    private static final Logger log = LoggerFactory.getLogger(Main.class);

    public static void main(String args[]) {
        /* 1. Setup out global configuration. */
        ConfigurationService.load(new ConfigurationService(new TaskRunnerConfigurationProvider("global_config.xml")));
        TaskRunnerConfig config = null;
        String testDir = new File(Objects.requireNonNull(Main.class.getClassLoader().getResource("example2")).getFile()).getAbsolutePath();
        /* 2. Setup an executor service. */
        ExecutorService executorService = Executors.newFixedThreadPool(2);
//        ExecutorService executorService = Executors.newFixedThreadPool(1);

        try {
            /* 3. Setup a Task Group that will hold our tasks (via a LocalFileSystemTaskBuilder) */
            TaskGroup taskGroup = new TaskGroup(LocalFileSystemTaskBuilder.create(testDir));
            /* 4. Pass the executor and TaskGroup to our TaskRunnerConfig. */
            config = new TaskRunnerConfig(  executorService, taskGroup);
            /* 5. Build a TaskRunner with the config */
            TaskRunner taskRunner = new TaskRunner(config);
            /* 6. Execute out tasks */
            //@TODO Launch a monitoring thread? Looping arounf Task status.
            taskRunner.execute(ExecutionConfig.NON_TERMINATING);
            //@TODO Shutdown monitor thread.

        } catch (TaskRunnerException e) {
            log.error("Unexpected Exception: " + e);
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
