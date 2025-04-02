package io.jenkins.plugins.akeyless.credentials;

import com.cloudbees.plugins.credentials.CredentialsScope;
import com.cloudbees.plugins.credentials.impl.BaseStandardCredentials;
import edu.umd.cs.findbugs.annotations.NonNull;
import hudson.model.ItemGroup;
import org.kohsuke.stapler.DataBoundSetter;

/**
 * @author alexeydolgopyatov
 */
public abstract class AbstractAkeylessBaseStandardCredentials extends BaseStandardCredentials
        implements AkeylessCredential {
    private String path;
    private transient ItemGroup context;

    AbstractAkeylessBaseStandardCredentials(CredentialsScope scope, String id, String description) {
        super(scope, id, description);
    }

    @NonNull
    public String getPath() {
        return path;
    }

    public String getSecretPath() {
        return getPath();
    }

    @DataBoundSetter
    public void setPath(String path) {
        this.path = path;
    }

    @DataBoundSetter
    public void setContext(@NonNull ItemGroup context) {
        this.context = context;
    }

    public ItemGroup getContext() {
        return this.context;
    }

    public String getDisplayName() {
        return this.path;
    }
}
