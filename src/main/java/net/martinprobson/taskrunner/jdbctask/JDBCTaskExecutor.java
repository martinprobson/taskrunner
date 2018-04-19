package net.martinprobson.taskrunner.jdbctask;

import com.github.dexecutor.core.task.Task;
import net.martinprobson.taskrunner.TaskExecutor;
import net.martinprobson.taskrunner.TaskResult;
import net.martinprobson.taskrunner.TaskRunnerException;
import net.martinprobson.taskrunner.taskrunner.jdbcconnection.DBSource;
import net.martinprobson.taskrunner.taskrunner.jdbcconnection.Kerberos;
import org.apache.commons.text.StringSubstitutor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class JDBCTaskExecutor extends TaskExecutor {
    private static final Logger log = LoggerFactory.getLogger(JDBCTaskExecutor.class);
    private static StringSubstitutor substitutor;

    /**
     * Given a String containing one or more jdbctask statements (separated by ';' character),
     * split the statements and execute each one in turn.
     * <p>
     *
     * @param hqlStmts String containing jdbctask statement(s) to be run.
     * @return returns <code>true</code> if jdbctask file successfully executed, <code>false</code> otherwise.
     */
    private static boolean ExecuteHqlStmts(String hqlStmts, Map<String, String> params) throws TaskRunnerException {
        if (params != null && params.size() != 0) {
            substitutor = new StringSubstitutor(params);
        }
        Connection conn = null;
        boolean rc = true;

        try {
            conn = DBSource.setupDataSource().getConnection();
            int i = 0;
            List<String> stmts = HQLSplit(hqlStmts);
            log.trace("String contains a string" + stmts.size() + " statements");
            for (String stmt : stmts) {
                log.trace("About to execute statment no: " + ++i);
                log.trace("Statement before substitution: " + stmt);
                stmt = replaceParams(stmt);
                log.trace("Statement after substitution: " + stmt);
                if (!ExecHQL(conn, stmt)) {
                    log.error("Statement number: " + i);
                    log.error("HQL statement: " + stmt + " failed");
                    log.trace("Skipping rest of String");
                    rc = false;
                    break;
                } else {
                    log.trace("Statement number: " + i + " success");
                }
            }
        } catch (SQLException e) {
            log.error("SQLException:");
            while (e != null) {
                log.error("SQLException:", e);
                log.error("SQLState: " + e.getSQLState());
                log.error("Message: " + e.getMessage());
                log.error("Vendor: " + e.getErrorCode());
                e = e.getNextException();
            }
            throw new TaskRunnerException("SQLException");
        } finally {
            try {
                if (conn != null) conn.close();
            } catch (Exception e) {
                log.error("Error on conn close", e);
                throw new TaskRunnerException("Error on conn close");
            }
        }
        return rc;
    }

    /**
     * Split a string into separate HQL statements.
     *
     * @param hql - String containing HQL
     * @return a list of HQL statements
     */
    private static List<String> HQLSplit(String hql) {
        String str = stripComments(hql);
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
            if (m.find()) {
                s = s.substring(0, m.start());
            }
            if (s.isEmpty()) continue;
            result.append(s).append(" ");
        }
        return result.toString();
    }

    private static String replaceParams(String line) {
        if (substitutor == null) {
            return line;
        }
        return substitutor.replace(line);
    }

    /**
     * Execute single HQL statement.
     * <p>
     *
     * @param conn    - DB Connection to run against.
     * @param hqlStmt - HQL statement to execute.
     * @return returns <code>true</code> if jdbctask file successfully executed, <code>false</code> otherwise.
     */
    private static boolean ExecHQL(Connection conn, String hqlStmt) {

        boolean rc = true;
        Kerberos.auth();
        log.trace("passed statement: " + hqlStmt);
        Statement stmt = null;
        try {
            if (conn == null) {
                DataSource dataSource = DBSource.setupDataSource();
                conn = dataSource.getConnection();
            }
            stmt = conn.createStatement();
            log.debug("About to execute statement: " + hqlStmt);
            stmt.execute(hqlStmt);

        } catch (SQLException e) {
            log.error("SQLException:");
            while (e != null) {
                log.error("SQLException:", e);
                log.error("SQLState: " + e.getSQLState());
                log.error("Message: " + e.getMessage());
                log.error("Vendor: " + e.getErrorCode());
                e = e.getNextException();
            }
            rc = false;

        } finally {
            try {
                if (stmt != null) stmt.close();
            } catch (Exception e) {
                log.error("Error on stmt close");
                rc = false;
            }

        }
        return rc;
    }

    @Override
    public TaskResult executeTask(Task task) throws TaskRunnerException {
        JDBCTask JDBCTask = (JDBCTask) task;
        TaskResult.Result r;
        boolean rc = ExecuteHqlStmts(JDBCTask.getHql(), Collections.emptyMap());
        if (rc)
            r = TaskResult.Result.SUCCESS;
        else
            r = TaskResult.Result.FAILURE;
        return new TaskResult(r);
    }

}
