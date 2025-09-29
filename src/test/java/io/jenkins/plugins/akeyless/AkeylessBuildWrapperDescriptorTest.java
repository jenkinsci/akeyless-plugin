package io.jenkins.plugins.akeyless;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

import org.junit.jupiter.api.Test;

class AkeylessBuildWrapperDescriptorTest {

    @Test
    void descriptorProperties() {
        AkeylessBuildWrapper.DescriptorImpl d = new AkeylessBuildWrapper.DescriptorImpl();
        assertThat(d.getDisplayName(), is("Akeyless Plugin"));
    }
}
