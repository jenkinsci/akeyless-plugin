package io.jenkins.plugins.akeyless.model;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

import java.util.Collections;
import org.junit.jupiter.api.Test;

class AkeylessSSHIssuerTest {

    @Test
    void gettersReturnConstructorValues() {
        AkeylessSecretValue value = new AkeylessSecretValue("data");
        AkeylessSSHIssuer issuer =
                new AkeylessSSHIssuer("/aaa/ssh", "name", "user", "pubkey", 3600L, Collections.singletonList(value));

        assertThat(issuer.getPath(), is("/aaa/ssh"));
        assertThat(issuer.getCertUserName(), is("user"));
        assertThat(issuer.getPublicKey(), is("pubkey"));
        assertThat(issuer.getTtl(), is(3600L));
        assertThat(issuer.getSecretValues().get(0).getSecretKey(), is("data"));
    }
}
