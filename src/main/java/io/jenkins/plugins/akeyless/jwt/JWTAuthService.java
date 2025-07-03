package io.jenkins.plugins.akeyless.jwt;

import hudson.Extension;
import hudson.model.UnprotectedRootAction;
import io.jenkins.plugins.akeyless.AkeylessPluginException;
import io.jenkins.plugins.akeyless.configuration.GlobalAkeylessConfiguration;
import jenkins.model.GlobalConfiguration;
import org.kohsuke.stapler.WebMethod;
import org.kohsuke.stapler.verb.GET;
import org.springframework.web.HttpRequestMethodNotSupportedException;

@Extension
public class JWTAuthService implements UnprotectedRootAction {
    @Override
    public String getIconFileName() {
        return null;
    }

    @Override
    public String getDisplayName() {
        return "JWT Endpoint";
    }

    @Override
    public String getUrlName() {
        return ".well-known";
    }

    @GET
    @WebMethod(name = "jwks.json")
    public String getJwkKeys() throws HttpRequestMethodNotSupportedException {
        try {
            GlobalAkeylessConfiguration result = GlobalConfiguration.all().get(GlobalAkeylessConfiguration.class);
            if (result == null) {
                throw new HttpRequestMethodNotSupportedException("jwts.json");
            }
            return JwtToken.getJWTKeys().toString(4);
        } catch (Exception ex) {
            throw new AkeylessPluginException("Error while getting JWT keys", ex);
        }
    }
}
