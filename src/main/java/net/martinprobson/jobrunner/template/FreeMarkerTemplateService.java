package net.martinprobson.jobrunner.template;


import freemarker.template.Template;
import freemarker.template.TemplateException;
import freemarker.template.TemplateExceptionHandler;
import org.apache.commons.configuration2.Configuration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import static freemarker.template.Configuration.VERSION_2_3_28;

/**
 * A mock template service that does nothing, it simply returns the content unchanged.
 */
public class FreeMarkerTemplateService implements TemplateService {

    private static final Logger log = LoggerFactory.getLogger(FreeMarkerTemplateService.class);
    private static final freemarker.template.Configuration freeMarkerConfig;

    static {
        freeMarkerConfig = new freemarker.template.Configuration(VERSION_2_3_28);
        freeMarkerConfig.setDefaultEncoding("UTF-8");
        freeMarkerConfig.setTemplateExceptionHandler(TemplateExceptionHandler.DEBUG_HANDLER);
        freeMarkerConfig.setInterpolationSyntax(freemarker.template.Configuration.SQUARE_BRACKET_INTERPOLATION_SYNTAX);
        freeMarkerConfig.setTagSyntax(freemarker.template.Configuration.SQUARE_BRACKET_TAG_SYNTAX);
    }

    @Override
    public String apply(String id, String content, Configuration configuration) throws net.martinprobson.jobrunner.template.TemplateException {
        String templatedContent;
        Configuration sub =  configuration.subset("template");
        Iterator<String> keys = sub.getKeys();
        Map<String,String> templateVars = new HashMap<>();
        while (keys.hasNext()) {
            String key = keys.next();
            templateVars.put(key,sub.getString(key));
        }
        if (templateVars.isEmpty())
            templatedContent = content;
        else {
            Template template;
            try {
                template = new Template(id, content, freeMarkerConfig);
                StringWriter stringWriter = new StringWriter();
                template.process(templateVars, stringWriter);
                templatedContent =stringWriter.toString();
            } catch (IOException e) {
                throw new net.martinprobson.jobrunner.template.TemplateException("IOError", e);
            } catch (TemplateException e) {
                throw new net.martinprobson.jobrunner.template.TemplateException("TemplateException", e);
            }
        }
        return templatedContent;
    }

    /* For testing */
    FreeMarkerTemplateService() {
    }
}




