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
package net.martinprobson.jobrunner.sparkpythontask;

import net.martinprobson.jobrunner.TaskProvider;
import net.martinprobson.jobrunner.TaskResult;
import net.martinprobson.jobrunner.common.BaseTask;
import net.martinprobson.jobrunner.common.JobRunnerException;
import net.martinprobson.jobrunner.common.TaskExecutor;
import org.apache.commons.exec.*;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;

/**
 * <p>{@code SparkPythonTaskExecutor}</p>
 *
 * <p>Responsible for executing Python Spark code via a Spark connection.</p>
 *
 * @author martinr
 */
class SparkPythonTaskExecutor implements TaskExecutor {
    private static final Logger log = LoggerFactory.getLogger(SparkPythonTaskExecutor.class);

    /**
     * Executes the Python code in a {@code SparkPythonTask}.
     * The results of the execution are set on the {@code TaskResult} object within the
     * SparkPythonTask itself.
     *
     * @param task The task to execute.
     * @throws JobRunnerException on execution error.
     */
    @Override
    public void executeTask(BaseTask task) throws JobRunnerException {
        try {
            execute(task);
        } catch (JobRunnerException e) {
            task.setTaskResult(new TaskResult(TaskResult.Result.FAILED,e));
            throw e;
        }
        task.setTaskResult(new TaskResult(TaskResult.Result.SUCCESS));
    }

    private static File createTempFile(String script) throws JobRunnerException {
        File temp;
        try {
            temp = File.createTempFile("sparkpython",".py");
            FileUtils.write(temp,script + "\n",Charset.defaultCharset());
        } catch (IOException e) {
            throw new JobRunnerException("Error creating temp file",e);
        }
        temp.deleteOnExit();
        return temp;
    }

    private static void checkSparkEnv() throws JobRunnerException {
        if (System.getenv("SPARK_HOME") == null) {
            StringBuilder msg = new StringBuilder("Environment variable SPARK_HOME is not set.");
            throw new JobRunnerException(msg.toString());
        }
    }

    private void execute(BaseTask task) throws JobRunnerException {
        log.trace("SparkPythonTaskExecutor - executeTask");
        checkSparkEnv();
        Map map = new HashMap();
        map.put("file", createTempFile(task.getRenderedTaskContents()));
        CommandLine cmdLine = CommandLine.parse(System.getenv("SPARK_HOME") + File.separatorChar + "bin" + File.separatorChar + "spark-submit");
        cmdLine.setSubstitutionMap(map);
        cmdLine.addArgument("--master").addArgument(task.getConfig().getString("spark-python.master"));
        cmdLine.addArgument("--num-executors").addArgument(task.getConfig().getString("spark-python.num-executors"));
        cmdLine.addArgument("--queue").addArgument(task.getConfig().getString("spark-python.queue"));
        cmdLine.addArgument("${file}");
        DefaultExecutor executor = new DefaultExecutor();
        executor.setExitValue(0);
        ExecuteWatchdog watchdog = new ExecuteWatchdog(60000);
        executor.setWatchdog(watchdog);
        int exitValue;
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        ByteArrayOutputStream error = new ByteArrayOutputStream();
        try {
            log.trace("About to execute: " + cmdLine);
            PumpStreamHandler streamHandler = new PumpStreamHandler(output,error);

            executor.setStreamHandler(streamHandler);
            exitValue = executor.execute(cmdLine);
        } catch (IOException e) {
            throw new JobRunnerException("Execute error: stdout = [" + output + "] stderr = [" + error + "]");
        }
        if (exitValue != 0)
            throw new JobRunnerException("Non-zero exit value: stdout = [" + output + "] stderr = [" + error + "]");
        else
            log.trace("Command ouput: " + output);
    }

    public static void main(String args[]) throws JobRunnerException {
        SparkPythonTaskExecutor spte = new SparkPythonTaskExecutor();
        TaskProvider taskProvider = TaskProvider.getInstance();
        BaseTask task = taskProvider.createTask("spark-python","test","print 'Hello'");
        task.execute();
    }
}
