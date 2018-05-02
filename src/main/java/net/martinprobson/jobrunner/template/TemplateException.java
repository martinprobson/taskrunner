package net.martinprobson.jobrunner.template;

public class TemplateException extends Exception {

    public TemplateException(final String msg) {
        super(msg);
    }

    public TemplateException(String msg, Throwable cause) {
        super(msg, cause);
    }

}
