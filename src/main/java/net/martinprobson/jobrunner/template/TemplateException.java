package net.martinprobson.jobrunner.template;

@SuppressWarnings("unused")
public class TemplateException extends Exception {

    public TemplateException(final String msg) {
        super(msg);
    }

    public TemplateException(String msg, Throwable cause) {
        super(msg, cause);
    }

}
