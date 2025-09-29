package io.jenkins.plugins.akeyless;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.google.gson.internal.LinkedTreeMap;
import hudson.EnvVars;
import io.jenkins.plugins.akeyless.model.AkeylessSecret;
import io.jenkins.plugins.akeyless.model.AkeylessSecretBase;
import io.jenkins.plugins.akeyless.model.AkeylessSecretValue;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.*;
import org.junit.jupiter.api.Test;

class AkeylessAccessorFillObjectValuesTest {

    private static PrintStream logger() {
        return new PrintStream(new ByteArrayOutputStream());
    }

    @Test
    void fillsValuesFromPlainStringSecret() {
        String path = "/s/plain";
        AkeylessSecretValue value = new AkeylessSecretValue("data");
        AkeylessSecret secret = new AkeylessSecret(path, Collections.singletonList(value));

        AkeylessAccessor fake = new AkeylessAccessor(null, null) {
            @Override
            public Map<String, Object> getSecret(String token, AkeylessSecretBase akeylessSecret) {
                Map<String, Object> values = new HashMap<>();
                values.put(path, "hello\nworld");
                return values;
            }
        };

        Map<String, String> env = new HashMap<>();
        AkeylessAccessor.fillObjectValues(logger(), new EnvVars(), fake, "tkn", Collections.singletonList(secret), env);

        assertThat(env.get("data"), is("hello\nworld"));
    }

    @Test
    void fillsValuesFromStructuredSecret() {
        String path = "/s/json";
        AkeylessSecretValue value = new AkeylessSecretValue("username");
        value.setEnvVar("DB_USER");
        AkeylessSecret secret = new AkeylessSecret(path, Collections.singletonList(value));

        AkeylessAccessor fake = new AkeylessAccessor(null, null) {
            @Override
            public Map<String, Object> getSecret(String token, AkeylessSecretBase akeylessSecret) {
                LinkedTreeMap<String, Object> inner = new LinkedTreeMap<>();
                inner.put("username", "admin");
                Map<String, Object> values = new HashMap<>();
                values.put(path, inner);
                return values;
            }
        };

        Map<String, String> env = new HashMap<>();
        AkeylessAccessor.fillObjectValues(logger(), new EnvVars(), fake, "tkn", Collections.singletonList(secret), env);

        assertThat(env.get("DB_USER"), is("admin"));
    }

    @Test
    void throwsWhenRequiredValueMissing() {
        String path = "/s/missing";
        AkeylessSecretValue value = new AkeylessSecretValue("password");
        // value is required by default
        AkeylessSecret secret = new AkeylessSecret(path, Collections.singletonList(value));

        AkeylessAccessor fake = new AkeylessAccessor(null, null) {
            @Override
            public Map<String, Object> getSecret(String token, AkeylessSecretBase akeylessSecret) {
                LinkedTreeMap<String, Object> inner = new LinkedTreeMap<>();
                inner.put("username", "admin");
                Map<String, Object> values = new HashMap<>();
                values.put(path, inner);
                return values;
            }
        };

        Map<String, String> env = new HashMap<>();
        assertThrows(
                IllegalArgumentException.class,
                () -> AkeylessAccessor.fillObjectValues(
                        logger(), new EnvVars(), fake, "tkn", Collections.singletonList(secret), env));
    }
}
