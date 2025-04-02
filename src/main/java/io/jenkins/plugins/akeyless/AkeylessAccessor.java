package io.jenkins.plugins.akeyless;

import com.cloudbees.plugins.credentials.CredentialsMatchers;
import com.cloudbees.plugins.credentials.CredentialsProvider;
import com.cloudbees.plugins.credentials.CredentialsUnavailableException;
import com.cloudbees.plugins.credentials.matchers.IdMatcher;
import com.google.gson.Gson;
import com.google.gson.internal.LinkedTreeMap;
import hudson.EnvVars;
import hudson.ExtensionList;
import hudson.model.Run;
import hudson.security.ACL;
import io.akeyless.client.ApiClient;
import io.akeyless.client.ApiException;
import io.akeyless.client.Configuration;
import io.akeyless.client.api.V2Api;
import io.akeyless.client.model.*;
import io.akeyless.cloudid.CloudIdProvider;
import io.akeyless.cloudid.CloudProviderFactory;
import io.jenkins.plugins.akeyless.configuration.AkeylessConfigResolver;
import io.jenkins.plugins.akeyless.configuration.AkeylessConfiguration;
import io.jenkins.plugins.akeyless.credentials.AkeylessCredential;
import io.jenkins.plugins.akeyless.credentials.CredentialsPayload;
import io.jenkins.plugins.akeyless.model.*;
import java.io.PrintStream;
import java.io.Serializable;
import java.nio.charset.StandardCharsets;
import java.util.*;
import javax.annotation.Nonnull;
import jenkins.model.Jenkins;
import net.sf.json.JSONObject;
import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.lang.StringUtils;

/**
 * @author alexeydolgopyatov
 */
public class AkeylessAccessor implements Serializable {
    private static final long serialVersionUID = 1L;
    private transient V2Api api;
    private final AkeylessCredential credential;
    public static final String DATA_KEY = "data";
    private static final Gson gson = new Gson();

    public AkeylessAccessor(V2Api api, AkeylessCredential credential) {
        this.api = api;
        this.credential = credential;
    }

    public V2Api getApi() {
        return api;
    }

    public static Map<String, String> retrieveSecrets(
            Run<?, ?> run,
            PrintStream logger,
            EnvVars envVars,
            AkeylessAccessor accessor,
            AkeylessConfiguration configuration,
            List<AkeylessSecret> akeylessSecrets,
            List<AkeylessPKIIssuer> akeylessPKIIssuers,
            List<AkeylessSSHIssuer> akeylessSSHIssuers) {
        Map<String, String> secrets = new HashMap<>();
        AkeylessConfiguration config = pullAndMergeConfiguration(run, configuration);
        String url = config.getAkeylessUrl();

        if (StringUtils.isBlank(url)) {
            throw new AkeylessPluginException(
                    "The Akeyless url was not configured - please specify the Akeyless url to use.");
        }
        AkeylessCredential credential = config.getAkeylessCredential();
        if (credential == null) {
            credential = retrieveAkeylessCredentials(run, config);
        }
        if (credential == null) {
            throw new AkeylessPluginException("Failed to retrieve Akeyless credential");
        }

        ApiClient client = Configuration.getDefaultApiClient();
        client.setBasePath(url);
        client.setVerifyingSsl(!config.getSkipSslVerification());
        client.setConnectTimeout(config.getTimeout());
        V2Api api = new V2Api(client);
        accessor = new AkeylessAccessor(api, credential);

        CredentialsPayload payload = credential.getCredentialsPayload();
        // authenticate
        String token = payload.getToken();
        try {
            if (token == null || token.isEmpty()) {
                Auth auth = payload.getAuth();
                if (payload.isCloudIdNeeded()) {
                    CloudIdProvider idProvider = CloudProviderFactory.getCloudIdProvider(auth.getAccessType());
                    auth.setCloudId(idProvider.getCloudId());
                }
                token = api.auth(auth).getToken();
            }
        } catch (ApiException e) {
            throw new AkeylessPluginException("Authentication failed: " + e.getResponseBody(), e);
        } catch (Exception e) {
            throw new AkeylessPluginException("Authentication failed.", e);
        }
        fillObjectValues(logger, envVars, accessor, token, akeylessSecrets, secrets);
        fillObjectValues(logger, envVars, accessor, token, akeylessPKIIssuers, secrets);
        fillObjectValues(logger, envVars, accessor, token, akeylessSSHIssuers, secrets);
        return secrets;
    }

