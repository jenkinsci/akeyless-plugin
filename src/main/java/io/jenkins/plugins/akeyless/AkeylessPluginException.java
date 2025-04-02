package io.jenkins.plugins.akeyless;

/**
 * @author alexeydolgopyatov
 */
public class AkeylessPluginException extends RuntimeException {
    public AkeylessPluginException(String s) {
        super(s);
    }

    public AkeylessPluginException(String s, Throwable throwable) {
        super(s, throwable);
    }
}
