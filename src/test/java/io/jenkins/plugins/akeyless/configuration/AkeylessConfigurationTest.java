package io.jenkins.plugins.akeyless.configuration;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

import org.junit.jupiter.api.Test;

class AkeylessConfigurationTest {

    private enum ConfigEnum {
        ALL,
        URL_ONLY,
        CREDENTIALS_ONLY
    }

    @Test
    void handleNullConfiguration() {
        AkeylessConfiguration configuration = testConfig("test", ConfigEnum.ALL);
        AkeylessConfiguration result = configuration.mergeWithParent(null);
        assertThat(result.getAkeylessCredentialId(), is(result.getAkeylessCredentialId()));
        assertThat(result.getAkeylessUrl(), is(result.getAkeylessUrl()));
    }

    @Test
    void childShouldPartlyOverwriteParent() {
        AkeylessConfiguration parent = testConfig("parent", ConfigEnum.ALL);
        AkeylessConfiguration child = testConfig("child", ConfigEnum.URL_ONLY);
        AkeylessConfiguration result = child.mergeWithParent(parent);

        assertThat(result.getAkeylessCredentialId(), is(parent.getAkeylessCredentialId()));
        assertThat(result.getAkeylessUrl(), is(child.getAkeylessUrl()));

        parent = testConfig("parent", ConfigEnum.ALL);
        child = testConfig("child", ConfigEnum.CREDENTIALS_ONLY);
        result = child.mergeWithParent(parent);

        assertThat(result.getAkeylessCredentialId(), is(child.getAkeylessCredentialId()));
        assertThat(result.getAkeylessUrl(), is(parent.getAkeylessUrl()));
    }

    @Test
    void emptyParentShouldBeIgnored() {
        AkeylessConfiguration parent = new AkeylessConfiguration();
        AkeylessConfiguration child = testConfig("child", ConfigEnum.ALL);
        AkeylessConfiguration result = child.mergeWithParent(parent);

        assertThat(result.getAkeylessCredentialId(), is(child.getAkeylessCredentialId()));
        assertThat(result.getAkeylessUrl(), is(child.getAkeylessUrl()));
    }

    @Test
    void childShouldCompletelyOverwriteParent() {
        AkeylessConfiguration parent = testConfig("parent", ConfigEnum.ALL);
        AkeylessConfiguration child = testConfig("child", ConfigEnum.ALL);
        AkeylessConfiguration result = child.mergeWithParent(parent);

        assertThat(result.getAkeylessCredentialId(), is(child.getAkeylessCredentialId()));
        assertThat(result.getAkeylessUrl(), is(child.getAkeylessUrl()));
    }

    private static AkeylessConfiguration testConfig(String id, ConfigEnum confType) {
        AkeylessConfiguration configuration = new AkeylessConfiguration();
        switch (confType) {
            case ALL:
                configuration.setAkeylessUrl("https://akeyless.io/" + id);
                configuration.setAkeylessCredentialId(id);
                return configuration;
            case URL_ONLY:
                configuration.setAkeylessUrl("https://akeyless.io/" + id);
                return configuration;
            case CREDENTIALS_ONLY:
                configuration.setAkeylessCredentialId(id);
                return configuration;
        }
        throw new RuntimeException("Unknown config type: " + confType);
    }
}
