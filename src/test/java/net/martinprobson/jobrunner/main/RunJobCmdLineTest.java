package net.martinprobson.jobrunner.main;

import org.junit.Rule;
import org.junit.Test;
import org.junit.contrib.java.lang.system.ExpectedSystemExit;
import org.junit.contrib.java.lang.system.SystemErrRule;
import org.junit.contrib.java.lang.system.SystemOutRule;

import java.nio.file.Paths;

import static org.hamcrest.CoreMatchers.containsString;
import static org.junit.Assert.*;

public class RunJobCmdLineTest {

    @Rule
    public final SystemOutRule systemOutRule = new SystemOutRule().enableLog().muteForSuccessfulTests();

    @Rule
    public final SystemErrRule systemErrRule = new SystemErrRule().enableLog().muteForSuccessfulTests();

    @Rule
    public final ExpectedSystemExit exit = ExpectedSystemExit.none();


    @Test
    public void testHelpOption() {
        exit.expectSystemExitWithStatus(2);
        exit.checkAssertionAfterwards(() -> {
            assertThat(systemOutRule.getLog(),containsString("-conf"));
            assertThat(systemOutRule.getLog(),containsString("-tasks"));
            assertThat(systemOutRule.getLog(),containsString("-render"));
            assertThat(systemOutRule.getLog(),containsString("-help"));
            assertThat(systemOutRule.getLog(),containsString("usage: runjob"));
        });
        String[] args = {"-help"};
        RunJob.main(args);
    }

    @Test
    public void testInvalidTask() {
        exit.expectSystemExitWithStatus(2);
        exit.checkAssertionAfterwards(() -> {
            assertThat(systemErrRule.getLog(),containsString("directory foo does not exist"));
            assertEquals(systemOutRule.getLog(),"");
        });
        String test1 = Paths.get("src","test","resources","taskrunner_test1").toFile().getAbsolutePath();

        String[] args = {"-conf",test1,"-tasks","foo"};
        RunJob.main(args);
    }

    @Test
    public void testInvalidConf() {
        exit.expectSystemExitWithStatus(2);
        exit.checkAssertionAfterwards(() -> {
            assertThat(systemErrRule.getLog(),containsString("directory foo does not exist"));
            assertEquals(systemOutRule.getLog(),"");
        });
        String test1 = Paths.get("src","test","resources","runjob_test1","tasks").toFile().getAbsolutePath();

        String[] args = {"-conf","foo","-tasks",test1};
        RunJob.main(args);
    }

     @Test
    public void testInvalidOpt() {
        exit.expectSystemExitWithStatus(2);
        exit.checkAssertionAfterwards(() -> {
            assertThat(systemErrRule.getLog(),containsString("Unrecognized option: -foo"));
            assertThat(systemOutRule.getLog(),containsString("-conf"));
            assertThat(systemOutRule.getLog(),containsString("-tasks"));
            assertThat(systemOutRule.getLog(),containsString("-help"));
            assertThat(systemOutRule.getLog(),containsString("usage: runjob"));
        });

        String[] args = {"-foo","foo"};
        RunJob.main(args);
    }


}