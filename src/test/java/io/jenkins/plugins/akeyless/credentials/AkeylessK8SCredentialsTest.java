package io.jenkins.plugins.akeyless.credentials;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

import hudson.util.Secret;
import io.akeyless.client.model.Auth;
import org.junit.jupiter.api.Test;

class AkeylessK8SCredentialsTest {

    @Test
    void payloadContainsAuthWithK8sFields() {
        AkeylessK8SCredentials creds = new AkeylessK8SCredentials(null, null, null);
        creds.setAccessId("id");
        creds.setGatewayUrl("https://gw");
        creds.setConfigName("conf");
        creds.setServiceAccountToken(Secret.fromString("tok"));

        CredentialsPayload payload = creds.getCredentialsPayload();
        Auth auth = payload.getAuth();
        assertThat(auth.getAccessId(), is("id"));
        assertThat(auth.getAccessType(), is("k8s"));
        assertThat(auth.getGatewayUrl(), is("https://gw"));
        assertThat(auth.getK8sAuthConfigName(), is("conf"));
        assertThat(auth.getK8sServiceAccountToken(), is("tok"));
    }
}
