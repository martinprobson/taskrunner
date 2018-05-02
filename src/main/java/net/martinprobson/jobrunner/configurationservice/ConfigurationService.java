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

package net.martinprobson.jobrunner.configurationservice;

import org.apache.commons.configuration2.Configuration;

/**
 * {@code ConfigurationService}
 * <p>A simple Configuration service provider. This class holds the global configuration(s)
 * for all the {@code JobRunner} classes that require it.</p>
 * <p>The configuration itself is loaded via a {@link ConfigurationProvider}</p>
 *
 * @author martinr
 */
public class ConfigurationService {

    private static ConfigurationService service;
    private final Configuration configuration;

    /**
     * Construct a singleton that loads the configuration provided by {@code ConfigurationProvider}
     * <p></p>
     * @param configurationProvider {@code ConfigurationProvider} responsible to locating and loading configuration.
     */
    public ConfigurationService(ConfigurationProvider<Configuration> configurationProvider) {
        this.configuration = configurationProvider.getConfiguration();
    }

    /**
     *
     * @return the {@code Configuration} held by this {@code ConfigurationService}
     *
     * This method throws the unchecked exception {@code ServiceConfigurationError} if
     * the ConfigurationService has not been setup properly.
     *
     */
    public static Configuration getConfiguration() {
        if (service == null)
            throw new java.util.ServiceConfigurationError("JobRunner requires a global configuration to be set.");
        return service.configuration;
    }

    /**
     * Use the provided {@code ConfigurationService} to set configuration.
     */
    public static void load(ConfigurationService configurationService) {
        ConfigurationService.service = configurationService;
    }

    /** For testing only */
    static void clear() { service = null; }

}
