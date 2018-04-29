/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
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

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * <p>{@code JDBCTaskExecutor}</p>
 *
 * <p>Responsible for executing SQL via a JDBC connection.</p>
 *
 * @author martinr
 */
public class JDBCTaskExecutor implements TaskExecutor {
    private static final Logger log = LoggerFactory.getLogger(JDBCTaskExecutor.class);
    private static StringSubstitutor substitutor;

    /**
     * <p>
     * Given a String containing one or more SQL statements (separated by ';' character),
     * split the statements and execute each one in turn against a JDBC connection supplied by a
     * {@link DBSource}
     * </p>
     *
     * @param sqlStmts String containing SQL statement(s) to be run.
     * @throws TaskRunnerException on execution error.
     */
    private static void ExecuteSqlStmts(String sqlStmts, Map<String, String> params) throws TaskRunnerException {
        if (params != null && params.size() != 0) {
            substitutor = new StringSubstitutor(params);
        }

        try (Connection conn = DBSource.get().getConnection()) {
            //@TODO Worth adding batch processing?
            boolean batchSupported = conn.getMetaData().supportsBatchUpdates();
            log.trace("DatabaseMetaData supportsBatchUpdates() = " + batchSupported);
            int i = 0;
            List<String> stmts = SQLSplit(sqlStmts);
            log.trace("String contains  " + stmts.size() + " statement(s)");
            for (String stmt : stmts) {
                log.trace("About to execute statment no: " + ++i);
                log.trace("Statement before substitution: " + stmt);
                stmt = replaceParams(stmt);
                log.trace("Statement after substitution: " + stmt);
                ExecSQL(conn,stmt);
            }
        } catch (SQLException e) {
            throw new TaskRunnerException("SQLException", e);
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

    private static String replaceParams(String line) {
        return (substitutor == null) ? line : substitutor.replace(line);
    }

    /**
     * Execute single SQL statement.
     * <p></p>
     *
     * @param conn    - JDBC DB Connection to run against.
     * @param sqlStmt - SQL statement to execute.
     * @throws SQLException If the statement caused an error.
     */
    private static void ExecSQL(Connection conn, String sqlStmt) throws SQLException {

        Kerberos.auth();
        log.trace("passed statement: " + sqlStmt);
        try (Statement stmt = conn.createStatement()) {
            log.debug("About to execute statement: " + sqlStmt);
            ResultSet rs;
            int updateCount;
            //@TODO Fix
            if (stmt.execute(sqlStmt)) {
                rs = stmt.getResultSet();
                log.debug("Resultset returned " + rs);
            } else {
                updateCount = stmt.getUpdateCount();
                log.debug("Update count returned " + updateCount);
            }
        }
    }

    /**
     * Executes the SQL in a {@code JDBCTask}. The results of the execution are set on the {@code TaskResult} object within the
     * JDBCTask itself.
     *
     * @param task The task to execute.
     * @throws TaskRunnerException on execution error.
     */
    @Override
    public void executeTask(Task task) throws TaskRunnerException {
        JDBCTask jdbcTask = (JDBCTask) task;
        try {
            ExecuteSqlStmts(jdbcTask.getSql(), Collections.emptyMap());
        } catch (TaskRunnerException e) {
            jdbcTask.setTaskResult(new TaskResult(TaskResult.Result.FAILED,e));
            throw e;
        }
        jdbcTask.setTaskResult(new TaskResult(TaskResult.Result.SUCCESS));
    }
}
