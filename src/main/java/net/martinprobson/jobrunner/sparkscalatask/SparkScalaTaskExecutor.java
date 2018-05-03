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
package net.martinprobson.jobrunner.sparkscalatask;

import com.google.inject.Inject;
import net.martinprobson.jobrunner.BaseTask;
import net.martinprobson.jobrunner.JobRunnerException;
import net.martinprobson.jobrunner.TaskExecutor;
import net.martinprobson.jobrunner.TaskResult;
import net.martinprobson.jobrunner.template.TemplateService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * <p>{@code SparkScalaTaskExecutor}</p>
 *
 * <p>Responsible for executing Scala Spark script via a Spark connection.</p>
 *
 * @author martinr
 */
public class SparkScalaTaskExecutor implements TaskExecutor {
    private static final Logger log = LoggerFactory.getLogger(SparkScalaTaskExecutor.class);

    /**
     * Executes the Scala code in a {@code SparkScalaTask}.
     * The results of the execution are set on the {@code TaskResult} object within the
     * SparkScalaTask itself.
     *
     * @param task The task to execute.
     * @throws JobRunnerException on execution error.
     */
    @Override
    public void executeTask(BaseTask task) throws JobRunnerException {
        try {
            execute(task.getTemplatedTaskContents());
        } catch (JobRunnerException e) {
            task.setTaskResult(new TaskResult(TaskResult.Result.FAILED,e));
            throw e;
        }
        task.setTaskResult(new TaskResult(TaskResult.Result.SUCCESS));
    }

    private static void execute(String script) throws JobRunnerException {
        log.trace("SparkScalaTaskExecutor - executeTask - " + script);
    }
}
