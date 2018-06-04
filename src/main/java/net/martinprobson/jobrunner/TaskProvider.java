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

import com.google.inject.*;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import net.martinprobson.jobrunner.common.BaseTask;
import net.martinprobson.jobrunner.common.JobRunnerException;
import net.martinprobson.jobrunner.configurationservice.ConfigurationProvider;

import java.io.File;
import java.util.Map;
import java.util.Optional;

/**
 * <h2>{@code TaskProvider}</h2>
 * <p>A {@code TaskProvider} is responsible for constructing new Tasks via its
 * {@code createTask} methods.</p>
 * <h3>Detail</h3>
 * <p>The type of tasks that a {@code TaskProvider} can construct depends on
 * the task plugin modules that are installed. These are configured as
 * <a href="https://github.com/google/guice/wiki/Motivation">Google Guice</a>
 * modules.
 * <p>A {@code TaskProvider} internally holds a mapping between Task type (String)
 * and Task that it uses to validate and build new Tasks when requested via the
 * {@link TaskProvider#createTask(String, String, File, Config)}</p> methods.
 *  <p>The actual construction of the Task is handed off to a OldTaskFactory managed
 *  by Guice.</p>
 */
public class TaskProvider {


    /**
     * <h3>{@code fileExtensionCreateTask}</h3>
     * <p>Construct a new Task based on the passed {@code fileExtension}</p><p></p>
     * <h4>Detail</h4>
     * Internally, a {@code TaskProvider} holds a mapping between file extension and
     * task type (obtained from configuration). Therefore this method can be used by a
     * file system Task builder to obtain a task of the correct type, based on the file
     * extension being processed.
     * If no mapping is found between file type and Task, then a TaskProviderMapping exception is
     * thrown for the caller to deal with.
     *
     * @param fileExtension The file extension to process (in the form e.g {@code sql}
     * @param taskId The task id.
     * @param  taskFile The file containing the task content.
     * @param taskConfiguration (Optional) task specific configuration.
     * @return An {@code Optional<BaseTask>}
     * @throws JobRunnerException General error occurred.
     */
    public Optional<BaseTask> fileExtensionCreateTask(String fileExtension,
                                                      String taskId,
                                                      File  taskFile,
                                                      Config taskConfiguration ) throws JobRunnerException {
        String taskType = pluginConfig.fileExtensionMapping.get(fileExtension.toLowerCase());
        if (taskType == null)
            return Optional.empty();
        else
            return Optional.of(createTask(taskType, taskId,taskFile, taskConfiguration));
    }

    public String[] getSupportedFileExtensions() {
        return pluginConfig.fileExtensionMapping.keySet().toArray(new String[0]);
    }

    /**
     * <h3>{@code createTask}</h3>
     * <p>Construct a new Task with the type {@code taskType}</p>
     * @param taskType The type of task to construct
     * @param taskId The task id of the new task
     * @param taskFile The file containing the task content.
     * @param taskConfiguration The task specific configuration (e.g. dependency and/or template fields)
     * @return A new Task of the type {@code taskType}
     * @throws JobRunnerException If the task type is unknown.
     */
    public BaseTask createTask(String taskType, String taskId, File taskFile, Config taskConfiguration ) throws JobRunnerException {
        TaskFactory taskFactory = taskMapping.get(taskType);
        if (taskFactory == null) {
            String sb = "Unknown task type - [" +
                    taskType +
                    "] is not a registered task type. " +
                    " Registered task types are " +
                    taskMapping.keySet();
            throw new JobRunnerException(sb);
        }
        return taskFactory.create(taskId,taskFile,taskConfiguration);
    }

    /**
     * <h3>{@code createTask}</h3>
     * <p>Construct a new Task with the type {@code taskType}</p>
     * @param taskType The type of task to construct
     * @param taskId The task id of the new task
     * @param taskFile The file containing the task content.
     * @return A new Task of the type {@code taskType}
     * @throws JobRunnerException If the task type is unknown.
     */
    public BaseTask createTask(String taskType, String taskId, File taskFile) throws JobRunnerException {
        return createTask(taskType,taskId,taskFile,ConfigFactory.empty());
    }

    /**
     * <h3>{@code getInstance}</h3>
     * <p>Configure and return a TaskProvider instance that can be used
     * to construct new Tasks.</p>
     * <h4>Detail</h4>
     * <p>The type of tasks that a {@code TaskProvider} can construct depends on
     * the task plugin modules that are installed. These are configured as
     * <a href="https://github.com/google/guice/wiki/Motivation">Google Guice</a>
     * modules.
     * <p>A {@code TaskProvider} internally holds a mapping between Task type (String)
     * and Task that it uses to validate and build new Tasks when requested via the
     * {@link TaskProvider#createTask(String, String, File, Config)}</p> methods.
     *
     * @return A TaskProvider instance that can be used to construct tasks.
     */
    public static TaskProvider getInstance() {
        if (singleton == null) {
            try {
                pluginConfig = taskConfig.getConfiguration();
            } catch (Exception e) {
                throw new java.util.ServiceConfigurationError("Error occured during task configuration.",e);
            }
            /* This is where the actual list of Modules is provided to Guice to perform the DI */
            Injector injector = Guice.createInjector(pluginConfig.pluginModules);
            singleton = injector.getInstance(TaskProvider.class);
        }
        return singleton;
    }

    private static TaskProvider singleton;

    /**
     * A Map of known Tasks.
     */
    private final Map<String,TaskFactory> taskMapping;

    /**
     * PluginTaskConfiguration - The Tasks and corresponding file extension mappings
     * that this TaskProvider knows about.
     */
    private static PluginTaskConfiguration pluginConfig;

    private static final ConfigurationProvider<PluginTaskConfiguration> taskConfig = new PluginTaskConfigurationProvider();

    /**
     * The Mapping between task type (String) and a factory that can
     * build that type of task is injected into this class.
     * @param taskMapping Task type -> Task mapping (injected).
     */
    @Inject
    private TaskProvider(Map<String, TaskFactory> taskMapping) {
        this.taskMapping = taskMapping;
    }
}

