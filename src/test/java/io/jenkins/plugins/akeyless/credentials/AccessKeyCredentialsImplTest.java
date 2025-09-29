package io.jenkins.plugins.akeyless.credentials;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

import hudson.util.Secret;
import io.akeyless.client.model.Auth;
import org.junit.jupiter.api.Test;

class AccessKeyCredentialsImplTest {

    @Test
    void payloadContainsAuthWithApiKey() {
        AccessKeyCredentialsImpl creds = new AccessKeyCredentialsImpl(null, null, null);
        creds.setAccessId("id");
        creds.setAccessKey(Secret.fromString("key"));

        CredentialsPayload payload = creds.getCredentialsPayload();
        Auth auth = payload.getAuth();
        assertThat(auth.getAccessId(), is("id"));
        assertThat(auth.getAccessType(), is("api_key"));
        assertThat(auth.getAccessKey(), is("key"));
    }
}
