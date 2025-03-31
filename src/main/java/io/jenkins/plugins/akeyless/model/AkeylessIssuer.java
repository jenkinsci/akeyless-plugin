package io.jenkins.plugins.akeyless.model;

import com.google.common.base.Strings;
import edu.umd.cs.findbugs.annotations.NonNull;
import hudson.Extension;
import hudson.Util;
import hudson.model.AbstractDescribableImpl;
import hudson.model.Descriptor;
import hudson.util.FormValidation;
import java.util.List;
import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.QueryParameter;

/**
 * @author alexeydolgopyatov
 */
public class AkeylessIssuer extends AbstractDescribableImpl<AkeylessIssuer> implements AkeylessSecretBase {
    private String path;

    private String outputFile;

    private String certUserName;

    private String publicKey;

    private String csrBase64;

    @NonNull
    private long ttl = 0;

    private List<AkeylessSecretValue> secretValues;

    @DataBoundConstructor
    public AkeylessIssuer(
            String path,
            String name,
            String certUserName,
            String publicKey,
            String csrBase64,
            @NonNull long ttl,
            List<AkeylessSecretValue> secretValues) {
        this.path = Util.fixEmptyAndTrim(path);
        this.secretValues = secretValues;
        this.outputFile = name;
        this.certUserName = certUserName;
        this.publicKey = publicKey;
        this.csrBase64 = csrBase64;
        this.ttl = ttl;
    }

    public String getPath() {
        return this.path;
    }

    public String getOutputFile() {
        return this.outputFile;
    }

    public String getCertUserName() {
        return this.certUserName;
    }

    public String getPublicKey() {
        return this.publicKey;
    }

    public String getCsrBase64() {
        return this.csrBase64;
    }

    public List<AkeylessSecretValue> getSecretValues() {
        return this.secretValues;
    }

    public long getTtl() {
        return this.ttl;
    }

    @Extension
    public static final class DescriptorImpl extends Descriptor<AkeylessIssuer> {

        @Override
        public String getDisplayName() {
            return "Akeyless Issuer";
        }

        public FormValidation doCheckPath(@QueryParameter String value) {
            if (!Strings.isNullOrEmpty(value)) return FormValidation.ok();
            else return FormValidation.error("This field can not be empty");
        }

        public FormValidation doCheckCertUserName(@QueryParameter String value) {
            if (!Strings.isNullOrEmpty(value)) return FormValidation.ok();
            else return FormValidation.error("This field can not be empty");
        }
    }
}