    public static void fillObjectValues(
            PrintStream logger,
            EnvVars envVars,
            AkeylessAccessor accessor,
            String token,
            List<? extends AkeylessSecretBase> akeylessSecrets,
            Map<String, String> secrets) {
        if (secrets == null) {
            throw new AkeylessPluginException("Akeyless secrets holder should be initialized.");
        }
        if (akeylessSecrets == null || akeylessSecrets.isEmpty()) {
            return;
        }
        for (AkeylessSecretBase akeylessSecret : akeylessSecrets) {
            String path = envVars.expand(akeylessSecret.getPath());
            logger.printf("Retrieving secret: %s%n", path);

            Map<String, Object> values = accessor.getSecret(token, akeylessSecret);
            // static secret can be flat String in non JSON format
            Object tempVal = values.get(path);
            if (tempVal instanceof String) {
                tempVal = StringEscapeUtils.escapeJava(tempVal.toString());
                String newVal = "{\"data\": \"" + tempVal + "\"}";
                tempVal = gson.fromJson(JSONObject.fromObject(newVal).toString(), LinkedTreeMap.class);
            }
            Map<String, Object> innerValues = fillDataValues((LinkedTreeMap) tempVal, values);
            for (AkeylessSecretValue value : akeylessSecret.getSecretValues()) {
                String key = value.getSecretKey();
                Object secret = innerValues.get(key);
                if (secret == null && value.getIsRequired()) {
                    throw new IllegalArgumentException("Required secret " + key + " at " + path
                            + " is either null or empty. Please check the Secret name and type in Akeyless.");
                }
                if (secret != null) {
                    secrets.put(value.getEnvVar(), secret.toString());
                }
            }
            logger.printf("Retrieving secret: %s -- SUCCESS.%n", path);
        }
    }

    private static Map<String, Object> fillDataValues(LinkedTreeMap innerValues, Map<String, Object> values) {
        if (innerValues == null) {
            innerValues = (LinkedTreeMap) values.get("value");
            if (innerValues == null) {
                innerValues = new LinkedTreeMap<>();
                innerValues.putAll(values);
            }
        }
        if (innerValues.get(DATA_KEY) == null) {
            innerValues.put(DATA_KEY, JSONObject.fromObject(innerValues).toString());
        }
        return innerValues;
    }

    public static AkeylessCredential retrieveAkeylessCredentials(Run build, AkeylessConfiguration config) {
        if (Jenkins.getInstanceOrNull() != null) {
            String id = config.getAkeylessCredentialId();
            if (StringUtils.isEmpty(id)) {
                throw new AkeylessPluginException(
                        "The credential id was not configured - please specify the credentials to use.");
            }
            List<AkeylessCredential> credentials = CredentialsProvider.lookupCredentials(
                    AkeylessCredential.class, build.getParent(), ACL.SYSTEM, Collections.emptyList());
            AkeylessCredential credential = CredentialsMatchers.firstOrNull(credentials, new IdMatcher(id));

            if (credential == null) {
                throw new CredentialsUnavailableException(id);
            }

            return credential;
        }

        return null;
    }

    public static AkeylessConfiguration pullAndMergeConfiguration(
            Run<?, ?> build, AkeylessConfiguration buildConfiguration) {
        AkeylessConfiguration configuration = buildConfiguration;
        for (AkeylessConfigResolver resolver : ExtensionList.lookup(AkeylessConfigResolver.class)) {
            if (configuration != null) {
                configuration = configuration.mergeWithParent(resolver.forJob(build.getParent()));
            } else {
                configuration = resolver.forJob(build.getParent());
            }
        }
        if (configuration == null) {
            throw new AkeylessPluginException("No configuration found - please configure the Akeyless Plugin.");
        }
        return configuration;
    }

