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

import net.martinprobson.jobrunner.common.BaseTask;
import net.martinprobson.jobrunner.common.JobRunnerException;
import org.apache.commons.configuration2.XMLConfiguration;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.filefilter.SuffixFileFilter;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;

/**
 * {@code LocalFileSystemTaskBuilder} - Build a Collection of tasks from the specified
 * base directory on the local file system.
 *
 * @author martinr
 */
public class LocalFileSystemTaskBuilder implements TaskBuilder {

    private static TaskProvider taskProvider;
    private String taskDirectory;

    private LocalFileSystemTaskBuilder(String taskDirectory, String configDirectory) {
        this.taskDirectory = taskDirectory;
        String configDirectory1 = configDirectory;
    }

    public static LocalFileSystemTaskBuilder create(String taskDirectory,
                                                    String configDirectory) throws JobRunnerException {
        File taskDir = new File(taskDirectory);
        if (!taskDir.exists() || !taskDir.isDirectory())
            throw new JobRunnerException("task directory " + taskDirectory + " does not exist");
        File configDir = new File(configDirectory);
        if (!configDir.exists() || !configDir.isDirectory())
            throw new JobRunnerException("config directory " + configDirectory + " does not exist");
        taskProvider = TaskProvider.getInstance();
        return new LocalFileSystemTaskBuilder(taskDir.getAbsolutePath(), configDir.getAbsolutePath());
    }

    /**
     * <p>Given a filename of the form {@code file.<ext>}
     * attempt to find a corresponding <code>file.xml</code> file
     * in the same directory.</p>
     * <p>If found, then load the task specific configuration from
     * the file.</p>
     * <p>If not found, just return an empty configuration.</p>
     *
     * @param directory - The directory to search.
     * @param file      - The name of the file.
     * @return - An {@code XMLConfiguration}.
     */
    private static XMLConfiguration getTaskConfiguration(File directory, String file) {
        String configName = directory.getAbsolutePath() +
                File.separatorChar +
                FilenameUtils.getBaseName(file) +
                ".xml";
        File configFile = new File(configName);
        if (configFile.exists() && configFile.isFile())
            return TaskBuilder.getConfig(configFile.getAbsolutePath());
        else
            return new XMLConfiguration();
    }

    /**
     * @return A collection of Tasks built from specific base directory.
     * @throws JobRunnerException
     */
    @Override
    public Map<String, BaseTask> build() throws JobRunnerException {

        Map<String, BaseTask> taskMap = new HashMap<>();
        File testDirectory = new File(taskDirectory);
        String[] taskFiles = testDirectory.list(new SuffixFileFilter(taskProvider.getSupportedFileExtensions()));
        if (taskFiles != null)
            for (String taskFile : taskFiles) {
                File file = new File(testDirectory.getAbsolutePath() + File.separatorChar + taskFile);
                String fileExtension = FilenameUtils.getExtension(file.getName());
                String contents;
                try {
                    contents = FileUtils.readFileToString(file, Charset.defaultCharset());
                } catch (IOException e) {
                    throw new JobRunnerException("Error reading file: " + file, e);
                }
                XMLConfiguration taskConfig = getTaskConfiguration(testDirectory, taskFile);
                BaseTask task = taskProvider.fileExtensionCreateTask("." + fileExtension,
                        taskFile,
                        contents,
                        taskConfig)
                        .orElseThrow(() -> new JobRunnerException("No mapping found for " + fileExtension.toLowerCase()));
                taskMap.put(taskFile, task);
            }
        return taskMap;
    }
}
