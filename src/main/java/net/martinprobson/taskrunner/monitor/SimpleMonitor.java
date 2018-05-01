package net.martinprobson.taskrunner.monitor;

import com.github.dexecutor.core.ExecutionConfig;
import net.martinprobson.taskrunner.*;
import net.martinprobson.taskrunner.configurationservice.ConfigurationService;
import net.martinprobson.taskrunner.main.Main;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class SimpleMonitor {
    private static final Logger log = LoggerFactory.getLogger(SimpleMonitor.class);
    private TaskGroup taskGroup;
    private final ScheduledExecutorService monitorService = Executors.newSingleThreadScheduledExecutor();

    public static SimpleMonitor getInstance(TaskGroup taskGroup) {
        return new SimpleMonitor(taskGroup);
    }

    private SimpleMonitor(TaskGroup taskGroup) {
        this.taskGroup = taskGroup;
    }

    private void monitor() {
        log.info(taskGroup.toString());
    }

    public void start() {
        monitorService.scheduleAtFixedRate(this::monitor, 0, 1, TimeUnit.SECONDS);
    }

    public void stop() {
        monitorService.shutdown();
        System.out.println("FINAL:");
        monitor();
    }

    public static void main(String args[]) {
        ConfigurationService.load(new ConfigurationService(new TaskRunnerConfigurationProvider("global_config.xml")));
        String testDir = new File(Objects.requireNonNull(Main.class.getClassLoader().getResource("example2")).getFile()).getAbsolutePath();
        try {
            TaskGroup taskGroup = new TaskGroup(LocalFileSystemTaskBuilder.create(testDir));
            SimpleMonitor monitor = SimpleMonitor.getInstance(taskGroup);
            monitor.start();
            TimeUnit.SECONDS.sleep(30);
            monitor.stop();

        } catch (TaskRunnerException e) {
            log.error("Unexpected Exception: " + e);
        } catch (InterruptedException e) {
            log.error("Unexpected Exception: " + e);
        }
    }
}











