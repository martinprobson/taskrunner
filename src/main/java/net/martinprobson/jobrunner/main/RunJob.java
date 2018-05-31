package net.martinprobson.jobrunner.main;

import com.github.dexecutor.core.ExecutionConfig;
import net.martinprobson.jobrunner.Job;
import net.martinprobson.jobrunner.JobRunner;
import net.martinprobson.jobrunner.JobRunnerConfig;
import net.martinprobson.jobrunner.LocalFileSystemTaskBuilder;
import net.martinprobson.jobrunner.common.JobRunnerException;
import net.martinprobson.jobrunner.configurationservice.GlobalConfigurationProvider;
import org.apache.commons.cli.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class RunJob {

    /**
     * Logger
     */
    private static final Logger log = LoggerFactory.getLogger(RunJob.class);
    private static Job job;

    private RunJob() {
    }

    /**
     * <h3>{@code RunJob.main}</h3>
     * <p>Main command line entry point for the RunJob application.</p>
     * <p>Run a group of related tasks according to specifications passed in via the
     * following command line: -</p>
     * <p>
     *
     * </p>
     * <blockquote><pre>{@code
     *  usage: runjob
     *      -help               Display help
     *      -conf <DIR>         Directory containing application and task specific configuration
     *                          files.
     *      -tasks <DIR>        Directory containing tasks to be run.}
     * </pre></blockquote>
     *
     * @author martinr
     */
    public static void main(String args[]) {
        Args a = processCmdLine(args);
        int rc;
        if (a.renderTask)
            rc = render(a);
        else
            rc = run(a.taskDirectory, a.configDirectory);
        System.exit(rc);
    }

    /**
     * <h3>{@code RunJob.run}</h3>
     * <p>Main entry point for the RunJob application.</p>
     * <p>Run a group of related tasks according to specifications passed in via the
     * following: -</p>
     *
     * @param args Config/task and task id to render.
     * @return 0 Task rendered successfully.
     * 1 Error exception occurred.
     * @author martinr
     */
    static int render(Args args) {
        // Initialize our global configuration.
        initializeGlobalConfig(args.configDirectory);
        int rc = 0;
        try {
            job = new Job(LocalFileSystemTaskBuilder.create(args.taskDirectory, args.configDirectory));
            if (job.hasId(args.renderTaskId)) {
                System.out.println(job.getId(args.renderTaskId).getRenderedTaskContents());
            } else
                throw new JobRunnerException("TaskId: " + args.renderTaskId + " does not exist");

        } catch (JobRunnerException e) {
            System.err.println("Exception: " + e);
            rc = 2;
        }
        return rc;
    }

    /**
     * <h3>{@code RunJob.run}</h3>
     * <p>Main entry point for the RunJob application.</p>
     * <p>Run a group of related tasks according to specifications passed in via the
     * following: -</p>
     *
     * @param taskDirectory   The full path to directory on local filesystem holding tasks.
     * @param configDirectory The full path to directory containing application/task config file(s).
     * @return 0 TaskGroup executed (see individual tasks for status).
     * 1 Error exception occurred.
     * @author martinr
     */
    static int run(String taskDirectory, String configDirectory) {

        // Initialize out global configuration.
        initializeGlobalConfig(configDirectory);
        int numThreads = GlobalConfigurationProvider.get().getConfiguration().getInt("jobrunner.threads");
        ExecutorService executorService = Executors.newFixedThreadPool(numThreads);
        int rc = 0;
        JobRunnerConfig config;
        try {
            job = new Job(LocalFileSystemTaskBuilder.create(taskDirectory, configDirectory));
            config = new JobRunnerConfig(executorService, job);
            JobRunner jobRunner = new JobRunner(config);
            jobRunner.execute(ExecutionConfig.NON_TERMINATING);
        } catch (JobRunnerException e) {
            System.err.println("Exception: " + e);
            rc = 2;
        } finally {
            try {
                executorService.shutdownNow();
                executorService.awaitTermination(1, TimeUnit.SECONDS);
            } catch (InterruptedException e) {
                System.err.println("Exception: " + e);
                rc = 2;
            }
        }
        return rc;
    }

    static Job getJob() {
        return job;
    }

    private static void initializeGlobalConfig(String configDirectory) {
        String appConfigFilename;
        if (configDirectory.endsWith(File.separator))
            appConfigFilename = configDirectory + "application.conf";
        else
            appConfigFilename = configDirectory + File.separator + "application.conf";
        if (!Files.exists(Paths.get(appConfigFilename))) {
            log.warn("No global application file, " + appConfigFilename + " exists using default config");
            GlobalConfigurationProvider.get();
        } else
            GlobalConfigurationProvider.setConfig(new File(appConfigFilename));
    }

    private static Args processCmdLine(String args[]) {
        Options options = new Options();
        Option confDir = Option.builder("conf")
                .argName("DIR")
                .hasArg()
                .required()
                .desc("Directory containing application/task specific configuration files.")
                .build();
        Option tasks = Option.builder("tasks")
                .argName("DIR")
                .hasArg()
                .required()
                .desc("Directory containing tasks to be run.")
                .build();
        Option render = Option.builder("render")
                .argName("taskId")
                .hasArg()
                .optionalArg(true)
                .desc("Render the given taskId to stdout")
                .build();
        options.addOption("help", false, "Display help")
                .addOption(confDir).addOption(tasks).addOption(render);
        CommandLineParser parser = new DefaultParser();
        CommandLine cmd = null;
        try {
            cmd = parser.parse(options, args);
        } catch (MissingArgumentException e) {
            System.err.println("Missing argument " + e.getOption());
            System.exit(2);
        } catch (ParseException e) {
            System.err.println(e.getMessage());
            HelpFormatter h = new HelpFormatter();
            h.printHelp("runjob", options);
            System.exit(2);
        }
        if (cmd.hasOption("help")) {
            HelpFormatter h = new HelpFormatter();
            h.printHelp("runjob", options);
            System.exit(2);
        }
        if (!Files.isDirectory(Paths.get(cmd.getOptionValue("conf")))) {
            System.err.println("directory " + cmd.getOptionValue("conf") + " does not exist.");
            System.exit(2);
        }

        if (!Files.isDirectory(Paths.get(cmd.getOptionValue("tasks")))) {
            System.err.println("directory " + cmd.getOptionValue("tasks") + " does not exist.");
            System.exit(2);
        }

        return new Args(cmd.getOptionValue("tasks"),
                cmd.getOptionValue("conf"),
                cmd.hasOption("render"),
                cmd.getOptionValue("render"));

    }

    private static class Args {
        final String taskDirectory;
        final String configDirectory;
        final boolean renderTask;
        final String renderTaskId;

        Args(String taskDirectory, String configDirectory, boolean renderTask, String renderTaskId) {
            this.taskDirectory = taskDirectory;
            this.configDirectory = configDirectory;
            this.renderTask = renderTask;
            this.renderTaskId = renderTaskId;
        }
    }
}
