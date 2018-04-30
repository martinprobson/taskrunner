package net.martinprobson.taskrunner.net.martinprobson.taskrunner.template;


import org.apache.commons.configuration2.Configuration;

/**
 * <h3><p>{@code TemplateService}</p></h3>
 *
 * <p>A service responsible for applying a template against content.</p>
 * <p>The interface consists of a single method {@code apply} that
 * accepts the content and the {@code Configuration} from where the
 * template fields will be looked up.</p>
 *
 */
public interface TemplateService {
    /**
     * Apply the template fields defined in {@code COnfiguation} to the given content.
     * Returns the content modified by the template fields or a {@code TemplateException} on error
     * (for example missing fields).
     * @param content The content to apply the template against.
     * @param configuration The configuration from which the template fields are looked up.
     * @return The content modified by the template.
     * @throws TemplateException if a field cannot be found or other template error occurs.
     */
    public String apply(String content, Configuration configuration) throws TemplateException;
}
