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

import net.martinprobson.taskrunner.configurationservice.ConfigurationProvider;
import org.apache.commons.configuration2.CompositeConfiguration;
import org.apache.commons.configuration2.Configuration;
import org.apache.commons.configuration2.XMLConfiguration;
import org.apache.commons.configuration2.builder.FileBasedConfigurationBuilder;
import org.apache.commons.configuration2.builder.fluent.Parameters;
import org.apache.commons.configuration2.ex.ConfigurationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * {@code TaskRunnerConfigurationProvider} is responsible for loading
 * global configuration used by the {@code TaskRunner} and all packaged classes.
 *
 * @author martinr
 */
public class TaskRunnerConfigurationProvider implements ConfigurationProvider<Configuration> {
    private static final Logger log = LoggerFactory.getLogger(TaskRunnerConfigurationProvider.class);
    private final CompositeConfiguration configuration;

    public TaskRunnerConfigurationProvider(String firstConfig, String... others) {
        configuration = new CompositeConfiguration(getXmlConfig(firstConfig));
        for (String config : others) configuration.addConfiguration(getXmlConfig(config));
    }

    /**
     * Helper method to get a {@code XMLConfiguration} from the classpath.
     *
     * @param fileName Name of the XML configuration file to load.
     * @return - The loaded XMLConfiguration.
     */
    static XMLConfiguration getXmlConfig(String fileName) {
        XMLConfiguration config = null;
        FileBasedConfigurationBuilder<XMLConfiguration> builder =
                new FileBasedConfigurationBuilder<>(XMLConfiguration.class)
                        .configure(new Parameters().xml().setFileName(fileName));
        try {
            config = builder.getConfiguration();
        } catch (ConfigurationException cex) {
            log.error("Loading of configuration failed", cex);
            System.exit(2);
        }
        log.debug("Loaded configuration file: " + fileName);
        return config;
    }

    @Override
    public Configuration getConfiguration() {
        return configuration;
    }

}

