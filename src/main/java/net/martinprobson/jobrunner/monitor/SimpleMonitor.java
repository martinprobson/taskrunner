package net.martinprobson.jobrunner.monitor;

import net.martinprobson.jobrunner.*;
import net.martinprobson.jobrunner.common.JobRunnerException;
import net.martinprobson.jobrunner.main.RunJob;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.Objects;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class SimpleMonitor {

    public static SimpleMonitor getInstance(Job job) {
        return new SimpleMonitor(job);
    }

    private SimpleMonitor(Job job) {
        this.job = job;
    }

    private void monitor() {
        log.info(job.status());
    }

    public void start() {
        monitorService.scheduleAtFixedRate(this::monitor, 0, 10, TimeUnit.SECONDS);
    }

    public void stop() {
        monitorService.shutdown();
        System.out.println("FINAL:");
        monitor();
    }

    private static final Logger log = LoggerFactory.getLogger(SimpleMonitor.class);
    private final Job job;
    private final ScheduledExecutorService monitorService = Executors.newSingleThreadScheduledExecutor();
}











