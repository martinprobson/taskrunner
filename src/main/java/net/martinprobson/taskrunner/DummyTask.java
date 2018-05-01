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
package net.martinprobson.taskrunner;

import com.google.inject.assistedinject.Assisted;
import com.google.inject.assistedinject.AssistedInject;
import com.google.inject.name.Named;
import net.martinprobson.taskrunner.template.TemplateService;
import org.apache.commons.configuration2.Configuration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A {@code DummyTask} A task with no content that always returns SUCCESS when executed.
 *
 * @author martinr
 */
public class DummyTask extends BaseTask {

    private static final Logger log = LoggerFactory.getLogger(DummyTask.class);

    /**
     * Construct a new DummyTask with the given id.
     * @param taskId Taskid to be created.
     */
    @AssistedInject
    private DummyTask(TemplateService templateService,
                     @Named("dummy") TaskExecutor taskExecutor,
                     @Assisted("taskid") String taskId,
                     @Assisted Configuration taskConfiguration) {
        super(taskId,"",taskConfiguration,templateService,taskExecutor);
        log.trace("Built a new DummyTask with id: " + taskId);
    }

    /**
     * Construct a new DummyTask with the given id.
     * @param taskId Taskid to be created.
     */
    @AssistedInject
    private DummyTask(TemplateService templateService,
                      @Named("dummy") TaskExecutor taskExecutor,
                      @Assisted("taskid") String taskId) {
        super(taskId,"",templateService,taskExecutor);
        log.trace("Built a new DummyTask with id: " + taskId);
    }


    /**
     * Execute the DummyTask - this always returns SUCCESS.
     */
    public TaskResult execute() {
        log.trace("About to execute: " + this.getId());
        return new TaskResult(TaskResult.Result.SUCCESS);
    }
}
