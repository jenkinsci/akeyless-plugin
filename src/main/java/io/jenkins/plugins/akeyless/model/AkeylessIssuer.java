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
public class AkeylessIssuer extends AbstractDescribableImpl<AkeylessIssuer> implements AkeylessSecretBase {

    private String path;
    private String name;
    private String certUserName;
    private String publicKey;
    private String csrBase64;
    private long ttl;

    private List<AkeylessSecretValue> secretValues;

    @DataBoundConstructor
    public AkeylessIssuer(
            String path,
            String name,
            String certUserName,
            String publicKey,
            String csrBase64,
            long ttl,
            List<AkeylessSecretValue> secretValues) {
        this.path = fixEmptyAndTrim(path);
        this.secretValues = secretValues;
        this.name = name;
        this.certUserName = certUserName;
        this.publicKey = publicKey;
        this.csrBase64 = csrBase64;
        this.ttl = ttl;
    }

    public String getPath() {
        return this.path;
    }

    public String getName() {
        return this.name;
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
    }
}
