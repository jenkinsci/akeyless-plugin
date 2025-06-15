package io.jenkins.plugins.akeyless.credentials;

import com.cloudbees.plugins.credentials.CredentialsScope;
import edu.umd.cs.findbugs.annotations.NonNull;
import hudson.Extension;
import hudson.util.Secret;
import io.akeyless.client.model.Auth;
import org.kohsuke.stapler.DataBoundConstructor;

/**
 * @author alexeydolgopyatov
 */
public class AkeylessJWTCredentials extends AbstractAkeylessBaseStandardCredentials implements AkeylessCredential {

    @NonNull
    @SuppressWarnings("lgtm[jenkins/plaintext-storage]")
    private String accessId = "";

    private Secret token;

    @DataBoundConstructor
    public AkeylessJWTCredentials(CredentialsScope scope, String id, String description) {
        super(scope, id, description);
    }

    public Secret getToken() {
        return token;
    }

    public void setToken(Secret token) {
        this.token = token;
    }

    @NonNull
    public String getAccessId() {
        return accessId;
    }

    public void setAccessId(String accessId) {
        this.accessId = accessId;
    }

    @Override
    public CredentialsPayload getCredentialsPayload() {
        CredentialsPayload payload = new CredentialsPayload();
        Auth auth = new Auth();
        auth.setAccessType("jwt");
        auth.setAccessId(getAccessId());
        auth.jwt(getToken().getPlainText());
        payload.setAuth(auth);
        return payload;
    }

    @Extension
    public static class DescriptorImpl extends BaseStandardCredentialsDescriptor {

        @NonNull
        @Override
        public String getDisplayName() {
            return "Akeyless JWT Credentials";
        }
    }
}
