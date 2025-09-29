package io.jenkins.plugins.akeyless.model;

import hudson.Extension;
import hudson.model.Descriptor;
import java.util.List;
import org.kohsuke.stapler.DataBoundConstructor;

public class AkeylessPKIIssuer extends AkeylessIssuer<AkeylessPKIIssuer> {
    private String csrBase64;

    @DataBoundConstructor
    public AkeylessPKIIssuer(
            String path,
            String name,
            String certUserName,
            String publicKey,
            String csrBase64,
            long ttl,
            List<AkeylessSecretValue> secretValues) {
        super(path, name, publicKey, ttl, secretValues);
        this.csrBase64 = csrBase64;
    }

    public String getCsrBase64() {
        return this.csrBase64;
    }

    @Extension
    public static final class DescriptorImpl extends Descriptor<AkeylessPKIIssuer> {

        @Override
        public String getDisplayName() {
            return "Akeyless PKI Issuer";
        }

        /** Prepare fields validation - currently doesn't work
         *
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
