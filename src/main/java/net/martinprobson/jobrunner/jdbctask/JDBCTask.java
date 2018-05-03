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

import javax.annotation.Nullable;

/**
 * <p>{@code JDBCTask}</p>
 *
 * <p>A task that holds SQL that will be executed against a JDBC connection.</p>
 *
 * @author martinr
 */
public class JDBCTask extends BaseTask {

    private static final Logger log = LoggerFactory.getLogger(JDBCTask.class);

    /**
     *
     * Creates a JDBCTask with the specified id,sql and task configuration.
     *
     * @param id Task Id
     * @param task Sql contents
     * @param taskConfiguration  Task specification configuration XML filename.
     */
    @AssistedInject
    private JDBCTask(TemplateService templateService,
                     @Named("jdbc") TaskExecutor jdbcTaskExecutor,
                     @Assisted("id") String id,
                     @Assisted("task") String task,
                     @Assisted @Nullable Configuration taskConfiguration) {
        super(id,task,taskConfiguration,templateService,jdbcTaskExecutor);
        log.trace("Built a new JDBCTask: " + this);
    }
}
