package io.jenkins.plugins.akeyless.model;

import static hudson.Util.fixEmptyAndTrim;

import hudson.Extension;
import hudson.model.AbstractDescribableImpl;
import hudson.model.Descriptor;
import java.util.List;
import org.kohsuke.stapler.DataBoundConstructor;

/**
 * @author alexeydolgopyatov
 */
public class AkeylessSecret extends AbstractDescribableImpl<AkeylessSecret> implements AkeylessSecretBase {

    private String path;
    private List<AkeylessSecretValue> secretValues;

    @DataBoundConstructor
    public AkeylessSecret(String path, List<AkeylessSecretValue> secretValues) {
        this.path = fixEmptyAndTrim(path);
        this.secretValues = secretValues;
    }

    public String getPath() {
        return this.path;
    }

    public List<AkeylessSecretValue> getSecretValues() {
        return this.secretValues;
    }

    @Extension
    public static final class DescriptorImpl extends Descriptor<AkeylessSecret> {

        @Override
        public String getDisplayName() {
            return "Akeyless Secret";
        }
    }
}
