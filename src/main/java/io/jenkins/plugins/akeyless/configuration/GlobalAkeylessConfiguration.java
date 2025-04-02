package io.jenkins.plugins.akeyless.configuration;

import edu.umd.cs.findbugs.annotations.NonNull;
import hudson.Extension;
import hudson.ExtensionList;
import hudson.model.Item;
import hudson.model.ItemGroup;
import jenkins.model.GlobalConfiguration;
import net.sf.json.JSONObject;
import org.jenkinsci.Symbol;
import org.kohsuke.stapler.DataBoundSetter;
import org.kohsuke.stapler.StaplerRequest;

/**
 * @author alexeydolgopyatov
 */
@Extension
@Symbol("akeyless")
public class GlobalAkeylessConfiguration extends GlobalConfiguration {
    private AkeylessConfiguration configuration;

    public static GlobalAkeylessConfiguration get() {
        return ExtensionList.lookupSingleton(GlobalAkeylessConfiguration.class);
    }

    public GlobalAkeylessConfiguration() {
        load();
    }

    @Override
    public boolean configure(StaplerRequest req, JSONObject json) throws FormException {
        req.bindJSON(this, json);
        return true;
    }

    public AkeylessConfiguration getConfiguration() {
        return configuration;
    }

    @DataBoundSetter
    public void setConfiguration(AkeylessConfiguration configuration) {
        this.configuration = configuration;
        save();
    }

    @Extension(ordinal = 0)
    public static class ForJob extends AkeylessConfigResolver {

        @NonNull
        @Override
        public AkeylessConfiguration forJob(@NonNull Item job) {
            return getConfig(job.getParent());
        }

        @Override
        public AkeylessConfiguration getConfig(@NonNull ItemGroup itemGroup) {
            return GlobalAkeylessConfiguration.get().getConfiguration();
        }
    }

    protected Object readResolve() {
        return this;
    }
}
