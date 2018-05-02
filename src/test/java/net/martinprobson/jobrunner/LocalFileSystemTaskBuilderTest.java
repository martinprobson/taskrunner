package net.martinprobson.jobrunner;

import com.google.inject.Guice;
import com.google.inject.Injector;
import net.martinprobson.jobrunner.configurationservice.ConfigurationService;
import org.apache.commons.configuration2.CombinedConfiguration;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.filefilter.SuffixFileFilter;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.io.File;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;

import static org.hamcrest.CoreMatchers.startsWith;
import static org.junit.Assert.assertEquals;

public class LocalFileSystemTaskBuilderTest {

    private static Map<String,BaseTask> expectedResults = new HashMap<>();
    private static TaskFactory taskFactory;
    private static Injector injector;

    /**
     * Given a filename of the form <code>hqlfile.hql</code>,
     * attempt to find a corresponding <code>hqlfile.xml</code> file
     * in the same directory.
     *
     * @param directory - The directory to search.
     * @param hqlFile - The name of the hqlFile.
     * @return - The full pathname of the xml file, or null if not found.
     */
    private static String getConfigFile(File directory,String hqlFile) {
        String configName = directory.getAbsolutePath() +
                File.separatorChar +
                FilenameUtils.getBaseName(hqlFile) +
                ".xml";
        File configFile = new File(configName);
        if (configFile.exists() && configFile.isFile())
            return configFile.getAbsolutePath();
        else
            return null;
    }

    @BeforeClass
    public static void setUpBeforeClass() throws Exception {
        injector = Guice.createInjector(new JobRunnerModule());
        taskFactory = injector.getInstance(TaskFactory.class);
    }

    private void setUp() throws Exception {
        ConfigurationService.load(new ConfigurationService(new JobRunnerConfigurationProvider("test_global_config.xml")));
        ClassLoader classLoader = getClass().getClassLoader();
        File testDirectory = new File(classLoader.getResource("taskrunner_test1").getFile());
        String[] sqlFiles = testDirectory.list(new SuffixFileFilter(".sql"));
        for (String sqlFile: sqlFiles) {
            File file = new File(testDirectory.getAbsolutePath() + File.separatorChar + sqlFile);
            String contents = FileUtils.readFileToString(file,Charset.defaultCharset());
            String configFile = getConfigFile(testDirectory,sqlFile);
            if (configFile == null)
                //@TODO Fix
                expectedResults.put(sqlFile,taskFactory.createJDBCTask(sqlFile,contents,new CombinedConfiguration()));
            else
                expectedResults.put(sqlFile, taskFactory.createJDBCTask(sqlFile,contents,TaskBuilder.getConfig(configFile)));
        };
    }

    @Test
    public void testBuild() throws JobRunnerException,Exception {
        setUp();
        ClassLoader classLoader = getClass().getClassLoader();
        File file = new File(classLoader.getResource("taskrunner_test1").getFile());
        LocalFileSystemTaskBuilder builder = LocalFileSystemTaskBuilder.create(file.getAbsolutePath(),
                file.getAbsolutePath());
        Map<String,BaseTask> tasks = builder.build();
        assertEquals(expectedResults,tasks);
    }

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Test
    public void testInvalidTaskDir() throws JobRunnerException {
        ClassLoader classLoader = getClass().getClassLoader();
        File file = new File(classLoader.getResource("taskrunner_test1").getFile());

        thrown.expect(JobRunnerException.class);
        thrown.expectMessage(startsWith("task directory"));
        LocalFileSystemTaskBuilder.create("foo",file.getAbsolutePath());
    }

    @Test
    public void testInvalidConfigDir() throws JobRunnerException {
        ClassLoader classLoader = getClass().getClassLoader();
        File file = new File(classLoader.getResource("taskrunner_test1").getFile());

        thrown.expect(JobRunnerException.class);
        thrown.expectMessage(startsWith("config directory"));
        LocalFileSystemTaskBuilder.create(file.getAbsolutePath(),"foo");
    }

}
