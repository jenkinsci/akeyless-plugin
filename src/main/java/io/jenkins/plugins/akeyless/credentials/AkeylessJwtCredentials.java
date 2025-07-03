package io.jenkins.plugins.akeyless.credentials;

import com.cloudbees.plugins.credentials.CredentialsScope;
import edu.umd.cs.findbugs.annotations.NonNull;
import hudson.Extension;
import io.akeyless.client.model.Auth;
import javax.annotation.CheckForNull;
import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.DataBoundSetter;

public class AkeylessJwtCredentials extends AbstractAkeylessBaseStandardCredentials implements AkeylessCredential {

    @NonNull
    @SuppressWarnings("lgtm[jenkins/plaintext-storage]")
    private String accessId = "";

    @DataBoundConstructor
    public AkeylessJwtCredentials(
            @CheckForNull CredentialsScope scope, @CheckForNull String id, @CheckForNull String description) {
        super(scope, id, description);
    }

    @NonNull
    public String getAccessId() {
        return accessId;
    }

    @DataBoundSetter
    public void setAccessId(String accessId) {
        this.accessId = accessId;
    }

    @Override
    public CredentialsPayload getCredentialsPayload() {
        CredentialsPayload payload = new CredentialsPayload();
        Auth auth = new Auth();
        auth.setAccessId(accessId);
        auth.setAccessType("jwt");
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
