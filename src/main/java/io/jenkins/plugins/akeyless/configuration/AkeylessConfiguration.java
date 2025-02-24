package io.jenkins.plugins.akeyless.configuration;

import com.cloudbees.plugins.credentials.common.StandardListBoxModel;
import com.cloudbees.plugins.credentials.domains.DomainRequirement;
import com.cloudbees.plugins.credentials.domains.URIRequirementBuilder;
import edu.umd.cs.findbugs.annotations.NonNull;
import hudson.Extension;
import hudson.model.AbstractDescribableImpl;
import hudson.model.Descriptor;
import hudson.model.Item;
import hudson.security.ACL;
import hudson.util.ListBoxModel;
import io.akeyless.client.ApiClient;
import io.akeyless.client.Configuration;
import io.akeyless.client.api.V2Api;
import io.jenkins.plugins.akeyless.credentials.AkeylessCredential;
import org.apache.commons.lang3.StringUtils;
import org.kohsuke.stapler.AncestorInPath;
import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.DataBoundSetter;
import org.kohsuke.stapler.QueryParameter;

import java.io.Serializable;
import java.util.List;

import static hudson.Util.fixEmptyAndTrim;

/**
 * @author alexeydolgopyatov
 */
public class AkeylessConfiguration extends AbstractDescribableImpl<AkeylessConfiguration> implements Serializable {
    private static final long serialVersionUID = 1L;

    private String akeylessUrl;
    private String akeylessCredentialId;
    private AkeylessCredential akeylessCredential;
    private Boolean disableChildPoliciesOverride;
    private Boolean failIfNotFound = DescriptorImpl.DEFAULT_FAIL_NOT_FOUND;
    private String policies;

    private Boolean skipSslVerification = DescriptorImpl.DEFAULT_SKIP_SSL_VERIFICATION;

    @DataBoundConstructor
    public AkeylessConfiguration() {
        // no args constructor
    }

    public AkeylessConfiguration(AkeylessConfiguration toCopy) {
        this.akeylessUrl = toCopy.getAkeylessUrl();
        this.akeylessCredentialId = toCopy.getAkeylessCredentialId();
        this.akeylessCredential = toCopy.getAkeylessCredential();
        this.failIfNotFound = toCopy.failIfNotFound;
        this.skipSslVerification = toCopy.skipSslVerification;
        this.policies = toCopy.policies;
        this.disableChildPoliciesOverride = toCopy.disableChildPoliciesOverride;
    }

    public String getAkeylessUrl() {
        return akeylessUrl;
    }

    public String getAkeylessCredentialId() {
        return akeylessCredentialId;
    }

    @DataBoundSetter
    public void setAkeylessUrl(String akeylessUrl) {
        this.akeylessUrl = normalizeUrl(fixEmptyAndTrim(akeylessUrl));
    }

    @DataBoundSetter
    public void setAkeylessCredentialId(String akeylessCredentialId) {
        this.akeylessCredentialId = fixEmptyAndTrim(akeylessCredentialId);
    }

    public AkeylessCredential getAkeylessCredential() {
        return akeylessCredential;
    }

    @DataBoundSetter
    public void setAkeylessCredential(AkeylessCredential akeylessCredential) {
        this.akeylessCredential = akeylessCredential;
    }

    public AkeylessConfiguration mergeWithParent(AkeylessConfiguration parent) {
        if (parent == null) {
            return this;
        }
        AkeylessConfiguration result = new AkeylessConfiguration(this);
        if (StringUtils.isBlank(result.getAkeylessCredentialId())) {
            result.setAkeylessCredentialId(parent.getAkeylessCredentialId());
        }
        if (result.akeylessCredential == null) {
            result.setAkeylessCredential(parent.getAkeylessCredential());
        }
        if (StringUtils.isBlank(result.getAkeylessUrl())) {
            result.setAkeylessUrl(parent.getAkeylessUrl());
        }

        if (StringUtils.isBlank(result.getPolicies())
                || (parent.getDisableChildPoliciesOverride() != null && parent.getDisableChildPoliciesOverride())) {
            result.setPolicies(parent.getPolicies());
        }
        //        if (result.timeout == null) {
        //            result.setTimeout(parent.getTimeout());
        //        }
        if (result.failIfNotFound == null) {
            result.setFailIfNotFound(parent.failIfNotFound);
        }
        if (result.skipSslVerification == null) {
            result.setSkipSslVerification(parent.skipSslVerification);
        }
        return result;
    }

    public Boolean getFailIfNotFound() {
        return failIfNotFound;
    }

    @DataBoundSetter
    public void setFailIfNotFound(Boolean failIfNotFound) {
        this.failIfNotFound = failIfNotFound;
    }

    public Boolean getSkipSslVerification() {
        return skipSslVerification;
    }

    @DataBoundSetter
    public void setSkipSslVerification(Boolean skipSslVerification) {
        this.skipSslVerification = skipSslVerification;
    }

    public Boolean getDisableChildPoliciesOverride() {
        return disableChildPoliciesOverride;
    }

    @DataBoundSetter
    public void setDisableChildPoliciesOverride(Boolean disableChildPoliciesOverride) {
        this.disableChildPoliciesOverride = disableChildPoliciesOverride;
    }

    public String getPolicies() {
        return policies;
    }

    @DataBoundSetter
    public void setPolicies(String policies) {
        this.policies = fixEmptyAndTrim(policies);
    }

    @Extension
    public static class DescriptorImpl extends Descriptor<AkeylessConfiguration> {

        public static final boolean DEFAULT_FAIL_NOT_FOUND = true;

        public static final boolean DEFAULT_SKIP_SSL_VERIFICATION = false;

        public static final int DEFAULT_ENGINE_VERSION = 2;

        @Override
        @NonNull
        public String getDisplayName() {
            return "Akeyless Auth Method Configuration";
        }

        @SuppressWarnings("unused") // used by stapler
        public ListBoxModel doFillAkeylessCredentialIdItems(@AncestorInPath Item item, @QueryParameter String uri) {
            // This is needed for folders: credentials bound to a folder are
            // realized through domain requirements
            List<DomainRequirement> domainRequirements =
                    URIRequirementBuilder.fromUri(uri).build();
            return new StandardListBoxModel()
                    .includeEmptyValue()
                    .includeAs(ACL.SYSTEM, item, AkeylessCredential.class, domainRequirements);
        }
    }

    private String normalizeUrl(String url) {
        if (url == null) {
            return null;
        }

        if (url.endsWith("/")) {
            return url.substring(0, url.length() - 1);
        }

        return url;
    }

    public V2Api getAkeylessApi() {
        ApiClient client = Configuration.getDefaultApiClient();
        client.setBasePath(this.getAkeylessUrl());
        return new V2Api(client);
    }
}
