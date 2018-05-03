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

import com.google.inject.Guice;
import com.google.inject.Inject;
import com.google.inject.Injector;
import net.martinprobson.jobrunner.configurationservice.ConfigurationService;
import net.martinprobson.jobrunner.dummytask.DummyTaskModule;
import net.martinprobson.jobrunner.jdbctask.JDBCTaskModule;
import net.martinprobson.jobrunner.sparkscalatask.SparkScalaTask;
import net.martinprobson.jobrunner.sparkscalatask.SparkScalaTaskModule;
import org.apache.commons.configuration2.CombinedConfiguration;
import org.apache.commons.configuration2.Configuration;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

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
 * {@link TaskProvider#createTask(String, String, String, Configuration)}</p> methods.
 *  <p>The actual construction of the Task is handed off to a OldTaskFactory managed
 *  by Guice.</p>
 */
public class TaskProvider {

    private static TaskProvider singleton;
    private Map<String,TaskFactory> taskMapping;

    /**
     * The Mapping between task type (String) and a factory that can
     * build that type of task is injected into this class.
     * @param taskMapping Task type -> Task mapping (injected).
     */
    @Inject
    private TaskProvider(Map<String, TaskFactory> taskMapping) {
        this.taskMapping = taskMapping;
    }

    /**
     * <h3>{@code createTask}</h3>
     * <p>Construct a new Task with the type {@code taskType}</p>
     * @param taskType The type of task to construct
     * @param taskId The task id of the new task
     * @param taskContent The content of the new task
     * @param taskConfiguration The task specific configuration (e.g. dependency and/or template fields)
     * @return A new Task of the type {@code taskType}
     * @throws JobRunnerException If the task type is unknown.
     */
    public BaseTask createTask(String taskType, String taskId, String taskContent, @Nullable Configuration taskConfiguration ) throws JobRunnerException {
        TaskFactory taskFactory = taskMapping.get(taskType);
        if (taskFactory == null) {
            String sb = "Unknown task type - [" +
                    taskType +
                    "] is not a registered task type. " +
                    " Registered task types are " +
                    taskMapping.keySet();
            throw new JobRunnerException(sb);
        }
        return taskFactory.create(taskId,taskContent,taskConfiguration);
    }

    /**
     * <h3>{@code createTask}</h3>
     * <p>Construct a new Task with the type {@code taskType}</p>
     * @param taskType The type of task to construct
     * @param taskId The task id of the new task
     * @param taskContent The content of the new task
     * @return A new Task of the type {@code taskType}
     * @throws JobRunnerException If the task type is unknown.
     */
    public BaseTask createTask(String taskType, String taskId, String taskContent) throws JobRunnerException {
        return createTask(taskType,taskId,taskContent,null);
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
     * {@link TaskProvider#createTask(String, String, String, Configuration)}</p> methods.
     *
     * @return A TaskProvider instance that can be used to construct tasks.
     */
    public static TaskProvider getInstance() {
        if (singleton == null) {
            Injector injector = Guice.createInjector(new DummyTaskModule(),
                                                     new JDBCTaskModule(),
                                                     new SparkScalaTaskModule());
            singleton = injector.getInstance(TaskProvider.class);
        }
        return singleton;
    }

    //@TODO Remove
    public static void main(String args[]) {
        ConfigurationService.load(new ConfigurationService(new JobRunnerConfigurationProvider("reference_config.xml")));
        TaskProvider taskProvider = TaskProvider.getInstance();
        BaseTask d = null;
        List<BaseTask> tasks = new ArrayList<>();
        try {
            tasks.add(taskProvider.createTask("dummy","Martin","contents",new CombinedConfiguration()));
            tasks.add(taskProvider.createTask("dummy","Martin2","contents"));
            tasks.add(taskProvider.createTask("jdbc","Martin","contents",new CombinedConfiguration()));
            tasks.add(taskProvider.createTask("jdbc","Martin2","contents"));
            tasks.add(taskProvider.createTask("spark-scala","Martin","contents",new CombinedConfiguration()));
            tasks.add(taskProvider.createTask("spark-scala","Martin2","contents"));

        } catch (JobRunnerException e) {
            e.printStackTrace();
        }
        System.out.println("Done");
    }

}

