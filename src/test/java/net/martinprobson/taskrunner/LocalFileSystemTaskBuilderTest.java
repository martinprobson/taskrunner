package net.martinprobson.taskrunner;

import net.martinprobson.taskrunner.configurationservice.ConfigurationService;
import net.martinprobson.taskrunner.jdbctask.JDBCTask;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.filefilter.SuffixFileFilter;
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

    private static Map<String,DependentTask> expectedResults = new HashMap<>();

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

    private void setUp() throws Exception {
        ConfigurationService.load(new ConfigurationService(new TaskRunnerConfigurationProvider("test_global_config.xml")));
        ClassLoader classLoader = getClass().getClassLoader();
        File testDirectory = new File(classLoader.getResource("taskrunner_test1").getFile());
        String[] sqlFiles = testDirectory.list(new SuffixFileFilter(".sql"));
        for (String sqlFile: sqlFiles) {
            File file = new File(testDirectory.getAbsolutePath() + File.separatorChar + sqlFile);
            String contents = FileUtils.readFileToString(file,Charset.defaultCharset());
            String configFile = getConfigFile(testDirectory,sqlFile);
            if (configFile == null)
                expectedResults.put(sqlFile,new JDBCTask(sqlFile,contents));
            else
                expectedResults.put(sqlFile,new JDBCTask(sqlFile,contents,configFile));
        };
    }

    @Test
    public void testBuild() throws TaskRunnerException,Exception {
        setUp();
        ClassLoader classLoader = getClass().getClassLoader();
        File file = new File(classLoader.getResource("taskrunner_test1").getFile());
        LocalFileSystemBuilder builder = new LocalFileSystemBuilder(file.getAbsolutePath());
        Map<String,DependentTask> tasks = builder.build();
        assertEquals(expectedResults,tasks);
    }

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Test
    public void testInvalidDir() throws TaskRunnerException {
        thrown.expect(TaskRunnerException.class);
        thrown.expectMessage(startsWith("LocalFileSystemBuilder: directory foo"));

        new LocalFileSystemBuilder("foo");

    }
}
