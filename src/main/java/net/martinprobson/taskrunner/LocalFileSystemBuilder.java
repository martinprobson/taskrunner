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

import net.martinprobson.taskrunner.jdbctask.JDBCTask;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.filefilter.SuffixFileFilter;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;

/**
 * {@code LocalFileSystemBuilder} - Build a Collection of tasks from the specified
 * base directory on the local file system.
 *
 * @author martinr
 */
public class LocalFileSystemBuilder implements TaskBuilder {

    private String baseDirectory;

    /**
     * Given a filename of the form <code>file.sql</code>,
     * attempt to find a corresponding <code>file.xml</code> file
     * in the same directory.
     *
     * @param directory - The directory to search.
     * @param sqlFile - The name of the sqlFile.
     * @return - The full pathname of the xml file, or null if not found.
     */
    private static String getConfigFile(File directory,String sqlFile) {
        String configName = directory.getAbsolutePath() +
                File.separatorChar +
                FilenameUtils.getBaseName(sqlFile) +
                ".xml";
        File configFile = new File(configName);
        if (configFile.exists() && configFile.isFile())
            return configFile.getAbsolutePath();
        else
            return null;
    }

    /**
     * Constructs a new LocalFileSystemBuilder with give base directory.
     *
     * @param baseDirectory
     * @throws TaskRunnerException
     */
    public LocalFileSystemBuilder(String baseDirectory) throws TaskRunnerException {
        File f = new File(baseDirectory);
        if (f.exists() && f.isDirectory())
            this.baseDirectory = f.getAbsolutePath();
        else
            throw new TaskRunnerException("LocalFileSystemBuilder: directory " + baseDirectory + " does not exist");
    }

    /**
     *
     * @return A collection of Tasks built from specifically base directory.
     * @throws TaskRunnerException
     */
    @Override
    public Map<String, DependentTask> build() throws TaskRunnerException {
        Map<String, DependentTask> taskMap = new HashMap<>();
        File testDirectory = new File(baseDirectory);
        String[] sqlFiles = testDirectory.list(new SuffixFileFilter(".sql"));
        if (sqlFiles != null)
            for (String sqlFile : sqlFiles) {
                File file = new File(testDirectory.getAbsolutePath() + File.separatorChar + sqlFile);
                String contents;
                try {
                    contents = FileUtils.readFileToString(file, Charset.defaultCharset());
                } catch (IOException e) {
                    throw new TaskRunnerException("Error reading file: " + file, e);
                }
                String configFile = getConfigFile(testDirectory, sqlFile);
                if (configFile == null)
                    taskMap.put(sqlFile, new JDBCTask(sqlFile, contents));
                else
                    taskMap.put(sqlFile, new JDBCTask(sqlFile, contents, configFile));
            }

        return taskMap;
    }
}
