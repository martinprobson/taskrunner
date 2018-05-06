package net.martinprobson.jobrunner.template;

import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import org.apache.commons.io.FileUtils;
import org.junit.*;
import org.junit.rules.ExpectedException;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Objects;

import static org.hamcrest.CoreMatchers.startsWith;
import static org.junit.Assert.*;

public class FreeMarkerTemplateServiceTest {
    private static String testDir;

    @BeforeClass
    public static void setUpBeforeClass() throws Exception {
        testDir = new File(Objects.requireNonNull(FreeMarkerTemplateServiceTest.class.getClassLoader()
                .getResource("templateTest")).getFile()).getAbsolutePath();
    }

    @Test
    public void applyEmptyConfig() throws IOException {

        String expected = FileUtils.readFileToString(
                new File(testDir + File.separatorChar + "templateTest1.txt"),
                Charset.defaultCharset());
        String content = expected;
        FreeMarkerTemplateService service = new FreeMarkerTemplateService();
        String actual = null;
        try {
            actual = service.apply("templateTest1.txt",content,ConfigFactory.empty());
        } catch (TemplateException e) {
            fail("Unexpected exception ");
        }
        assertEquals(expected,actual);
    }

    @Test
    public void applywillAllConfig() throws IOException {
        String testCase = readFile(testDir,"templateTest2.txt");
        String expected = readFile(testDir,"templateTest2.expected");
        FreeMarkerTemplateService service = new FreeMarkerTemplateService();
        String actual = null;
        Config config = ConfigFactory.load("freemarkertest1");
        try {
            actual = service.apply("templateTest2.txt",testCase,config);
        } catch (TemplateException e) {
            fail("Unexpected exception ");
        }
        assertEquals(expected,actual);
    }

    @Rule
    public ExpectedException thrown = ExpectedException.none();


    @Test
    public void applyMissingConfig() throws TemplateException,IOException {
        String testCase = readFile(testDir,"templateTest2.txt");
        FreeMarkerTemplateService service = new FreeMarkerTemplateService();
        thrown.expect(TemplateException.class);
        thrown.expectMessage(startsWith("TemplateException"));

        String actual = null;
        Config config = ConfigFactory.load("freemarkertest2");
        thrown.expect(TemplateException.class);
        actual = service.apply("templateTest2.txt",testCase,config);
        fail("Expected exception");
    }

    private static String readFile(String dir, String fileName) throws IOException {
        String filePath = testDir + File.separatorChar + fileName;
        return FileUtils.readFileToString(new File(filePath), Charset.defaultCharset());
    }
}