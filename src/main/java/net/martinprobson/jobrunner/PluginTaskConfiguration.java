package net.martinprobson.jobrunner;

import com.google.inject.Module;


import java.util.List;
import java.util.Map;

/**
 * <h3>{@code PluginTaskConfiguration}</h3>
 * <p>The Tasks and corresponding file extension to {@code Task}
 * mappings loaded from extenal configuration.<p></p>
 * <h3>Detail</h3>
 * <p>This configuration holds two items: -
 * <ol>
 *     <li>A mapping of file extension(s) to task type.</li>
 *     <li>A list of modules used to inject the {@code Task} type into
 *     the {@link TaskProvider}</li>
 * </ol>
*/
class PluginTaskConfiguration {
    final Map<String,String> fileExtensionMapping;
    final List<? extends Module> pluginModules;

    PluginTaskConfiguration(Map<String,String> fileExtensionMapping,List<? extends Module> pluginModules) {
        this.fileExtensionMapping = fileExtensionMapping;
        this.pluginModules = pluginModules;
    }
}
