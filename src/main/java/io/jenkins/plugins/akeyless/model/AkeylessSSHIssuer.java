package io.jenkins.plugins.akeyless.model;

import hudson.Extension;
import hudson.model.Descriptor;
import java.util.List;
import javax.annotation.Nonnull;
import org.kohsuke.stapler.DataBoundConstructor;

public class AkeylessSSHIssuer extends AkeylessIssuer<AkeylessSSHIssuer> {
    private String certUserName;

    @DataBoundConstructor
    public AkeylessSSHIssuer(
            String path,
            String name,
            String certUserName,
            String publicKey,
            @Nonnull long ttl,
            List<AkeylessSecretValue> secretValues) {
        super(path, name, publicKey, ttl, secretValues);
        this.certUserName = certUserName;
    }

    public String getCertUserName() {
        return this.certUserName;
    }

    @Extension
    public static final class DescriptorImpl extends Descriptor<AkeylessSSHIssuer> {

        @Override
        public String getDisplayName() {
            return "Akeyless SSH Issuer";
        }

        /** Prepare fields validation - currently doesn't work
         *
         * public FormValidation doCheckPath(@QueryParameter String value) {
         * if (!Strings.isNullOrEmpty(value)) return FormValidation.ok();
         * else return FormValidation.error("This field can not be empty");
         * }
         *
         * public FormValidation doCheckCertUserName(@QueryParameter String value) {
         * if (!Strings.isNullOrEmpty(value)) return FormValidation.ok();
         * else return FormValidation.error("This field can not be empty");
         * }
         */
    }
}
