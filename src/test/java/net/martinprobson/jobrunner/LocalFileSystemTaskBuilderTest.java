package net.martinprobson.jobrunner;

import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import net.martinprobson.jobrunner.common.BaseTask;
import net.martinprobson.jobrunner.common.JobRunnerException;
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
    private static TaskProvider taskProvider;

    /**
     * <p>Given a filename of the form {@code file.<ext>}
     * attempt to find a corresponding <code>file.conf</code> file
     * in the same directory.</p>
     * <p>If found, then load the task specific configuration from
     * the file.</p>
     * <p>If not found, just return an empty configuration.</p>
     *
     * @param directory - The directory to search.
     * @param file      - The name of the file.
     * @return - A {@code Config}.
     */
    private static Config getTaskConfiguration(File directory, String file) {
        String configName = directory.getAbsolutePath() +
                File.separatorChar +
                FilenameUtils.getBaseName(file) +
                ".conf";
        File configFile = new File(configName);
        if (configFile.exists() && configFile.isFile())
            return ConfigFactory.parseFile(configFile);
        else
            return ConfigFactory.empty();
    }


    @BeforeClass
    public static void setUpBeforeClass() throws Exception {
        taskProvider = TaskProvider.getInstance();
    }

    private void setUp() throws Exception {
        ClassLoader classLoader = getClass().getClassLoader();
        File testDirectory = new File(classLoader.getResource("taskrunner_test1").getFile());
        String[] sqlFiles = testDirectory.list(new SuffixFileFilter(".sql"));
        for (String sqlFile: sqlFiles) {
            File file = new File(testDirectory.getAbsolutePath() + File.separatorChar + sqlFile);
            Config config = getTaskConfiguration(testDirectory,sqlFile);
            expectedResults.put(sqlFile, taskProvider.createTask("jdbc",sqlFile,file,config));
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
