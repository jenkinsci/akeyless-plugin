package io.jenkins.plugins.akeyless.model;

import edu.umd.cs.findbugs.annotations.NonNull;
import hudson.Util;
import hudson.model.AbstractDescribableImpl;
import java.util.List;

/**
 * @author alexeydolgopyatov
 */
public abstract class AkeylessIssuer<T extends AbstractDescribableImpl<T>> extends AbstractDescribableImpl<T>
        implements AkeylessSecretBase {
    private String path;

    // lgtm[jenkins/plaintext-storage]
    private String publicKey;

    @NonNull
    private long ttl = 0;

    private List<AkeylessSecretValue> secretValues;

    public AkeylessIssuer(
            String path, String name, String publicKey, @NonNull long ttl, List<AkeylessSecretValue> secretValues) {
        this.path = Util.fixEmptyAndTrim(path);
        this.secretValues = secretValues;
        this.publicKey = publicKey;
        this.ttl = ttl;
    }

    public String getPath() {
        return this.path;
    }

    public String getPublicKey() {
        return this.publicKey;
    }

    public List<AkeylessSecretValue> getSecretValues() {
        return this.secretValues;
    }

    public long getTtl() {
        return this.ttl;
    }
}
