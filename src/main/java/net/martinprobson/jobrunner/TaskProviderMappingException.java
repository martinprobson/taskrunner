package net.martinprobson.jobrunner;

class TaskProviderMappingException extends Exception {

    TaskProviderMappingException(final String msg) {
        super(msg);
    }

    TaskProviderMappingException(String msg, Throwable cause) {
        super(msg, cause);
    }

}
