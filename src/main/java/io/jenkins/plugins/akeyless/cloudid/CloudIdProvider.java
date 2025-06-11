package io.jenkins.plugins.akeyless.cloudid;

public interface CloudIdProvider {
    public String getCloudId() throws Exception;
}
