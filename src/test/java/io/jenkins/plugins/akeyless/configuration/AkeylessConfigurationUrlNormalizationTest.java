package io.jenkins.plugins.akeyless.configuration;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

import org.junit.jupiter.api.Test;

class AkeylessConfigurationUrlNormalizationTest {

    @Test
    void normalizeUrlShouldTrimTrailingSlash() {
        AkeylessConfiguration configuration = new AkeylessConfiguration();
        configuration.setAkeylessUrl("https://akeyless.io/");
        assertThat(configuration.getAkeylessUrl(), is("https://akeyless.io"));
    }

    @Test
    void normalizeUrlShouldPreserveIfNoTrailingSlash() {
        AkeylessConfiguration configuration = new AkeylessConfiguration();
        configuration.setAkeylessUrl("https://akeyless.io");
        assertThat(configuration.getAkeylessUrl(), is("https://akeyless.io"));
    }

    @Test
    void normalizeUrlShouldHandleNull() {
        AkeylessConfiguration configuration = new AkeylessConfiguration();
        configuration.setAkeylessUrl(null);
        assertThat(configuration.getAkeylessUrl(), is((String) null));
    }

    @Test
    void parentPoliciesShouldApplyWhenChildBlank() {
        AkeylessConfiguration parent = new AkeylessConfiguration();
        parent.setPolicies("team=parent");

        AkeylessConfiguration child = new AkeylessConfiguration();

        AkeylessConfiguration result = child.mergeWithParent(parent);
        assertThat(result.getPolicies(), is("team=parent"));
    }

    @Test
    void parentPoliciesShouldOverrideChildWhenDisabledOverride() {
        AkeylessConfiguration parent = new AkeylessConfiguration();
        parent.setPolicies("team=parent");
        parent.setDisableChildPoliciesOverride(true);

        AkeylessConfiguration child = new AkeylessConfiguration();
        child.setPolicies("team=child");

        AkeylessConfiguration result = child.mergeWithParent(parent);
        assertThat(result.getPolicies(), is("team=parent"));
    }
}
