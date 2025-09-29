package io.jenkins.plugins.akeyless.credentials;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

import io.akeyless.client.model.Auth;
import org.junit.jupiter.api.Test;

class AkeylessCloudCredentialsTest {

    @Test
    void payloadContainsAuthWithCloudTypeAndRequiresCloudId() {
        AkeylessCloudCredentials creds = new AkeylessCloudCredentials(null, null, null);
        creds.setAccessId("id");
        creds.setCloudType("aws_iam");

        CredentialsPayload payload = creds.getCredentialsPayload();
        Auth auth = payload.getAuth();
        assertThat(auth.getAccessId(), is("id"));
        assertThat(auth.getAccessType(), is("aws_iam"));
        assertThat(payload.isCloudIdNeeded(), is(true));
    }
}
