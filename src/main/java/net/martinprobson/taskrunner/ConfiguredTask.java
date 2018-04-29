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

import net.martinprobson.taskrunner.configurationservice.ConfigurationService;
import org.apache.commons.configuration2.CombinedConfiguration;
import org.apache.commons.configuration2.Configuration;

/**
 * <p><code>ConfiguredTask</code></p>
 *
 * <p>A {@code ConfiguredTask} is a task that can hold one or more Configurations
 * ( as defined by {@link org.apache.commons.configuration2.Configuration}).</p>
 * <p>At the very least, the global {@code TaskRunner} configuration is held, but, in addition,
 * task specific configuration can also be added; for example to list task template and/or
 * dependency information.</p>
 *
 * @param <C> The type of task.
 * @param <T> The type of the task id.
 *
 * @author martinr
 */
abstract class ConfiguredTask<C, T> extends BaseTaskRunnerTask<C, T> {

    private final CombinedConfiguration configuration;

    /**
     * Construct a {@code ConfiguredTask} with the given task specific configuration.
     *
     * @param id                The id of the task.
     * @param task              The task contents.
     * @param taskConfiguration The task specific configuration.
     */
    private ConfiguredTask(T id, C task, Configuration taskConfiguration) {
        this(id, task);
        configuration.addConfiguration(taskConfiguration, id.toString());
    }

    /**
     * Construct a {@code ConfiguredTask} with the just the default {@code TaskRunner} configuration.
     *
     * @param id   The id of the task.
     * @param task The task contents.
     */
    ConfiguredTask(T id, C task) {
        super(id, task);
        // First get the global configuration ....
        Configuration conf = ConfigurationService.getConfiguration();
        // add it to our CombinedConfiguration....
        configuration = new CombinedConfiguration();
        configuration.addConfiguration(conf, "global");
    }

    /**
     * Construct a {@code ConfiguredTask} with the given task specific configuration.
     *
     * @param id                The id of the task.
     * @param task              The task contents.
     * @param taskConfiguration The task specific configuration. String contains a reference to a
     *                          XML configuration file.
     */
    ConfiguredTask(T id, C task, String taskConfiguration) {
        this(id, task, TaskRunnerConfigurationProvider.getXmlConfig(taskConfiguration));
    }

    /**
     * Return the (Combined) Configuration for this task.
     *
     * @return Configuration {@link org.apache.commons.configuration2.CombinedConfiguration}
     */
    Configuration getConfiguration() {
        return configuration;
    }
}
