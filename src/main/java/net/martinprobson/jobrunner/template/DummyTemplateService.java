package net.martinprobson.jobrunner.template;


import org.apache.commons.configuration2.Configuration;

/**
 * A mock template service that does nothing, it simply returns the content unchanged.
 */
class DummyTemplateService implements TemplateService {

    @Override
    public String apply(String id,String content, Configuration configuration) {
        return "Applied Other template to content";
    }
}
