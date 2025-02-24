package io.jenkins.plugins.akeyless;

import com.google.common.annotations.VisibleForTesting;
import edu.umd.cs.findbugs.annotations.CheckForNull;
import edu.umd.cs.findbugs.annotations.NonNull;
import hudson.*;
import hudson.console.ConsoleLogFilter;
import hudson.model.AbstractProject;
import hudson.model.Run;
import hudson.model.TaskListener;
import hudson.tasks.BuildWrapperDescriptor;
import io.jenkins.plugins.akeyless.configuration.AkeylessConfigResolver;
import io.jenkins.plugins.akeyless.configuration.AkeylessConfiguration;
import io.jenkins.plugins.akeyless.model.AkeylessIssuer;
import io.jenkins.plugins.akeyless.model.AkeylessSecret;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import jenkins.tasks.SimpleBuildWrapper;
import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.DataBoundSetter;

/**
 * @author alexeydolgopyatov
 */
public class AkeylessBuildWrapper extends SimpleBuildWrapper {
    protected transient PrintStream logger;
    private List<AkeylessSecret> akeylessSecrets;
    private transient AkeylessAccessor accessor;
    private AkeylessConfiguration configuration;
    private List<String> valuesToMask = new ArrayList<>();
    private List<AkeylessIssuer> akeylessIssuers;

    @DataBoundConstructor
    public AkeylessBuildWrapper(
            @CheckForNull List<AkeylessSecret> akeylessSecrets, @CheckForNull List<AkeylessIssuer> akeylessIssuers) {
        this.akeylessSecrets = akeylessSecrets;
        this.akeylessIssuers = akeylessIssuers;
    }

    public List<AkeylessSecret> getAkeylessSecrets() {
        return this.akeylessSecrets;
    }

    public List<AkeylessIssuer> getAkeylessIssuers() {
        return this.akeylessIssuers;
    }

    @DataBoundSetter
    public void setConfiguration(AkeylessConfiguration configuration) {
        this.configuration = configuration;
    }

    public AkeylessConfiguration getConfiguration() {
        return this.configuration;
    }

    @VisibleForTesting
    public void setAkeylessAccessor(AkeylessAccessor accessor) {
        this.accessor = accessor;
    }

    @Override
    public void setUp(
            Context context,
            Run<?, ?> build,
            FilePath workspace,
            Launcher launcher,
            TaskListener listener,
            EnvVars initialEnvironment)
            throws IOException, InterruptedException {
        logger = listener.getLogger();
        buildConfiguration(build);
        retrieveSecretsAndSetToEnvironments(context, build, initialEnvironment);
    }

    private void retrieveSecretsAndSetToEnvironments(Context context, Run<?, ?> build, EnvVars envVars) {
        Map<String, String> overrides = AkeylessAccessor.retrieveSecrets(
                build, logger, envVars, accessor, getConfiguration(), getAkeylessSecrets(), getAkeylessIssuers());

        for (Map.Entry<String, String> secret : overrides.entrySet()) {
            valuesToMask.add(secret.getValue());
            context.env(secret.getKey(), secret.getValue());
        }
    }

    private void buildConfiguration(Run<?, ?> build) {
        for (AkeylessConfigResolver resolver : ExtensionList.lookup(AkeylessConfigResolver.class)) {
            if (configuration != null) {
                configuration = configuration.mergeWithParent(resolver.forJob(build.getParent()));
            } else {
                configuration = resolver.forJob(build.getParent());
            }
        }
        if (configuration == null) {
            throw new AkeylessPluginException("No configuration found - please configure the AkeylessPlugin.");
        }
    }

    @Override
    public ConsoleLogFilter createLoggerDecorator(@NonNull final Run<?, ?> build) {
        return new MaskSecretsLogsFilter(build.getCharset().name(), valuesToMask);
    }

    @Extension
    public static final class DescriptorImpl extends BuildWrapperDescriptor {

        public DescriptorImpl() {
            super(AkeylessBuildWrapper.class);
            load();
        }

        @Override
        public boolean isApplicable(AbstractProject<?, ?> item) {
            return true;
        }

        /**
         * This human-readable name is used in the configuration screen.
         */
        @Override
        public String getDisplayName() {
            return "Akeyless Plugin";
        }
    }
}
