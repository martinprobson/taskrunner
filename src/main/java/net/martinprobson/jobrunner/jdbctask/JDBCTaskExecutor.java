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
package net.martinprobson.jobrunner.jdbctask;

import com.google.inject.Inject;
import net.martinprobson.jobrunner.BaseTask;
import net.martinprobson.jobrunner.JobRunnerException;
import net.martinprobson.jobrunner.TaskExecutor;
import net.martinprobson.jobrunner.TaskResult;
import net.martinprobson.jobrunner.jdbcconnection.DBSource;
import net.martinprobson.jobrunner.jdbcconnection.Kerberos;
import net.martinprobson.jobrunner.template.TemplateService;
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
public class JDBCTaskExecutor implements TaskExecutor {
    private static final Logger log = LoggerFactory.getLogger(JDBCTaskExecutor.class);

    //@TODO Temp
    @Inject
    TemplateService template;

    /**
     * <p>
     * Given a String containing one or more SQL statements (separated by ';' character),
     * split the statements and execute each one in turn against a JDBC connection supplied by a
     * {@link DBSource}
     * </p>
     *
     * @param sqlStmts String containing SQL statement(s) to be run.
     * @throws JobRunnerException on execution error.
     */
    private static void ExecuteSqlStmts(String sqlStmts) throws JobRunnerException {
        try (Connection conn = DBSource.get().getConnection()) {
            List<String> stmts = SQLSplit(sqlStmts);
            log.trace("String contains  " + stmts.size() + " statement(s)");
            for (String stmt : stmts)
                ExecSQL(conn,stmt);
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
        try (Statement stmt = conn.createStatement()) {
            log.debug("About to execute statement: " + sqlStmt);
            stmt.execute(sqlStmt);
        }
    }

    /**
     * Executes the SQL in a {@code JDBCTask}. The results of the execution are set on the {@code TaskResult} object within the
     * JDBCTask itself.
     *
     * @param task The task to execute.
     * @throws JobRunnerException on execution error.
     */
    @Override
    public void executeTask(BaseTask task) throws JobRunnerException {
        try {
            ExecuteSqlStmts(task.getTemplatedTaskContents());
        } catch (JobRunnerException e) {
            task.setTaskResult(new TaskResult(TaskResult.Result.FAILED,e));
            throw e;
        }
        task.setTaskResult(new TaskResult(TaskResult.Result.SUCCESS));
    }
}
