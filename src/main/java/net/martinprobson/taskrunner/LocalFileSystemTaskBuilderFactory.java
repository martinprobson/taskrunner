package net.martinprobson.taskrunner;

import com.google.inject.assistedinject.Assisted;

/**
 * <h3>{@code LocalFileSystemTaskBuilderFactory}</h3>
 * <p>Note this is just an interface, the actual factory is created by
 * the Google Guice DI framework.</p>
 */
public interface LocalFileSystemTaskBuilderFactory {

    /**
     * Construct a new {@link LocalFileSystemTaskBuilder}.
     * <p>A {@code DummyTask} always returns success when executed.</p>
     * @param baseDirectory The base directory on the file system from which Tasks will be loaded.
     * @return A {@code LocalFileSystemTaskBuilder}.
     */
    LocalFileSystemTaskBuilder create(@Assisted String baseDirectory);

}

