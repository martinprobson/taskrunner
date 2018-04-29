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

import org.apache.commons.configuration2.Configuration;
import org.apache.commons.configuration2.XMLConfiguration;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class TaskRunnerConfigurationProviderTest {

    @Test
    public void getXmlConfig() {
        XMLConfiguration config =
                TaskRunnerConfigurationProvider.getXmlConfig("configprovider_test_1.xml");
        assertEquals("foo", config.getString("test"));
    }

    @Test
    public void getConfigurationtest1() {
        TaskRunnerConfigurationProvider provider =
                new TaskRunnerConfigurationProvider("configprovider_test_1.xml");
        Configuration config = provider.getConfiguration();
        assertEquals("foo", config.getString("test"));
        assertEquals("foo", config.getString("test3"));

    }

    @Test
    public void getConfigurationtest2() {
        TaskRunnerConfigurationProvider provider =
                new TaskRunnerConfigurationProvider("configprovider_test_1.xml", "configprovider_test_2.xml");
        Configuration config = provider.getConfiguration();
        assertEquals("Configuration from first config file can be accessed", "foo", config.getString("test"));
        assertEquals("Configuration from second config file can be accessed", "bar", config.getString("test2"));
    }

    @Test
    public void getConfigurationtest3() {
        TaskRunnerConfigurationProvider provider =
                new TaskRunnerConfigurationProvider("configprovider_test_1.xml", "configprovider_test_2.xml");
        Configuration config = provider.getConfiguration();
        assertEquals("Configuration from second config overrides same configuration key in first", "bar", config.getString("test3"));


    }

}