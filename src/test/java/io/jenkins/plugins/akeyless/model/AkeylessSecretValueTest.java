package io.jenkins.plugins.akeyless.model;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

import org.junit.jupiter.api.Test;

class AkeylessSecretValueTest {

    @Test
    void getEnvVarDefaultsToSecretKey() {
        AkeylessSecretValue value = new AkeylessSecretValue("DB_PASSWORD");
        assertThat(value.getEnvVar(), is("DB_PASSWORD"));
    }

    @Test
    void getEnvVarUsesCustomEnvVarWhenProvided() {
        AkeylessSecretValue value = new AkeylessSecretValue("password");
        value.setEnvVar("DB_PASSWORD");
        assertThat(value.getEnvVar(), is("DB_PASSWORD"));
    }

    @Test
    void isRequiredDefaultsTrueAndCanBeChanged() {
        AkeylessSecretValue value = new AkeylessSecretValue("password");
        assertThat(value.getIsRequired(), is(true));
        value.setIsRequired(false);
        assertThat(value.getIsRequired(), is(false));
    }
}
