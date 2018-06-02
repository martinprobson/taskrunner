package net.martinprobson.jobrunner;

import com.google.inject.Module;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigValue;
import net.martinprobson.jobrunner.configurationservice.ConfigurationProvider;
import com.typesafe.config.ConfigFactory;

import java.util.*;

/**
 * <h2>{@code PluginTaskConfigurationProvider}</h2>
 * <p>Reads plugin configuration file to obtain list of configured Task plugins.</p>
 * <p>Each configuration entry is of the form: -</p>
 * <pre>{@code
 *   task = [
 *       {
 *         name = "dummy"
 *         plugin-module = "net.martinprobson.jobrunner.dummytask.DummyTaskModule"
 *         file-extensions = ["txt","dmy"]
 *       }
 *       ......
 *     ]
 * }</pre><p>
 * Attributes are: -
 * <ul>
 *     <li>{@code name} - Task name</li>
 *     <li>{@code plugin-module} - Name of the Google Guice module that is responsible for injecting
 *     the required implementation classes into the framework. See note below.</li>
 *     <li>{@code file-extensions} - The list of file extensions supported by this task.</li>
 * </ul>
 * <p>The list of configured tasks is returned in a {@link PluginTaskConfiguration} object.</p>
 * <p></p>
 * <h3>Note on plugin-module</h3>
 * <p>Each plugin-module entry should reference a Google Guice Module class responsible for wiring
 * up the required class dependencies. see {@link net.martinprobson.jobrunner.dummytask.DummyTaskModule} for an example.</p>
 */
public class PluginTaskConfigurationProvider implements ConfigurationProvider<PluginTaskConfiguration> {
    @Override
    public PluginTaskConfiguration getConfiguration() {
        Map<String,String> fileExtensionMapping = new HashMap<>();
        List<Module> modules = new ArrayList<>();

        Config conf = ConfigFactory.load().resolve();
        for (Config c: conf.getConfigList("jobrunner.plugintasks.task")) {
            String name = c.getString("name");
            for (String extn: c.getStringList("file-extensions"))
                fileExtensionMapping.put(extn,name);
            String module = c.getString("plugin-module");
            try {
                modules.add(loadModule(module));
            } catch (TaskProviderMappingException e) {
                throw new java.util.ServiceConfigurationError("Task Configuration error ",e);
            }

        }
        return new PluginTaskConfiguration(fileExtensionMapping,modules);
    }

    private static Module loadModule(String moduleClassName) throws TaskProviderMappingException {
        Class clazz;
        try {
            clazz = PluginTaskConfigurationProvider.class.getClassLoader().loadClass(moduleClassName);
        } catch (ClassNotFoundException e) {
            throw new TaskProviderMappingException("Error when constructing class: " + moduleClassName,e);
        }
        Module module;
        if (Module.class.isAssignableFrom(clazz)) {
            try {
                module = (Module) clazz.newInstance();
            } catch (InstantiationException|IllegalAccessException e) {
                throw new TaskProviderMappingException("Error when constructing class: " + clazz.getName(),e);
            }
        } else {
            throw new TaskProviderMappingException(clazz.getName() +
                    " does not implement " +
                    Module.class.getSimpleName() + " interface.");
        }
        return module;
    }

    public static void main(String args[]) {
        Config conf = ConfigFactory.load("martin.conf");
        conf.getConfig("jobrunner.tempplate");
        Set<Map.Entry<String, ConfigValue>> set = conf.entrySet();
        for (Map.Entry<String, ConfigValue> m: set)
            System.out.println(m.getKey() + ":" + m.getValue() );
    }
}
