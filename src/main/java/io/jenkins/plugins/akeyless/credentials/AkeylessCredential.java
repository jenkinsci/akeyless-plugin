package io.jenkins.plugins.akeyless.credentials;

import com.cloudbees.plugins.credentials.CredentialsNameProvider;
import com.cloudbees.plugins.credentials.NameWith;
import com.cloudbees.plugins.credentials.common.StandardCredentials;
import edu.umd.cs.findbugs.annotations.NonNull;
import java.io.Serializable;

/**
 * @author alexeydolgopyatov
 */
@NameWith(AkeylessCredential.NameProvider.class)
public interface AkeylessCredential extends StandardCredentials, Serializable {
    public CredentialsPayload getCredentialsPayload();

    class NameProvider extends CredentialsNameProvider<AkeylessCredential> {
        @NonNull
        public String getName(@NonNull AkeylessCredential credentials) {
            return credentials.getDescription();
        }
    }
}
