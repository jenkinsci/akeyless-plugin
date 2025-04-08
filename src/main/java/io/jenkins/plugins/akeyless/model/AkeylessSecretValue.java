package io.jenkins.plugins.akeyless.model;

import static hudson.Util.fixEmptyAndTrim;

import edu.umd.cs.findbugs.annotations.NonNull;
import hudson.Extension;
import hudson.model.AbstractDescribableImpl;
import hudson.model.Descriptor;
import org.apache.commons.lang.StringUtils;
import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.DataBoundSetter;

/**
 * @author alexeydolgopyatov
 */
public class AkeylessSecretValue extends AbstractDescribableImpl<AkeylessSecretValue> {

    private String envVar;
    private boolean isRequired = DescriptorImpl.DEFAULT_IS_REQUIRED;

    @SuppressWarnings("lgtm[jenkins/plaintext-storage]")
    private final String secretKey;

    @DataBoundConstructor
    public AkeylessSecretValue(@NonNull String secretKey) {
        this.secretKey = fixEmptyAndTrim(secretKey);
    }

    @DataBoundSetter
    public void setEnvVar(String envVar) {
        this.envVar = envVar;
    }

    @DataBoundSetter
    public void setIsRequired(boolean isRequired) {
        this.isRequired = isRequired;
    }

    /**
     *
     * @return envVar if value is not empty otherwise return vaultKey
     */
    public String getEnvVar() {
        return StringUtils.isEmpty(envVar) ? secretKey : envVar;
    }

    public String getSecretKey() {
        return secretKey;
    }

    public boolean getIsRequired() {
        return isRequired;
    }

    @Extension
    public static final class DescriptorImpl extends Descriptor<AkeylessSecretValue> {

        public static final Boolean DEFAULT_IS_REQUIRED = true;

        @Override
        public String getDisplayName() {
            return "Environment variable/vault secret value pair";
        }
    }
}
