package net.martinprobson.jobrunner;

import com.github.dexecutor.core.DexecutorConfig;

import java.util.concurrent.ExecutorService;

public class JobRunnerConfig extends DexecutorConfig<String, TaskResult> {

    private final Job job;

    public JobRunnerConfig(final ExecutorService executorService, final Job job) {
        super(executorService, job);
        this.job = job;
    }

    public Job getJob() {
        return job;
    }
}
