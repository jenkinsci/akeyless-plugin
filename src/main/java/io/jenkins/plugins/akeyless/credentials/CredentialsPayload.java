package io.jenkins.plugins.akeyless.credentials;

import io.akeyless.client.model.Auth;

public class CredentialsPayload {
    Auth auth;
    String token;
    boolean isCloudIdNeeded;

    public Auth getAuth() {
        return auth;
    }

    public void setAuth(Auth auth) {
        this.auth = auth;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public boolean isCloudIdNeeded() {
        return isCloudIdNeeded;
    }

    public void setCloudIdNeeded(boolean cloudIdNeeded) {
        isCloudIdNeeded = cloudIdNeeded;
    }
}
