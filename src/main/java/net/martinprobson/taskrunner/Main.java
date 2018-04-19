package net.martinprobson.taskrunner;

import net.martinprobson.taskrunner.configurationservice.ConfigurationService;
import org.apache.commons.configuration2.ImmutableConfiguration;
import org.apache.commons.configuration2.XMLConfiguration;
import org.apache.commons.configuration2.builder.FileBasedConfigurationBuilder;
import org.apache.commons.configuration2.builder.fluent.Parameters;
import org.apache.commons.configuration2.ex.ConfigurationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Main {

    private static final Logger log = LoggerFactory.getLogger(Main.class);

    private static void println(Object o) {
        System.out.println(o);
    }


    public static void main(String args[]) {
        // Configure our global configuration service.
        ConfigurationService.load(new ConfigurationService(new TaskRunnerConfigurationProvider("global_config.xml", "user_password.xml")));
        ImmutableConfiguration conf = ConfigurationService.getConfiguration();
        println(conf.getString("jdbc.username"));
/*
        TaskBuilder taskBuilder =new ManualTaskBuilder();
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
                executorService.awaitTermination(1,TimeUnit.SECONDS);
            } catch (InterruptedException e) {
                log.error("Interrupt");
            }
        }
*/


//        DummyTask task = new DummyTask("id1","test_config.xml","test_config2.xml");
//        println(task.getConf().getString("colors.background"));
//        println(task.getConf().getString("template.queue"));
//        println(task.getConf().getString("template.tables.ever"));
//        List<Object> buttons = task.getConf().getList("buttons.name");
//        List<Object> depends = task.getConf().getList("depends-on.id");
////        HierarchicalConfiguration<ImmutableNode> sub =  task.getConf().configurationAt("template");
////        println(sub.getString("tables.ever"));
//        println(buttons);
//        println(depends);
////        println("Template keys");
////        Map root = new HashMap<>();
////        for (String s: iterable(sub.getKeys())) {
////            root.put(s,sub.getString(s));
////            log.debug("Adding key: [" + s + "] with value: [" + sub.getString(s) + "]" +
////                    "" +
////                    "" +
////                    "" +
////                    "" +
////                    " to template map");
////        }
////        log.info("Done");
////
////        Configuration cfg = new Configuration(VERSION_2_3_28);
////        cfg.setClassLoaderForTemplateLoading(Main.class.getClassLoader(), ".");
////        cfg.setDefaultEncoding("UTF-8");
////        cfg.setTemplateExceptionHandler(TemplateExceptionHandler.DEBUG_HANDLER);
////        cfg.setInterpolationSyntax(Configuration.SQUARE_BRACKET_INTERPOLATION_SYNTAX);
////        cfg.setTagSyntax(Configuration.SQUARE_BRACKET_TAG_SYNTAX);
////
////        Template temp = cfg.getTemplate("01_reading_candidates.ftl");
////        Writer out = new OutputStreamWriter(System.out);
////        try {
////            temp.process(root, out);
////        } catch (TemplateException e) {
////            log.error("Error reading XML", e);
////            System.exit(2);
//        }


    }

    private static XMLConfiguration getConfig(String fileName) {
        XMLConfiguration config = null;
        FileBasedConfigurationBuilder<XMLConfiguration> builder =
                new FileBasedConfigurationBuilder<>(XMLConfiguration.class)
                        .configure(new Parameters().xml().setFileName(fileName));
        try {
            config = builder.getConfiguration();
        } catch (ConfigurationException cex) {
            log.error("Loading of configuration failed", cex);
            System.exit(2);
        }
        log.debug("Loaded configuration file: " + fileName);
        return config;
    }
}
