package net.martinprobson.jobrunner.jdbctask;

import net.martinprobson.jobrunner.auth.Kerberos;
import net.martinprobson.jobrunner.common.BaseTask;
import net.martinprobson.jobrunner.common.JobRunnerException;
import net.martinprobson.jobrunner.common.TaskExecutor;
import net.martinprobson.jobrunner.TaskResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * <p>{@code JDBCTaskExecutor}</p>
 *
 * <p>Responsible for executing SQL via a JDBC connection.</p>
 *
 * @author martinr
 */
class JDBCTaskExecutor implements TaskExecutor {

    /**
     * Executes the SQL in a {@code JDBCTask}. The results of the execution are set on the {@code TaskResult} object within the
     * JDBCTask itself.
     *
     * @param task The task to execute.
     * @throws JobRunnerException on execution error.
     */
    @Override
    public TaskResult executeTask(BaseTask task) throws JobRunnerException {
        try {
            ExecuteSqlStmts(task.getRenderedTaskContents());
        } catch (JobRunnerException e) {
            task.setTaskResult(new TaskResult.Builder(TaskResult.Result.FAILED).exception(e).build());
            throw e;
        }
        return task.setTaskResult(new TaskResult.Builder(TaskResult.Result.SUCCESS).build());
    }

    /**
     * <p>
     * Given a String containing one or more SQL statements (separated by ';' character),
     * split the statements and execute each one in turn against a JDBC connection supplied by a
     * {@link DBSource}
     * </p>
     *
     * @param script String containing SQL statement(s) to be run.
     * @throws JobRunnerException on execution error.
     */
    private static void ExecuteSqlStmts(String script) throws JobRunnerException {
        try (Connection conn = DBSource.get().getConnection()) {
            Kerberos.auth();
            List<String> sqlStmts = SQLSplit(script);
            log.trace("String contains  " + sqlStmts.size() + " statement(s)");
            try (Statement stmt = conn.createStatement()) {
                for (String sql : sqlStmts) {
                    log.debug("About to execute statement: " + sql);
                    stmt.execute(sql);
                }
            }
        } catch (SQLException e) {
            throw new JobRunnerException("SQLException", e);
        }
    }

    /**
     * Split a string into separate SQL statements.
     *
     * @param sql - String containing SQL
     * @return a list of SQL statements
     */
    private static List<String> SQLSplit(String sql) {
        String str = stripComments(sql);
        List<String> stmts = new ArrayList<>();
        boolean in_sQuote = false, in_dQuote = false;
        StringBuilder stmt = new StringBuilder();

        for (final char c : str.toCharArray()) {
            if (c == '\'')
                in_sQuote = !in_sQuote;
            if (c == '\"')
                in_dQuote = !in_dQuote;
            if (c == ';') {
                if (!in_dQuote && !in_sQuote) {
                    stmts.add(stmt.toString());
                    stmt = new StringBuilder();
                    continue;
                }
            }
            stmt.append(c);
        }
        return stmts;
    }

    private static String stripComments(String str) {
        StringBuilder result = new StringBuilder();
        Pattern p = Pattern.compile("--");
        for (String s : str.split(System.getProperty("line.separator"))) {
            Matcher m = p.matcher(s);
            if (m.find()) s = s.substring(0, m.start());
            if (s.isEmpty()) continue;
            result.append(s).append(" ");
        }
        return result.toString();
    }

    private static final Logger log = LoggerFactory.getLogger(JDBCTaskExecutor.class);
}
