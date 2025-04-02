package io.jenkins.plugins.akeyless.credentials;

import com.cloudbees.hudson.plugins.folder.AbstractFolder;
import com.cloudbees.hudson.plugins.folder.properties.FolderCredentialsProvider;
import com.cloudbees.plugins.credentials.*;
import com.cloudbees.plugins.credentials.domains.DomainCredentials;
import com.cloudbees.plugins.credentials.domains.DomainRequirement;
import hudson.Extension;
import hudson.model.ItemGroup;
import hudson.security.ACL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import jenkins.model.Jenkins;
import org.acegisecurity.Authentication;

/**
 * @author alexeydolgopyatov
 */
@Extension(optional = true, ordinal = 1)
public class AkeylessCredentialsProvider extends CredentialsProvider {

    @Nonnull
    @Override
    public <C extends Credentials> List<C> getCredentials(
            @Nonnull Class<C> type, @Nullable ItemGroup itemGroup, @Nullable Authentication authentication) {
        return getCredentials(type, itemGroup, authentication, Collections.emptyList());
    }

    @Nonnull
    @Override
    public <C extends Credentials> List<C> getCredentials(
            @Nonnull Class<C> type,
            @Nullable ItemGroup itemGroup,
            @Nullable Authentication authentication,
            @Nonnull List<DomainRequirement> domainRequirements) {
        CredentialsMatcher matcher = (type != AkeylessCredential.class
                ? CredentialsMatchers.instanceOf(AbstractAkeylessBaseStandardCredentials.class)
                : CredentialsMatchers.always());
        List<C> creds = new ArrayList<C>();
        if (ACL.SYSTEM.equals(authentication)) {
            for (ItemGroup g = itemGroup; g instanceof AbstractFolder; g = (AbstractFolder.class.cast(g)).getParent()) {
                FolderCredentialsProvider.FolderCredentialsProperty property = ((AbstractFolder<?>) g)
                        .getProperties()
                        .get(FolderCredentialsProvider.FolderCredentialsProperty.class);
                if (property == null) {
                    continue;
                }

                List<C> folderCreds = DomainCredentials.getCredentials(
                        property.getDomainCredentialsMap(), type, domainRequirements, matcher);

                if (type != AkeylessCredential.class) {
                    for (C c : folderCreds) {
                        ((AbstractAkeylessBaseStandardCredentials) c).setContext(g);
                    }
                }

                creds.addAll(folderCreds);
            }

            List<C> globalCreds = DomainCredentials.getCredentials(
                    SystemCredentialsProvider.getInstance().getDomainCredentialsMap(),
                    type,
                    domainRequirements,
                    matcher);
            if (type != AkeylessCredential.class) {
                for (C c : globalCreds) {
                    ((AbstractAkeylessBaseStandardCredentials) c).setContext(Jenkins.get());
                }
            }
            creds.addAll(globalCreds);
        }

        return creds;
    }
}
