package net.martinprobson.taskrunner;

import net.martinprobson.taskrunner.configurationservice.ConfigurationService;
import org.apache.commons.configuration2.ImmutableConfiguration;

import java.util.Map;

/**
 * A TaskBuilder can construct a Map of Configured tasks
 * from the resource pointed to by {@link Resource}.
 * <p>It is up to implementing classes to determine the
 * meaning of <code>Resource</code>.</p> <p>It could be a directory
 * on the local filesystem, or a name of a database schema/table, or
 * a URL pointing to a resource on the classpath for example.
 * </p>
 */
abstract class TaskBuilder {

    public TaskBuilder() {
        this(null);
    }

    public TaskBuilder(Resource resource) {
    }

    /**
     * Return the configuration used by this object.
     */
    protected ImmutableConfiguration getConf() {
        return ConfigurationService.getConfiguration();
    }

    abstract Map<String, DependentTask> build();

    static class Resource {
        private final Object resource;
        private final String name;

        public Resource(Object resource) {
            this(resource, resource.toString());
        }

        public Resource(Object resource, String name) {
            this.resource = resource;
            this.name = name;
        }

        public String getName() {
            return name;
        }

        public Object getResource() {
            return resource;
        }

        @Override
        public String toString() {
            return name;
        }
    }

}
