package net.martinprobson.jobrunner.main;

import com.github.dexecutor.core.ExecutionConfig;
import net.martinprobson.jobrunner.*;
import net.martinprobson.jobrunner.configurationservice.ConfigurationService;
import org.apache.commons.configuration2.Configuration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import freemarker.template.*;

import java.io.File;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static freemarker.template.Configuration.VERSION_2_3_28;

public class Main {
    private static final Logger log = LoggerFactory.getLogger(Main.class);

    public static void main(String args[]) {
        templateTest();
        System.exit(0);
        /* 1. Setup out global configuration. */
        ConfigurationService.load(new ConfigurationService(new JobRunnerConfigurationProvider("reference_config.xml")));
        JobRunnerConfig config = null;
        String testDir = new File(Objects.requireNonNull(Main.class.getClassLoader().getResource("example2")).getFile()).getAbsolutePath();
        /* 2. Setup an executor service. */
        ExecutorService executorService = Executors.newFixedThreadPool(200);
//        ExecutorService executorService = Executors.newFixedThreadPool(1);
        Job job;
        try {
            /* 3. Setup a Task Group that will hold our tasks (via a LocalFileSystemTaskBuilder) */
            job = new Job(LocalFileSystemTaskBuilder.create(testDir,testDir));
            /* 4. Pass the executor and Job to our JobRunnerConfig. */
            config = new JobRunnerConfig(executorService, job);
            /* 5. Build a JobRunner with the config */
            JobRunner jobRunner = new JobRunner(config);
            /* 6. Execute out tasks */
            //@TODO Launch a monitoring thread? Looping arounf Task status.
            jobRunner.execute(ExecutionConfig.NON_TERMINATING);
            //@TODO Shutdown monitor thread.

        } catch (JobRunnerException e) {
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

    public static void templateTest(){

        ConfigurationService.load(new ConfigurationService(new JobRunnerConfigurationProvider("reference_config.xml")));
        JobRunnerConfig config = null;
        Job job = null;
        String testDir = new File(Objects.requireNonNull(Main.class.getClassLoader().getResource("templateTest")).getFile()).getAbsolutePath();
        try {
            job = new Job(LocalFileSystemTaskBuilder.create(testDir,testDir));
        } catch (JobRunnerException e) {
            log.error("Unexpected Exception: " + e);
            System.exit(2);
        }
        BaseTask task = job.provideTask("01_reading_candidates.sql");
        Configuration sub =  task.getConfiguration().subset("template");
        System.out.println(sub);
        Iterator<String> i = sub.getKeys();
        Map root = new HashMap<>();
        while (i.hasNext()) {
            String key = i.next();
            root.put(key,sub.getString(key));
            System.out.println(key);
        }
        freemarker.template.Configuration cfg = new freemarker.template.Configuration(VERSION_2_3_28);
        cfg.setClassLoaderForTemplateLoading(Main.class.getClassLoader(), "templateTest");
        cfg.setDefaultEncoding("UTF-8");
        cfg.setTemplateExceptionHandler(TemplateExceptionHandler.DEBUG_HANDLER);
        cfg.setInterpolationSyntax(freemarker.template.Configuration.SQUARE_BRACKET_INTERPOLATION_SYNTAX);
        cfg.setTagSyntax(freemarker.template.Configuration.SQUARE_BRACKET_TAG_SYNTAX);
        try {
            Template temp = new Template("01_reading_candidates.sql",task.getTask(),cfg);
            Writer out = new OutputStreamWriter(System.out);
            temp.process(root, out);
        } catch (TemplateException e) {
            log.error("Error reading XML", e);
            System.exit(2);
        } catch (IOException e) {
            e.printStackTrace();
        }


    }
////        Configuration cfg = new Configuration(VERSION_2_3_28);
////        cfg.setClassLoaderForTemplateLoading(Main.class.getClassLoader(), ".");
////        cfg.setDefaultEncoding("UTF-8");
////        cfg.setTemplateExceptionHandler(TemplateExceptionHandler.DEBUG_HANDLER);
////        cfg.setInterpolationSyntax(Configuration.SQUARE_BRACKET_INTERPOLATION_SYNTAX);
////        cfg.setTagSyntax(Configuration.SQUARE_BRACKET_TAG_SYNTAX);
////
////        Template temp = cfg.getTemplate("01_reading_candidates.sql");
////        Writer out = new OutputStreamWriter(System.out);
////        try {
////            temp.process(root, out);
////        } catch (TemplateException e) {
////            log.error("Error reading XML", e);
////            System.exit(2);
//        }



}
