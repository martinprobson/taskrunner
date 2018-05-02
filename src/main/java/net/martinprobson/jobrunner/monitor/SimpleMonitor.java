package net.martinprobson.jobrunner.monitor;

import net.martinprobson.jobrunner.*;
import net.martinprobson.jobrunner.configurationservice.ConfigurationService;
import net.martinprobson.jobrunner.main.Main;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.Objects;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class SimpleMonitor {
    private static final Logger log = LoggerFactory.getLogger(SimpleMonitor.class);
    private Job job;
    private final ScheduledExecutorService monitorService = Executors.newSingleThreadScheduledExecutor();

    public static SimpleMonitor getInstance(Job job) {
        return new SimpleMonitor(job);
    }

    private SimpleMonitor(Job job) {
        this.job = job;
    }

    private void monitor() {
        log.info(job.toString());
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
        ConfigurationService.load(new ConfigurationService(new JobRunnerConfigurationProvider("reference_config.xml")));
        String testDir = new File(Objects.requireNonNull(Main.class.getClassLoader().getResource("example2")).getFile()).getAbsolutePath();
        try {
            Job job = new Job(LocalFileSystemTaskBuilder.create(testDir,testDir));
            SimpleMonitor monitor = SimpleMonitor.getInstance(job);
            monitor.start();
            TimeUnit.SECONDS.sleep(30);
            monitor.stop();

        } catch (JobRunnerException e) {
            log.error("Unexpected Exception: " + e);
        } catch (InterruptedException e) {
            log.error("Unexpected Exception: " + e);
        }
    }
}











