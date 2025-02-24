package io.jenkins.plugins.akeyless.credentials;

import com.cloudbees.plugins.credentials.CredentialsScope;
import edu.umd.cs.findbugs.annotations.NonNull;
import hudson.Extension;
import hudson.model.Item;
import hudson.util.ListBoxModel;
import io.akeyless.client.model.Auth;
import org.kohsuke.stapler.AncestorInPath;
import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.DataBoundSetter;

public class AkeylessCloudCredentials extends AbstractAkeylessBaseStandardCredentials implements AkeylessCredential {
    private String accessId;
    private String cloudType;

    @DataBoundConstructor
    public AkeylessCloudCredentials(CredentialsScope scope, String id, String description) {
        super(scope, id, description);
    }

    public String getAccessId() {
        return accessId;
    }

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

        public ListBoxModel doFillCloudTypeItems(@AncestorInPath Item context) {
            ListBoxModel options = new ListBoxModel(
                    new ListBoxModel.Option("AWS-IAM", "aws_iam"),
                    new ListBoxModel.Option("GCP", "gcp"),
                    new ListBoxModel.Option("Azure", "azure_ad"));

            if (context != null) {
                ListBoxModel.Option option = new ListBoxModel.Option("Default", "");
                options.add(0, option);
            }
            return options;
        }
    }
}
