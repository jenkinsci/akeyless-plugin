package io.jenkins.plugins.akeyless.credentials;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

import hudson.util.Secret;
import io.akeyless.client.model.Auth;
import org.junit.jupiter.api.Test;

class AkeylessCertCredentialsTest {

    @Test
    void payloadContainsAuthWithCert() {
        AkeylessCertCredentials creds = new AkeylessCertCredentials(null, null, null);
        creds.setAccessId("id");
        creds.setCertificate("cert-data");
        creds.setPrivateKey(Secret.fromString("key"));

        CredentialsPayload payload = creds.getCredentialsPayload();
        Auth auth = payload.getAuth();
        assertThat(auth.getAccessId(), is("id"));
        assertThat(auth.getAccessType(), is("cert"));
        assertThat(auth.getCertData(), is("cert-data"));
        assertThat(auth.getKeyData(), is("key"));
    }
}
