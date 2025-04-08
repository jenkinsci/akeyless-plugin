package io.jenkins.plugins.akeyless.credentials;

import com.cloudbees.plugins.credentials.CredentialsScope;
import edu.umd.cs.findbugs.annotations.NonNull;
import hudson.util.Secret;
import javax.annotation.CheckForNull;
import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.DataBoundSetter;

public class AkeylessTokenCredentials extends AbstractAkeylessBaseStandardCredentials implements AkeylessCredential {
    private Secret token;

    @DataBoundConstructor
    public AkeylessTokenCredentials(
            @CheckForNull CredentialsScope scope, @CheckForNull String id, @CheckForNull String description) {
        super(scope, id, description);
    }

    @NonNull
    public Secret getToken() {
        return token;
    }

    @DataBoundSetter
    public void setToken(Secret token) {
        this.token = token;
    }

    @Override
    public CredentialsPayload getCredentialsPayload() {
        CredentialsPayload payload = new CredentialsPayload();
        payload.setToken(getToken());
        return payload;
    }
}
