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

import com.google.inject.assistedinject.Assisted;
import com.google.inject.assistedinject.AssistedInject;
import com.google.inject.name.Named;
import net.martinprobson.jobrunner.*;
import net.martinprobson.jobrunner.template.TemplateService;
import org.apache.commons.configuration2.Configuration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * <p>{@code JDBCTask}</p>
 *
 * <p>A task that hold SQL that will be executed against a JDBC connection.</p>
 *
 * @author martinr
 */
public class JDBCTask extends BaseTask {

    private static final Logger log = LoggerFactory.getLogger(JDBCTask.class);
    private static TaskExecutor taskExecutor = null;

    /**
     *
     * Creates a JDBCTask with the specified id,sql and task configuration.
     *
     * @param taskId             Task Id
     * @param content            Sql contents
     * @param taskConfiguration  Task specification configuration XML filename.
     */
    @AssistedInject
    private JDBCTask(TemplateService templateService,
                     @Named("jdbc") TaskExecutor jdbcTaskExecutor,
                     @Assisted("taskid") String taskId,
                     @Assisted("content") String content,
                     @Assisted Configuration taskConfiguration) {
        super(taskId,content,taskConfiguration,templateService,jdbcTaskExecutor);
        log.trace("Built a new JDBCTask with id: " + taskId);
    }

    /**
     *
     * Creates a JDBCTask with the specified id,sql and task configuration.
     *
     * @param taskId             Task Id
     * @param content            Sql contents
     */
    @AssistedInject
    private JDBCTask(TemplateService templateService,
                     @Named("jdbc") TaskExecutor jdbcTaskExecutor,
                     @Assisted("taskid") String taskId,
                     @Assisted("content") String content) {
        super(taskId,content,templateService,jdbcTaskExecutor);
        log.trace("Built a new JDBCTask with id: " + taskId);
    }


    /**
     * @return The sql that this task holds.
     */
    public String getSql() {
        return getTask();
    }

}
