package io.jenkins.plugins.akeyless.configuration;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import org.junit.Test;

public class AkeylessConfigurationTest {
    public static enum ConfigEnum {
        All,
        URLOnly,
        CredentialsOnly
    }

    @Test
    public void handleNullConfiguration() {
        AkeylessConfiguration configuration = testConfig("test", ConfigEnum.All);
        AkeylessConfiguration result = configuration.mergeWithParent(null);
        assertThat(result.getAkeylessCredentialId(), is(result.getAkeylessCredentialId()));
        assertThat(result.getAkeylessUrl(), is(result.getAkeylessUrl()));
    }

    @Test
    public void childShouldPartlyOverwriteParent() {
        AkeylessConfiguration parent = testConfig("parent", ConfigEnum.All);
        AkeylessConfiguration child = testConfig("child", ConfigEnum.URLOnly);
        AkeylessConfiguration result = child.mergeWithParent(parent);

        assertThat(result.getAkeylessCredentialId(), is(parent.getAkeylessCredentialId()));
        assertThat(result.getAkeylessUrl(), is(child.getAkeylessUrl()));

        parent = testConfig("parent", ConfigEnum.All);
        child = testConfig("child", ConfigEnum.CredentialsOnly);
        result = child.mergeWithParent(parent);

        assertThat(result.getAkeylessCredentialId(), is(child.getAkeylessCredentialId()));
        assertThat(result.getAkeylessUrl(), is(parent.getAkeylessUrl()));
    }

    @Test
    public void emptyParentShouldBeIgnored() {
        AkeylessConfiguration parent = new AkeylessConfiguration();
        AkeylessConfiguration child = testConfig("child", ConfigEnum.All);
        AkeylessConfiguration result = child.mergeWithParent(parent);

        assertThat(result.getAkeylessCredentialId(), is(child.getAkeylessCredentialId()));
        assertThat(result.getAkeylessUrl(), is(child.getAkeylessUrl()));
    }

    @Test
    public void childShouldCompletlyOverwriteParent() {
        AkeylessConfiguration parent = testConfig("parent", ConfigEnum.All);
        AkeylessConfiguration child = testConfig("child", ConfigEnum.All);
        AkeylessConfiguration result = child.mergeWithParent(parent);

        assertThat(result.getAkeylessCredentialId(), is(child.getAkeylessCredentialId()));
        assertThat(result.getAkeylessUrl(), is(child.getAkeylessUrl()));
    }

    public static AkeylessConfiguration testConfig(String id, ConfigEnum confType) {
        AkeylessConfiguration configuration = new AkeylessConfiguration();
        switch (confType) {
            case All:
                configuration.setAkeylessUrl("https://akeyless.io/" + id);
                configuration.setAkeylessCredentialId(id);
                return configuration;
            case URLOnly:
                configuration.setAkeylessUrl("https://akeyless.io/" + id);
                return configuration;
            case CredentialsOnly:
                configuration.setAkeylessCredentialId(id);
                return configuration;
        }
        throw new RuntimeException("Unknown config type: " + confType);
    }
}
