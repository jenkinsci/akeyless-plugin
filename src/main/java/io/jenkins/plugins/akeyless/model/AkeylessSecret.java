package io.jenkins.plugins.akeyless.model;

import com.google.common.base.Strings;
import hudson.Extension;
import hudson.model.AbstractDescribableImpl;
import hudson.model.Descriptor;
import hudson.model.Item;
import hudson.model.Saveable;
import hudson.util.FormValidation;
import java.util.List;
import org.kohsuke.accmod.Restricted;
import org.kohsuke.accmod.restrictions.NoExternalUse;
import org.kohsuke.stapler.AncestorInPath;
import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.QueryParameter;
import org.kohsuke.stapler.verb.POST;

/**
 * @author alexeydolgopyatov
 */
public class AkeylessSecret extends AbstractDescribableImpl<AkeylessSecret> implements AkeylessSecretBase {

    private final String path;
    private List<AkeylessSecretValue> secretValues;

    @DataBoundConstructor
    public AkeylessSecret(String path, List<AkeylessSecretValue> secretValues) {
        this.path = path;
        this.secretValues = secretValues;
    }

    public String getPath() {
        return this.path;
    }

    public List<AkeylessSecretValue> getSecretValues() {
        return this.secretValues;
    }

    @Extension
    public static final class DescriptorImpl extends Descriptor<AkeylessSecret> implements Saveable {

        @Override
        public String getDisplayName() {
            return "Akeyless Secret";
        }

        @POST
        @Restricted(NoExternalUse.class)
        public FormValidation doCheckPath(@AncestorInPath Item item, @QueryParameter String value) {
            if (item != null && !item.hasPermission(Item.CONFIGURE)) {
                // User does not have the necessary permission
                return FormValidation.error("You do not have permission to configure this item.");
            }

            if (!Strings.isNullOrEmpty(value)) {
                return FormValidation.ok();
            } else {
                return FormValidation.error("This field can not be empty");
            }
        }
    }
}
