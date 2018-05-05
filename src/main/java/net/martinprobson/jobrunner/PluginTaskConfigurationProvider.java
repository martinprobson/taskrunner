package net.martinprobson.jobrunner;

import com.google.inject.Module;
import com.typesafe.config.Config;
import net.martinprobson.jobrunner.configurationservice.ConfigurationProvider;
import com.typesafe.config.ConfigFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
 *     <li>{@code plugin-module} - Name of the Java class that implements this Task.</li>
 *     <li>{@code file-extensions} - The list of file extensions supported by this task.</li>
 * </ul>
 * <p>The list of configured tasks is returned in a {@link PluginTaskConfiguration} object.</p>
 */
class PluginTaskConfigurationProvider implements ConfigurationProvider<PluginTaskConfiguration> {
    @Override
    public PluginTaskConfiguration getConfiguration() {
        Map<String,String> fileExtensionMapping = new HashMap<>();
        List<Module> modules = new ArrayList<>();

        Config conf = ConfigFactory.load();
        for (Config c: conf.getConfigList("plugintasks.task")) {
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
        Class clazz = null;
        try {
            clazz = ClassLoader.getSystemClassLoader().loadClass(moduleClassName);
        } catch (ClassNotFoundException e) {
            throw new TaskProviderMappingException("Error when constructing class: " + moduleClassName,e);
        }
        Module module = null;
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
}