package net.martinprobson.jobrunner.template;

import com.typesafe.config.Config;

/**
 * A dummy template service that does nothing, it simply returns the content unchanged.
 */
public class DummyTemplateService implements TemplateService {

    /**
     * Return the task contents unchanged.
     *
     * @param id            The id of the content (ignored).
     * @param content       The task content.
     * @param configuration Configuration (ignored).
     * @return The unchanged content.
     */
    @Override
    public String apply(String id, String content, Config configuration) {
        return content;
    }
}
