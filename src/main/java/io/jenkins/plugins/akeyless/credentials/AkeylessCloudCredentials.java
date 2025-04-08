package io.jenkins.plugins.akeyless.credentials;

import com.cloudbees.plugins.credentials.CredentialsScope;
import edu.umd.cs.findbugs.annotations.NonNull;
import hudson.Extension;
import hudson.model.Item;
import hudson.util.ListBoxModel;
import io.akeyless.client.model.Auth;
import javax.annotation.CheckForNull;
import org.kohsuke.accmod.Restricted;
import org.kohsuke.accmod.restrictions.NoExternalUse;
import org.kohsuke.stapler.AncestorInPath;
import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.DataBoundSetter;
import org.kohsuke.stapler.verb.POST;

public class AkeylessCloudCredentials extends AbstractAkeylessBaseStandardCredentials implements AkeylessCredential {

    @NonNull
    @SuppressWarnings("lgtm[jenkins/plaintext-storage]")
    private String accessId = "";

    @NonNull
    private String cloudType = "aws_iam";

    @DataBoundConstructor
    public AkeylessCloudCredentials(
            @CheckForNull CredentialsScope scope, @CheckForNull String id, @CheckForNull String description) {
        super(scope, id, description);
    }

    @NonNull
    public String getAccessId() {
        return accessId;
    }

    @NonNull
    public String getCloudType() {
        return cloudType;
    }

    @DataBoundSetter
    public void setAccessId(String accessId) {
        this.accessId = accessId;
    }

    @DataBoundSetter
    public void setCloudType(String cloudType) {
        this.cloudType = cloudType;
    }

    @Override
    public CredentialsPayload getCredentialsPayload() {
        CredentialsPayload payload = new CredentialsPayload();
        Auth auth = new Auth();
        auth.setAccessId(accessId);
        auth.setAccessType(cloudType);
        payload.setAuth(auth);
        payload.setCloudIdNeeded(true);
        return payload;
    }

    @Extension
    public static class DescriptorImpl extends BaseStandardCredentialsDescriptor {

        @NonNull
        @Override
        public String getDisplayName() {
            return "Akeyless Cloud Provider Credentials";
        }

        @POST
        @Restricted(NoExternalUse.class)
        public ListBoxModel doFillCloudTypeItems(@AncestorInPath Item context) {
            ListBoxModel options = new ListBoxModel(
                    new ListBoxModel.Option("AWS-IAM", "aws_iam"),
                    new ListBoxModel.Option("GCP", "gcp"),
                    new ListBoxModel.Option("Azure-AD", "azure_ad"));

            if (context != null) {
                ListBoxModel.Option option = new ListBoxModel.Option("Default", "");
                options.add(0, option);
            }
            return options;
        }
    }
}
