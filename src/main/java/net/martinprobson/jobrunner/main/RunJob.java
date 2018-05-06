package net.martinprobson.jobrunner.main;

import com.github.dexecutor.core.ExecutionConfig;
import net.martinprobson.jobrunner.*;
import net.martinprobson.jobrunner.common.JobRunnerException;
import org.apache.commons.cli.*;

import java.io.File;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class RunJob {

    /**
     *  <h3>{@code RunJob.main}</h3>
     *  <p>Main command line entry point for the RunJob application.</p>
     *  <p>Run a group of related tasks according to specifications passed in via the
     *  following command line: -</p>
     *  <p>
     *
     *  </p>
     * <blockquote><pre>{@code
     *  usage: runjob
     *      -conf <XML file>    XML file for application level configuration.
     *                          Can be specified multiple times.
     *      -help               Display help
     *      -taskConf <DIR>     Directory containing task specific configuration
     *                          files.
     *      -tasks <DIR>        Directory containing tasks to be run.}
     * </pre></blockquote>
     *
     * @author martinr
     *
     */
    public static void main(String args[]) {
        Args a = processCmdLine(args);
        System.exit(run(a.taskDirectory,a.taskConfigDirectory));
    }

    /**
     *  <h3>{@code RunJob.run}</h3>
     *  <p>Main entry point for the RunJob application.</p>
     *  <p>Run a group of related tasks according to specifications passed in via the
     *  following: -</p>
     *
     * @param taskDirectory The full path to directory on local filesystem holding tasks.
     * @param taskConfigDirectory The full path to directory containing task config file(s) (XML).
     * @return 0 TaskGroup executed (see individual tasks for status).
     *         1 Error exception occurred.
     *
     * @author martinr
     *
     */
    static int run(String taskDirectory, String taskConfigDirectory) {

        ExecutorService executorService = Executors.newFixedThreadPool(200);
        int rc = 0;
        JobRunnerConfig config;
        try {
            job = new Job(LocalFileSystemTaskBuilder.create(taskDirectory,taskConfigDirectory));
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

    private static Args processCmdLine(String args[]) {
        Options options = new Options();
        Option taskConf = Option.builder("taskConf")
                .argName("DIR")
                .hasArg()
                .desc("Directory containing task specific configuration files.")
                .build();
        Option tasks = Option.builder("tasks")
                .argName("DIR")
                .hasArg()
                .desc("Directory containing tasks to be run.")
                .build();

        options.addOption("help", false, "Display help");
        options.addOption(taskConf);
        options.addOption(tasks);
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
            System.exit(0);
        }
        if (cmd.hasOption("taskConf")) {
            if (isDirectory(cmd.getOptionValue("taskConf"))) {
                System.err.println("directory " + cmd.getOptionValue("taskConf") + " does not exist.");
                System.exit(2);
            }
        } else {
            System.err.println("Task configuration directory must be specified (-taskConf)");
            System.exit(2);
        }

        if (cmd.hasOption("tasks")) {
            if (isDirectory(cmd.getOptionValue("tasks"))) {
                System.err.println("directory " + cmd.getOptionValue("tasks") + " does not exist.");
                System.exit(2);
            }
        } else {
            System.err.println("Tasks directory must be specified (-tasks)");
            System.exit(2);
        }

        return new Args(cmd.getOptionValue("tasks"),cmd.getOptionValue("taskConf"));

    }

    private static boolean fileExists(String file) {
        return !new File(file).exists();
    }

    private static boolean isDirectory(String directory) {
        return fileExists(directory) || !new File(directory).isDirectory();
    }

    private static class Args {
        final String taskDirectory;
        final String taskConfigDirectory;

        Args(String taskDirectory, String taskConfigDirectory) {
            this.taskDirectory = taskDirectory;
            this.taskConfigDirectory = taskConfigDirectory;
        }
    }

    private static Job job;
    private RunJob() {}

}
