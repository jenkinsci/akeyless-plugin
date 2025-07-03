package io.jenkins.plugins.akeyless.configuration;

import edu.umd.cs.findbugs.annotations.NonNull;
import hudson.Extension;
import hudson.ExtensionList;
import hudson.model.Item;
import hudson.model.ItemGroup;
import hudson.model.ModelObject;
import hudson.util.FormValidation;
import io.jenkins.plugins.akeyless.jwt.JwtToken;
import jenkins.model.GlobalConfiguration;
import jenkins.model.Jenkins;
import net.sf.json.JSONObject;
import org.jenkinsci.Symbol;
import org.kohsuke.stapler.AncestorInPath;
import org.kohsuke.stapler.DataBoundSetter;
import org.kohsuke.stapler.StaplerRequest;
import org.kohsuke.stapler.verb.POST;

/**
 * @author alexeydolgopyatov
 */
@Extension
@Symbol("akeyless")
public class GlobalAkeylessConfiguration extends GlobalConfiguration {
    private AkeylessConfiguration configuration;
    private String jwtAudience = "akeyless-jwt";
    private long keyLifetimeInMinutes = 60;
    private long tokenDurationInSeconds = 120;

    private Boolean enableIdentityFormatFieldsFromToken = false;
    private String identityFormatFieldsFromToken = "jenkins_full_name";
    private String selectIdentityFormatToken = "jenkins_full_name";
    private String selectIdentityFieldsSeparator = "-";
    private String identityFieldName = "sub";

    public static GlobalAkeylessConfiguration get() {
        return ExtensionList.lookupSingleton(GlobalAkeylessConfiguration.class);
    }

    public GlobalAkeylessConfiguration() {
        load();
    }

    @Override
    public boolean configure(StaplerRequest req, JSONObject json) throws FormException {
        req.bindJSON(this, json);
        return true;
    }

    public AkeylessConfiguration getConfiguration() {
        return configuration;
    }

    @DataBoundSetter
    public void setConfiguration(AkeylessConfiguration configuration) {
        this.configuration = configuration;
        save();
    }

    public String getJwtAudience() {
        return jwtAudience;
    }

    /**
     * @return the Key Life Time in Minutes
     */
    public long getKeyLifetimeInMinutes() {
        return keyLifetimeInMinutes;
    }

    /**
     * set the Key Life Time in Minutes
     */
    @DataBoundSetter
    public void setKeyLifetimeInMinutes(long keyLifetimeInMinutes) {
        this.keyLifetimeInMinutes = keyLifetimeInMinutes;
        save();
    }

    /**
     * @return the Token duration in seconds
     **/
    public long getTokenDurationInSeconds() {
        return tokenDurationInSeconds;
    }

    /**
     * set the Token duration in seconds
     **/
    @DataBoundSetter
    public void setTokenDurationInSeconds(long tokenDurationInSeconds) {
        this.tokenDurationInSeconds = tokenDurationInSeconds;
        save();
    }

    /**
     * POST method to obtain the JWTtoken for the Item
     *
     * @param item Jenkins Item
     * @return status ok based on the FormValidation
     */
    @POST
    public FormValidation doObtainJwtToken(@AncestorInPath ModelObject item) {
        GlobalAkeylessConfiguration globalConfig = GlobalConfiguration.all().get(GlobalAkeylessConfiguration.class);
        // global context is when item is equal to null
        if (item == null) {
            item = Jenkins.get();
        }
        JwtToken.getToken(item, globalConfig);
        JwtToken token = JwtToken.getUnsignedToken("pluginAction", item, globalConfig);
        if (token != null) {
            return FormValidation.ok("JWT Token: \n" + token.claim.toString(4));
        }
        return FormValidation.ok("JWT Token: \nCannot obtain token");
    }

    public Boolean getEnableIdentityFormatFieldsFromToken() {
        return enableIdentityFormatFieldsFromToken;
    }

    @DataBoundSetter
    public void setEnableIdentityFormatFieldsFromToken(Boolean enableIdentityFormatFieldsFromToken) {
        // LOGGER.log(Level.WARNING, "DEPRECATED: GlobalConjurConfiguration get() #enableIdentityFormatFieldsFromToken "
        // + enableIdentityFormatFieldsFromToken);
        this.enableIdentityFormatFieldsFromToken = enableIdentityFormatFieldsFromToken;
        save();
    }

    public String getSelectIdentityFormatToken() {
        return selectIdentityFormatToken;
    }

    @DataBoundSetter
    public void setSelectIdentityFormatToken(String selectIdentityFormatToken) {
        // LOGGER.log(Level.FINEST, "GlobalConjurConfiguration get() #selectIdentityFormatToken " +
        // selectIdentityFormatToken);
        this.selectIdentityFormatToken = selectIdentityFormatToken;
        save();
    }

    public String getSelectIdentityFieldsSeparator() {
        return selectIdentityFieldsSeparator;
    }

    @DataBoundSetter
    public void setSelectIdentityFieldsSeparator(String selectIdentityFieldsSeparator) {
        this.selectIdentityFieldsSeparator = selectIdentityFieldsSeparator;
        save();
    }

    public String getidentityFieldName() {
        return identityFieldName;
    }

    @DataBoundSetter
    public void setIdentityFieldName(String identityFieldName) {
        this.identityFieldName = (!identityFieldName.isEmpty()) ? identityFieldName : "sub";
        save();
    }

    public String getIdentityFormatFieldsFromToken() {
        return identityFormatFieldsFromToken;
    }

    @DataBoundSetter
    public void setIdentityFormatFieldsFromToken(String identityFormatFieldsFromToken) {
        // LOGGER.log(Level.FINE, "GlobalConjurConfiguration get() #identityFormatFieldsFromToken " +
        // identityFormatFieldsFromToken);
        this.identityFormatFieldsFromToken = identityFormatFieldsFromToken;
        save();
    }

    @Extension(ordinal = 0)
    public static class ForJob extends AkeylessConfigResolver {

        @NonNull
        @Override
        public AkeylessConfiguration forJob(@NonNull Item job) {
            return getConfig(job.getParent());
        }

        @Override
        public AkeylessConfiguration getConfig(@NonNull ItemGroup itemGroup) {
            return GlobalAkeylessConfiguration.get().getConfiguration();
        }
    }

    protected Object readResolve() {
        return this;
    }
}
