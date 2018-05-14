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
package net.martinprobson.jobrunner.hivetask;

import net.martinprobson.jobrunner.common.AbstractExternalCmdExecutor;
import net.martinprobson.jobrunner.common.BaseTask;
import net.martinprobson.jobrunner.common.JobRunnerException;
import net.martinprobson.jobrunner.common.TaskExecutor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;

/**
 * <p>{@code HiveTaskExecutor}</p>
 *
 * <p>Responsible for executing Hive QL script via Hive cli.</p>
 *
 * @author martinr
 */
class HiveTaskExecutor extends AbstractExternalCmdExecutor implements TaskExecutor {

    /**
     * <p>Check the environment is capable of executing the command.</p>
     * <p>For example, do the required environment variables exist? etc</p>
     * @throws JobRunnerException If there is an issue with the environment.
     */
    @Override
    protected void checkEnv() throws JobRunnerException {
        if (System.getenv("HIVE_HOME") == null) {
            StringBuilder msg = new StringBuilder("Environment variable HIVE_HOME is not set.");
            throw new JobRunnerException(msg.toString());
        }
    }

    /**
     * <p>Get the timeout interval in milliseconds.</p>
     * @throws JobRunnerException If there is an issue with the environment.
     */
    @Override
    protected long getTimeOutMs(BaseTask task) {
        return task.getConfig().getLong("hive.timeoutms");
    }

    /**
     * <p>Get the command to run.</p>
     */
    protected String getCmd() {
        return System.getenv("HIVE_HOME") +
                File.separatorChar +
                "bin" +
                File.separatorChar +
                "hive";
    }

    /**
     * <p>Get the command arguments.</p>
     * @throws JobRunnerException If there is an issue with the environment.
     */
    @Override
    protected String[] getArgs(BaseTask task) throws JobRunnerException {
        return new String[]{"-f", super.createTempFile(task).getAbsolutePath()};
    }

    /**
     * <p>Get temp file name prefix</p>
     */
    @Override
    protected String getTempFilePrefix() { return "hive";}

    /**
     * <p>Get temp file name suffix</p>
     */
    @Override
    protected String getTempFileSuffix() { return ".hql";}

    private static final Logger log = LoggerFactory.getLogger(HiveTaskExecutor.class);
}
