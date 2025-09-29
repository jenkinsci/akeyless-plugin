package io.jenkins.plugins.akeyless.model;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

import java.util.Collections;
import org.junit.jupiter.api.Test;

class AkeylessPKIIssuerTest {

    @Test
    void gettersReturnConstructorValues() {
        AkeylessSecretValue value = new AkeylessSecretValue("data");
        AkeylessPKIIssuer issuer = new AkeylessPKIIssuer(
                "/p/pki", "name", "user", "pubkey", "csr", 7200L, Collections.singletonList(value));

        assertThat(issuer.getPath(), is("/p/pki"));
        assertThat(issuer.getPublicKey(), is("pubkey"));
        assertThat(issuer.getCsrBase64(), is("csr"));
        assertThat(issuer.getTtl(), is(7200L));
        assertThat(issuer.getSecretValues().get(0).getSecretKey(), is("data"));
    }
}
