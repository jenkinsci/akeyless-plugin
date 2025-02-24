package io.jenkins.plugins.akeyless.credentials;

import com.cloudbees.plugins.credentials.CredentialsScope;
import edu.umd.cs.findbugs.annotations.NonNull;
import hudson.Extension;
import io.akeyless.client.model.Auth;
import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.DataBoundSetter;

public class AkeylessCertCredentials extends AbstractAkeylessBaseStandardCredentials implements AkeylessCredential {

    private String accessId;
    private String certificate;
    private String privateKey;

    @DataBoundConstructor
    public AkeylessCertCredentials(CredentialsScope scope, String id, String description) {
        super(scope, id, description);
    }

    public String getAccessId() {
        return accessId;
    }

    public String getCertificate() {
        return certificate;
    }

    public String getPrivateKey() {
        return privateKey;
    }

    @DataBoundSetter
    public void setAccessId(String accessId) {
        this.accessId = accessId;
    }

    @DataBoundSetter
    public void setCertificate(String certificate) {
        this.certificate = certificate;
    }

    @DataBoundSetter
    public void setPrivateKey(String privateKey) {
        this.privateKey = privateKey;
    }

    public Auth getAuth() {
        Auth auth = new Auth();
        auth.setAccessId(accessId);
        auth.setAccessType("cert");
        auth.setCertData(certificate);
        auth.setKeyData(privateKey);
        return auth;
    }

    @Override
    public CredentialsPayload getCredentialsPayload() {
        CredentialsPayload payload = new CredentialsPayload();
        payload.setAuth(getAuth());
        return payload;
    }

    @Extension
    public static class DescriptorImpl extends BaseStandardCredentialsDescriptor {

        @NonNull
        @Override
        public String getDisplayName() {
            return "Akeyless Certificate Credentials";
        }
    }
}
