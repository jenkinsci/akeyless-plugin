package io.jenkins.plugins.akeyless.credentials;

import com.cloudbees.plugins.credentials.CredentialsScope;
import edu.umd.cs.findbugs.annotations.NonNull;
import hudson.Extension;
import io.akeyless.client.model.Auth;
import org.kohsuke.stapler.DataBoundConstructor;

public class AkeylessUniversalIdCredentials extends AkeylessTokenCredentials {

    @DataBoundConstructor
    public AkeylessUniversalIdCredentials(CredentialsScope scope, String id, String description) {
        super(scope, id, description);
    }

    @Override
    public CredentialsPayload getCredentialsPayload() {
        CredentialsPayload payload = new CredentialsPayload();
        Auth auth = new Auth();
        auth.setAccessType("universal_identity");
        auth.setUidToken(getToken().getPlainText());
        payload.setAuth(auth);
        return payload;
    }

    @Extension
    public static class DescriptorImpl extends BaseStandardCredentialsDescriptor {

        @NonNull
        @Override
        public String getDisplayName() {
            return "Akeyless Universal Identity";
        }
    }
}
