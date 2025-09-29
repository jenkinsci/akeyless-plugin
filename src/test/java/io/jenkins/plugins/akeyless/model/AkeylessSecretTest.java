package io.jenkins.plugins.akeyless.model;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

import java.util.Collections;
import org.junit.jupiter.api.Test;

class AkeylessSecretTest {

    @Test
    void gettersReturnConstructorValues() {
        AkeylessSecretValue value = new AkeylessSecretValue("key");
        AkeylessSecret secret = new AkeylessSecret("/path/to/secret", Collections.singletonList(value));
        assertThat(secret.getPath(), is("/path/to/secret"));
        assertThat(secret.getSecretValues().size(), is(1));
        assertThat(secret.getSecretValues().get(0).getSecretKey(), is("key"));
    }
}