    public Map<String, Object> getSecret(String token, AkeylessSecretBase akeylessSecret) {
        DescribeItem describeItem = new DescribeItem();
        describeItem.setToken(token);
        describeItem.setName(akeylessSecret.getPath());
        String type = null;
        try {
            Item item = getApi().describeItem(describeItem);
            type = item.getItemType();
        } catch (ApiException e) {
            throw new AkeylessPluginException("Failed to describe item: " + e.getResponseBody(), e);
        }
        try {
            switch (type) {
                case "STATIC_SECRET":
                    GetSecretValue body = new GetSecretValue();
                    body.setToken(token);
                    body.json(true);
                    List<String> paths = Collections.singletonList(akeylessSecret.getPath());
                    body.names(paths);
                    body.setPrettyPrint(true);
                    return getApi().getSecretValue(body);
                case "DYNAMIC_SECRET":
                    GetDynamicSecretValue dbody = new GetDynamicSecretValue();
                    dbody.setToken(token);
                    dbody.json(true);
                    dbody.setName(akeylessSecret.getPath());
                    return getApi().getDynamicSecretValue(dbody);
                case "ROTATED_SECRET":
                    GetRotatedSecretValue rbody = new GetRotatedSecretValue();
                    rbody.setToken(token);
                    rbody.json(true);
                    rbody.setNames(akeylessSecret.getPath());
                    return getApi().getRotatedSecretValue(rbody);
                case "CERTIFICATE":
                    GetCertificateValue cbody = new GetCertificateValue();
                    cbody.setToken(token);
                    cbody.json(true);
                    cbody.setName(akeylessSecret.getPath());
                    GetCertificateValueOutput out = getApi().getCertificateValue(cbody);
                    return gson.fromJson(JSONObject.fromObject(out).toString(), LinkedTreeMap.class);
                case "SSH_CERT_ISSUER":
                    GetSSHCertificate sshCertificate = getSSHCertificateBody(token, (AkeylessSSHIssuer) akeylessSecret);
                    GetSSHCertificateOutput sshout = getApi().getSSHCertificate(sshCertificate);
                    Map<String, String> forJson = new LinkedTreeMap<>();
                    forJson.put("data", sshout.getData());
                    return gson.fromJson(JSONObject.fromObject(forJson).toString(), LinkedTreeMap.class);
                case "PKI_CERT_ISSUER":
                    GetPKICertificate pkiCertificate = getPKICertificateBody(token, (AkeylessPKIIssuer) akeylessSecret);
                    GetPKICertificateOutput pki = getApi().getPKICertificate(pkiCertificate);
                    forJson = new LinkedTreeMap<>();
                    forJson.put("data", pki.getData());
                    return gson.fromJson(JSONObject.fromObject(forJson).toString(), LinkedTreeMap.class);
                default:
                    throw new AkeylessPluginException("Wrong or not supported item type: " + type);
            }

        } catch (ApiException e) {
            throw new AkeylessPluginException("Failed to retrieve secret: " + e.getResponseBody(), e);
        }
    }

    @Nonnull
    private GetSSHCertificate getSSHCertificateBody(String token, AkeylessSSHIssuer sshIssuer) {
        GetSSHCertificate sshCertificate = new GetSSHCertificate();
        sshCertificate.setToken(token);
        sshCertificate.setJson(true);
        sshCertificate.setCertIssuerName(sshIssuer.getPath());
        sshCertificate.setPublicKeyData(sshIssuer.getPublicKey());
        sshCertificate.setCertUsername(sshIssuer.getCertUserName());
        sshCertificate.setTtl(sshIssuer.getTtl());
        return sshCertificate;
    }

    @Nonnull
    private GetPKICertificate getPKICertificateBody(String token, AkeylessPKIIssuer pkiIssuer) {
        GetPKICertificate pkiCertificate = new GetPKICertificate();
        pkiCertificate.certIssuerName(pkiIssuer.getPath());
        pkiCertificate.setToken(token);
        pkiCertificate.setTtl(Long.toString(pkiIssuer.getTtl()));
        pkiCertificate.setJson(true);
        pkiCertificate.setCsrDataBase64(pkiIssuer.getCsrBase64());
        pkiCertificate.setKeyDataBase64(
                Base64.getEncoder().encodeToString(pkiIssuer.getPublicKey().getBytes(StandardCharsets.UTF_8)));
        return pkiCertificate;
    }
}
