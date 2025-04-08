package io.jenkins.plugins.akeyless.credentials;

import com.cloudbees.plugins.credentials.CredentialsScope;
import edu.umd.cs.findbugs.annotations.NonNull;
import hudson.Extension;
import hudson.util.Secret;
import io.akeyless.client.model.Auth;
import javax.annotation.CheckForNull;
import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.DataBoundSetter;

public class AkeylessK8SCredentials extends AbstractAkeylessBaseStandardCredentials implements AkeylessCredential {

    @NonNull
    @SuppressWarnings("lgtm[jenkins/plaintext-storage]")
    private String accessId = "";

    private String gatewayUrl;
    private String configName;
    private Secret serviceAccountToken;

    @DataBoundConstructor
    public AkeylessK8SCredentials(
            @CheckForNull CredentialsScope scope, @CheckForNull String id, @CheckForNull String description) {
        super(scope, id, description);
    }

    @NonNull
    public String getAccessId() {
        return accessId;
    }

    @DataBoundSetter
    public void setAccessId(@NonNull String accessId) {
        this.accessId = accessId;
    }

    @NonNull
    public String getGatewayUrl() {
        return gatewayUrl;
    }

    @DataBoundSetter
    public void setGatewayUrl(@NonNull String gatewayUrl) {
        this.gatewayUrl = gatewayUrl;
    }

    @NonNull
    public String getConfigName() {
        return configName;
    }

    @DataBoundSetter
    public void setConfigName(@NonNull String configName) {
        this.configName = configName;
    }

    @NonNull
    public Secret getServiceAccountToken() {
        return serviceAccountToken;
    }

    @DataBoundSetter
    public void setServiceAccountToken(@NonNull Secret serviceAccountToken) {
        this.serviceAccountToken = serviceAccountToken;
    }

    @Override
    public CredentialsPayload getCredentialsPayload() {
        CredentialsPayload payload = new CredentialsPayload();
        Auth auth = new Auth()
                .accessType("k8s")
                .accessId(accessId)
                .gatewayUrl(gatewayUrl)
                .k8sAuthConfigName(configName);
        if (serviceAccountToken != null) {
            auth.setK8sServiceAccountToken(serviceAccountToken.getPlainText());
        }
        payload.setAuth(auth);
        return payload;
    }

    @Extension
    public static class DescriptorImpl extends BaseStandardCredentialsDescriptor {

        @NonNull
        @Override
        public String getDisplayName() {
            return "Akeyless Kubernetes Credentials";
        }
    }
}
