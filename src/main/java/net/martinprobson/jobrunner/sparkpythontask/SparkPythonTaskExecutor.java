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

import net.martinprobson.jobrunner.common.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * <p>{@code SparkPythonTaskExecutor}</p>
 *
 * <p>Responsible for executing Python Spark code via a Spark connection.</p>
 * <p>Code is executed via {@code spark-submit}</p>
 *
 * @author martinr
 */
class SparkPythonTaskExecutor extends AbstractExternalCmdExecutor implements TaskExecutor {

    /**
     * <p>Check the environment is capable of executing the command.</p>
     * <p>For example, do the required environment variables exist? etc</p>
     *
     * @throws JobRunnerException If there is an issue with the environment.
     */
    @Override
    public void checkEnv(BaseTask task) throws JobRunnerException {
        checkEnv(task.getConfig().getStringList("spark-python.environment"));
    }

    /**
     * <p>Get the timeout interval in milliseconds.</p>
     */
    @Override
    protected long getTimeOutMs(BaseTask task) {
        return task.getConfig().getLong("spark-python.timeoutms");
    }

    /**
     * <p>Get the command to run.</p>
     */
    protected String getCmd() {
        return System.getenv("SPARK_HOME") +
                File.separatorChar +
                "bin" +
                File.separatorChar +
                "spark-submit";
    }

    /**
     * <p>Get the command arguments.</p>
     */
    @Override
    protected String[] getArgs(BaseTask task) throws JobRunnerException {
        List<String> args = new ArrayList<>();
        args.add("--master");
        args.add(task.getConfig().getString("spark-python.master"));
        args.add("--num-executors");
        args.add(task.getConfig().getString("spark-python.num-executors"));
        args.add("--queue");
        args.add(task.getConfig().getString("spark-python.queue"));
        if (!task.getConfig().getIsNull("spark-python.driver-java-options")) {
            args.add("--driver-java-options");
            args.add(task.getConfig().getString("spark-python.driver-java-options"));
        }
        args.add(super.createTempFile(task).getAbsolutePath());
        return args.toArray(new String[0]);
    }

    /**
     * <p>Get temp file name prefix</p>
     */
    @Override
    protected String getTempFilePrefix() { return "spark-python";}

    /**
     * <p>Get temp file name suffix</p>
     */
    @Override
    protected String getTempFileSuffix() { return ".py";}

    private static final Logger log = LoggerFactory.getLogger(SparkPythonTaskExecutor.class);

}
