package io.jenkins.plugins.akeyless.credentials;

import hudson.util.Secret;
import io.akeyless.client.model.Auth;

public class CredentialsPayload {
    private Auth auth;
    private Secret token;
    private boolean isCloudIdNeeded;

    public Auth getAuth() {
        return auth;
    }

    public void setAuth(Auth auth) {
        this.auth = auth;
    }

    public Secret getToken() {
        return token;
    }

    public void setToken(Secret token) {
        this.token = token;
    }

    public boolean isCloudIdNeeded() {
        return isCloudIdNeeded;
    }

    public void setCloudIdNeeded(boolean cloudIdNeeded) {
        isCloudIdNeeded = cloudIdNeeded;
    }
}
