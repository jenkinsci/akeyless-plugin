package io.jenkins.plugins.akeyless.model;

import java.util.List;

public interface AkeylessSecretBase {
    public List<AkeylessSecretValue> getSecretValues();

    public String getPath();
}
