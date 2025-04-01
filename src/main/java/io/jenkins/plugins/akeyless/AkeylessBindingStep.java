package io.jenkins.plugins.akeyless;

import com.google.common.annotations.VisibleForTesting;
import edu.umd.cs.findbugs.annotations.CheckForNull;
import edu.umd.cs.findbugs.annotations.NonNull;
import hudson.EnvVars;
import hudson.Extension;
import hudson.console.ConsoleLogFilter;
import hudson.model.Run;
import hudson.model.TaskListener;
import hudson.util.Secret;
import io.jenkins.plugins.akeyless.configuration.AkeylessConfiguration;
import io.jenkins.plugins.akeyless.model.AkeylessPKIIssuer;
import io.jenkins.plugins.akeyless.model.AkeylessSSHIssuer;
import io.jenkins.plugins.akeyless.model.AkeylessSecret;
import java.io.IOException;
import java.util.*;
import org.jenkinsci.plugins.workflow.steps.*;
import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.DataBoundSetter;

/**
 * @author alexeydolgopyatov
 */
public class AkeylessBindingStep extends Step {

    private AkeylessConfiguration configuration;
    private List<AkeylessSecret> akeylessSecrets;
    private List<AkeylessPKIIssuer> akeylessPKIIssuers;
    private List<AkeylessSSHIssuer> akeylessSSHIssuers;

    @DataBoundConstructor
    public AkeylessBindingStep(
            @CheckForNull List<AkeylessSecret> akeylessSecrets,
            @CheckForNull List<AkeylessPKIIssuer> akeylessPKIIssuers,
            @CheckForNull List<AkeylessSSHIssuer> akeylessSSHIssuers) {
        this.akeylessSecrets = akeylessSecrets;
        this.akeylessPKIIssuers = akeylessPKIIssuers;
        this.akeylessSSHIssuers = akeylessSSHIssuers;
    }

    public List<AkeylessSecret> getAkeylessSecrets() {
        return akeylessSecrets;
    }

    public List<AkeylessPKIIssuer> getAkeylessPKIIssuers() {
        return akeylessPKIIssuers;
    }

    public List<AkeylessSSHIssuer> getAkeylessSSHIssuers() {
        return akeylessSSHIssuers;
    }

    @DataBoundSetter
    public void setConfiguration(AkeylessConfiguration configuration) {
        this.configuration = configuration;
    }

    public AkeylessConfiguration getConfiguration() {
        return configuration;
    }

    @DataBoundSetter
    public void setAkeylessSecrets(List<AkeylessSecret> akeylessSecrets) {
        this.akeylessSecrets = akeylessSecrets;
    }

    @DataBoundSetter
    public void setAkeylessPKIIssuers(List<AkeylessPKIIssuer> akeylessPKIIssuers) {
        this.akeylessPKIIssuers = akeylessPKIIssuers;
    }

    @DataBoundSetter
    public void setAkeylessSSHIssuers(List<AkeylessSSHIssuer> akeylessSSHIssuers) {
        this.akeylessSSHIssuers = akeylessSSHIssuers;
    }

    @Override
    public StepExecution start(StepContext context) throws Exception {
        return new Execution(this, context);
    }

    protected static class Execution extends GeneralNonBlockingStepExecution {

        private static final long serialVersionUID = 1;

        private transient AkeylessBindingStep step;
        private transient AkeylessAccessor akeylessAccessor;

        public Execution(AkeylessBindingStep step, StepContext context) {
            super(context);
            this.step = step;
        }

        @VisibleForTesting
        public void setAkeylessAccessor(AkeylessAccessor akeylessAccessor) {
            this.akeylessAccessor = akeylessAccessor;
        }

        @Override
        public boolean start() throws Exception {
            run(this::doStart);
            return false;
        }

        private void doStart() throws Exception {
            Run<?, ?> run = getContext().get(Run.class);
            TaskListener listener = getContext().get(TaskListener.class);
            EnvVars envVars = getContext().get(EnvVars.class);

            Map<String, String> overrides = AkeylessAccessor.retrieveSecrets(
                    run,
                    listener.getLogger(),
                    envVars,
                    akeylessAccessor,
                    step.getConfiguration(),
                    step.getAkeylessSecrets(),
                    step.getAkeylessPKIIssuers(),
                    step.getAkeylessSSHIssuers());

            List<String> secretValues = new ArrayList<>();
            secretValues.addAll(overrides.values());

            getContext()
                    .newBodyInvoker()
                    .withContext(EnvironmentExpander.merge(
                            getContext().get(EnvironmentExpander.class), new Overrider(overrides)))
                    .withContext(BodyInvoker.mergeConsoleLogFilters(
                            getContext().get(ConsoleLogFilter.class),
                            new MaskSecretsLogsFilter(run.getCharset().name(), secretValues)))
                    .withCallback(new Callback())
                    .start();
        }
    }

    private static final class Overrider extends EnvironmentExpander {

        private static final long serialVersionUID = 1;

        private final Map<String, Secret> overrides = new HashMap<String, Secret>();

        Overrider(Map<String, String> overrides) {
            for (Map.Entry<String, String> override : overrides.entrySet()) {
                this.overrides.put(override.getKey(), Secret.fromString(override.getValue()));
            }
        }

        @Override
        public void expand(EnvVars env) throws IOException, InterruptedException {
            for (Map.Entry<String, Secret> override : overrides.entrySet()) {
                env.override(override.getKey(), override.getValue().getPlainText());
            }
        }

        @Override
        public Set<String> getSensitiveVariables() {
            return Collections.unmodifiableSet(overrides.keySet());
        }
    }

    private static class Callback extends BodyExecutionCallback.TailCall {

        @Override
        protected void finished(StepContext context) throws Exception {}
    }

    @Extension
    public static final class DescriptorImpl extends StepDescriptor {

        @Override
        public Set<? extends Class<?>> getRequiredContext() {
            return Collections.unmodifiableSet(
                    new HashSet<>(Arrays.asList(TaskListener.class, Run.class, EnvVars.class)));
        }

        @Override
        public boolean takesImplicitBlockArgument() {
            return true;
        }

        @Override
        public String getFunctionName() {
            return "withAkeyless";
        }

        @NonNull
        @Override
        public String getDisplayName() {
            return "Akeyless Plugin";
        }
    }
}
