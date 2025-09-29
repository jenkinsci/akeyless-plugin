package io.jenkins.plugins.akeyless.credentials;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

import hudson.util.Secret;
import org.junit.jupiter.api.Test;

class AkeylessTokenCredentialsTest {

    @Test
    void payloadContainsToken() {
        AkeylessTokenCredentials creds = new AkeylessTokenCredentials(null, null, null);
        Secret token = Secret.fromString("tkn");
        creds.setToken(token);
        CredentialsPayload payload = creds.getCredentialsPayload();
        assertThat(payload.getToken().getPlainText(), is("tkn"));
    }
}
