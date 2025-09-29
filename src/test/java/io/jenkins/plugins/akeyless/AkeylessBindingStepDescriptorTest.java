package io.jenkins.plugins.akeyless;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

import org.junit.jupiter.api.Test;

class AkeylessBindingStepDescriptorTest {

    @Test
    void descriptorProperties() {
        AkeylessBindingStep.DescriptorImpl d = new AkeylessBindingStep.DescriptorImpl();
        assertThat(d.getFunctionName(), is("withAkeyless"));
        assertThat(d.getDisplayName(), is("Akeyless Plugin"));
        assertThat(d.takesImplicitBlockArgument(), is(true));
    }
}
