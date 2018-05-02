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

import com.google.inject.assistedinject.Assisted;
import com.google.inject.assistedinject.AssistedInject;
import com.google.inject.name.Named;
import net.martinprobson.jobrunner.BaseTask;
import net.martinprobson.jobrunner.TaskExecutor;
import net.martinprobson.jobrunner.template.TemplateService;
import org.apache.commons.configuration2.Configuration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * <p>{@code SparkScalaTask}</p>
 *
 * <p>A task that holds Spark Scala script code that will be executed in Spark via {@code Spark-shell}.</p>
 *
 * @author martinr
 */
public class SparkScalaTask extends BaseTask {

    private static final Logger log = LoggerFactory.getLogger(SparkScalaTask.class);
    private static TaskExecutor taskExecutor = null;

    /**
     *
     * Creates a SparkScalaTask with the specified id,scala code and task configuration.
     *
     * @param taskId             Task Id
     * @param content            Spark Scala script code to run.
     * @param taskConfiguration  Task specification configuration XML filename.
     */
    @AssistedInject
    private SparkScalaTask(TemplateService templateService,
                           @Named("spark-scala") TaskExecutor jdbcTaskExecutor,
                           @Assisted("taskid")   String taskId,
                           @Assisted("content")  String content,
                           @Assisted Configuration taskConfiguration) {
        super(taskId,content,taskConfiguration,templateService,jdbcTaskExecutor);
        log.trace("Built a new SparkScalaTask with id: " + taskId);
    }

    /**
     *
     * Creates a SparkScalaTask with the specified id,scala script content and task configuration.
     *
     * @param taskId             Task Id
     * @param content            Scala script (Spark) content
     */
    @AssistedInject
    private SparkScalaTask(TemplateService templateService,
                           @Named("spark-scala") TaskExecutor jdbcTaskExecutor,
                           @Assisted("taskid")   String taskId,
                           @Assisted("content")  String content) {
        super(taskId,content,templateService,jdbcTaskExecutor);
        log.trace("Built a new SparkScalaTask with id: " + taskId);
    }

}
