package io.jenkins.plugins.akeyless.configuration;

import com.cloudbees.hudson.plugins.folder.AbstractFolder;
import com.cloudbees.hudson.plugins.folder.AbstractFolderProperty;
import edu.umd.cs.findbugs.annotations.NonNull;
import hudson.Extension;
import hudson.model.Item;
import hudson.model.ItemGroup;
import org.kohsuke.stapler.DataBoundConstructor;

/**
 * @author alexeydolgopyatov
 */
public class FolderAkeylessConfiguration extends AbstractFolderProperty<AbstractFolder<?>> {
    private final AkeylessConfiguration configuration;

    public FolderAkeylessConfiguration() {
        this.configuration = null;
    }

    @DataBoundConstructor
    public FolderAkeylessConfiguration(AkeylessConfiguration configuration) {
        this.configuration = configuration;
    }

    public AkeylessConfiguration getConfiguration() {
        return configuration;
    }

    @Extension(ordinal = 100)
    public static class ForJob extends AkeylessConfigResolver {

        @NonNull
        @Override
        public AkeylessConfiguration forJob(@NonNull Item job) {
            return getConfig(job.getParent());
        }

        @Override
        public AkeylessConfiguration getConfig(@NonNull ItemGroup itemGroup) {
            AkeylessConfiguration resultingConfig = null;
            for (ItemGroup g = itemGroup; g instanceof AbstractFolder; g = ((AbstractFolder) g).getParent()) {
                FolderAkeylessConfiguration folderProperty =
                        ((AbstractFolder<?>) g).getProperties().get(FolderAkeylessConfiguration.class);
                if (folderProperty == null) {
                    continue;
                }
                if (resultingConfig != null) {
                    resultingConfig = resultingConfig.mergeWithParent(folderProperty.getConfiguration());
                } else {
                    resultingConfig = folderProperty.getConfiguration();
                }
            }
            return resultingConfig;
        }
    }
}
