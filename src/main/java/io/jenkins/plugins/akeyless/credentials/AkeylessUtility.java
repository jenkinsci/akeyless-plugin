package io.jenkins.plugins.akeyless.credentials;

import com.cloudbees.plugins.credentials.CredentialsMatchers;
import com.cloudbees.plugins.credentials.CredentialsProvider;
import com.cloudbees.plugins.credentials.CredentialsUnavailableException;
import com.cloudbees.plugins.credentials.domains.DomainRequirement;
import com.cloudbees.plugins.credentials.matchers.IdMatcher;
import hudson.model.Item;
import hudson.model.ItemGroup;
import hudson.security.ACL;
import io.jenkins.plugins.akeyless.AkeylessPluginException;
import java.util.Collections;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.lang.StringUtils;

/**
 * @author alexeydolgopyatov
 */
public class AkeylessUtility {
    private static final Logger LOGGER = Logger.getLogger(AkeylessUtility.class.getName());
    //
    //    static Map<String, Object> getSecret(@NonNull String path, @NonNull ItemGroup<Item> context) {
    //        AkeylessConfiguration configuration = null;
    //        for (AkeylessConfigResolver resolver : ExtensionList.lookup(AkeylessConfigResolver.class)) {
    //            if (configuration != null) {
    //                configuration = configuration
    //                        .mergeWithParent(resolver.getConfig(context));
    //            } else {
    //                configuration = resolver.getConfig(context);
    //            }
    //        }
    //
    //        if (configuration == null) {
    //            throw new IllegalStateException("Vault plugin has not been configured.");
    //        }
    //
    //        String msg = String.format(
    //                "Retrieving Akeyless secret path=%s ",
    //                path);
    //        LOGGER.info(msg);
    //
    //        try {
    //            V2Api api = configuration.getAkeylessApi();
    //
    //
    //            AkeylessCredential credential = configuration.getAkeylessCredential();
    //            if (credential == null)
    //                credential = retrieveAkeylessCredentials(
    //                        configuration.getAkeylessCredentialId(), context);
    //
    //            AkeylessAccessor vaultAccessor = new AkeylessAccessor(api, credential);
    //            return vaultAccessor.getSecret(path);
    //        } catch (AkeylessPluginException vpe) {
    //            throw vpe;
    //        } catch (Exception e) {
    //            throw new RuntimeException(e);
    //        }
    //
    //    }
    //
    //    static Object getSecretKey(@NonNull String secretPath,
    //                                    @NonNull String secretKey,
    //                                    @NonNull ItemGroup<Item> context) {
    //        try {
    //            Map<String, Object> values = getSecret(secretPath, context);
    //
    //            if (!values.containsKey(secretKey)) {
    //                String message = String.format(
    //                        "Key %s could not be found in path %s",
    //                        secretKey, secretPath);
    //                throw new AkeylessPluginException(message);
    //            }
    //
    //            return values.get(secretKey);
    //        } catch (IllegalStateException e) {
    //            throw new IllegalStateException(e);
    //        }
    //    }

    private static AkeylessCredential retrieveAkeylessCredentials(String id, ItemGroup<Item> itemGroup) {
        if (StringUtils.isBlank(id)) {
            throw new AkeylessPluginException(
                    "The credential id was not configured - please specify the credentials to use.");
        } else {
            LOGGER.log(Level.INFO, "Retrieving credential ID : " + id);
        }
        List<AkeylessCredential> credentials = CredentialsProvider.lookupCredentials(
                AkeylessCredential.class, itemGroup, ACL.SYSTEM, Collections.<DomainRequirement>emptyList());
        AkeylessCredential credential = CredentialsMatchers.firstOrNull(credentials, new IdMatcher(id));

        if (credential == null) {
            throw new CredentialsUnavailableException(id);
        }

        return credential;
    }
}
