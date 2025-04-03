package io.jenkins.plugins.akeyless.credentials;

import com.cloudbees.plugins.credentials.CredentialsScope;
import edu.umd.cs.findbugs.annotations.NonNull;
import hudson.Extension;
import hudson.util.Secret;
import io.akeyless.client.model.Auth;
import javax.annotation.CheckForNull;
import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.DataBoundSetter;

/**
 * @author alexeydolgopyatov
 */
public class AccessKeyCredentialsImpl extends AbstractAkeylessBaseStandardCredentials implements AkeylessCredential {

    private Secret accessKey;

    // lgtm[jenkins/plaintext-storage]
    @NonNull
    private String accessId = "";

    @DataBoundConstructor
    public AccessKeyCredentialsImpl(
            @CheckForNull CredentialsScope scope, @CheckForNull String id, @CheckForNull String description) {
        super(scope, id, description);
    }

    public Secret getAccessKey() {
        return accessKey;
    }

    @DataBoundSetter
    public void setAccessKey(Secret accessKey) {
        this.accessKey = accessKey;
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
    public CredentialsScope getScope() {
        return null;
    }

    public Auth getAuth() {
        Auth auth = new Auth();
        auth.setAccessType("api_key");
        auth.setAccessKey(accessKey.getPlainText());
        auth.setAccessId(accessId);
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
            return "Akeyless API Key";
        }
    }
}
