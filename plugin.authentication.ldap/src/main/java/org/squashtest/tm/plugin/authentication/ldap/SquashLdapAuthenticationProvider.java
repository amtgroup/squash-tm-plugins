package org.squashtest.tm.plugin.authentication.ldap;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.ldap.authentication.LdapAuthenticationProvider;
import org.springframework.security.ldap.authentication.LdapAuthenticator;
import org.squashtest.tm.api.security.authentication.AuthenticationProviderFeatures;
import org.squashtest.tm.api.security.authentication.FeaturesAwareAuthentication;

/**
 * @author Vitaly Ogoltsov &lt;vitaly.ogoltsov@me.com&gt;
 */
public class SquashLdapAuthenticationProvider extends LdapAuthenticationProvider implements AuthenticationProviderFeatures {

    SquashLdapAuthenticationProvider(LdapAuthenticator authenticator) {
        super(authenticator);
    }


    @Override
    protected Authentication createSuccessfulAuthentication(UsernamePasswordAuthenticationToken authentication, UserDetails user) {
        Authentication token = super.createSuccessfulAuthentication(authentication, user);
        return new SquashLdapAuthentication((UsernamePasswordAuthenticationToken) token);
    }


    @Override
    public boolean isManagedPassword() {
        return false;
    }

    @Override
    public String getProviderName() {
        return "ldap";
    }

    @Override
    public boolean shouldCreateMissingUser() {
        return true;
    }


    private class SquashLdapAuthentication extends UsernamePasswordAuthenticationToken implements FeaturesAwareAuthentication {

        SquashLdapAuthentication(UsernamePasswordAuthenticationToken token) {
            super(token.getPrincipal(), token.getCredentials(), token.getAuthorities());
            setDetails(token.getDetails());
        }

        @Override
        public AuthenticationProviderFeatures getFeatures() {
            return SquashLdapAuthenticationProvider.this;
        }

    }

}
