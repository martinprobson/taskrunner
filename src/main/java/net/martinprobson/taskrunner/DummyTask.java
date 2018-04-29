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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A {@code DummyTask} A task that always returns SUCCESS when executed.
 *
 * @author martinr
 */
public class DummyTask extends DependentTask {

    private static final Logger log = LoggerFactory.getLogger(DummyTask.class);

    /**
     * Construct a new DummyTask with the given id.
     * @param id Taskid
     */
    public DummyTask(String id) {
        super(id,"DUMMY");
    }

    /**
     * Construct a new DummyTask with the given id and task specific configuration.
     * @param id Taskid
     * @param taskConfiguration Task specific configuration (XML file).
     */
    public DummyTask(String id, String taskConfiguration) {
        super(id,"DUMMY",taskConfiguration);
    }

    /**
     * Execute the DummyTask - this always returns SUCCESS.
     */
    public TaskResult execute() {
        log.trace("About to execute: " + this.getId());
        return new TaskResult(TaskResult.Result.SUCCESS);
    }
}
