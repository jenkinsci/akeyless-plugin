package io.jenkins.plugins.akeyless.credentials;

import com.cloudbees.plugins.credentials.CredentialsScope;
import hudson.util.Secret;
import javax.annotation.Nonnull;
import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.DataBoundSetter;

public class AkeylessTokenCredentials extends AbstractAkeylessBaseStandardCredentials implements AkeylessCredential {
    private Secret token;

    @DataBoundConstructor
    public AkeylessTokenCredentials(CredentialsScope scope, String id, String description) {
        super(scope, id, description);
    }

    @Nonnull
    public String getToken() {
        return Secret.toString(token);
    }

    @DataBoundSetter
    public void setToken(String token) {
        this.token = Secret.fromString(token);
    }

    @Override
    public CredentialsPayload getCredentialsPayload() {
        CredentialsPayload payload = new CredentialsPayload();
        payload.setToken(getToken());
        return payload;
    }

    //    @Extension
    //    public static class DescriptorImpl extends BaseStandardCredentialsDescriptor {
    //
    //        @Nonnull
    //        @Override
    //        public String getDisplayName() {
    //            return "Akeyless t-Token Credentials";
    //        }
    //    }
}
