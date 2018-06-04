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
package net.martinprobson.jobrunner;

import com.github.dexecutor.core.DefaultDexecutor;
import com.github.dexecutor.core.ExecutionConfig;
import com.github.dexecutor.core.task.ExecutionResults;
import net.martinprobson.jobrunner.common.BaseTask;
import net.martinprobson.jobrunner.common.JobRunnerException;
import net.martinprobson.jobrunner.monitor.SimpleMonitor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class JobRunner extends DefaultDexecutor<String, TaskResult> {

    public JobRunner(final JobRunnerConfig config) throws JobRunnerException {
        super(config);
        this.config = config;
        setDependencies();
    }

    @Override
    public ExecutionResults<String, TaskResult> execute(final ExecutionConfig ExecutionConfig) {
        SimpleMonitor monitor = SimpleMonitor.getInstance(config.getJob());
        monitor.start();
        ExecutionResults<String,TaskResult> results = super.execute(ExecutionConfig);
        monitor.stop();
        return results;
    }


    private void setDependencies() throws JobRunnerException {
        for (BaseTask task : config.getJob()) {
            for (String dep : task.getDependencies()) {
                if (config.getJob().hasId(dep)) {
                    if (task.getId().equals(dep))
                        throw new JobRunnerException(task.getId() + " - setDependencies: A task cannot be dependent on itself");
                    this.addDependency(dep, task.getId());
                    log.trace(task.getId() + " depends on " + dep);
                } else
                    throw new JobRunnerException(task.getId() + " - setDependencies: There is no Task with an id of: " + dep);
            }
            if (task.getDependencies().size() == 0) this.addIndependent(task.getId());
        }
    }

    private static final Logger log = LoggerFactory.getLogger(JobRunner.class);
    private final JobRunnerConfig config;
}
