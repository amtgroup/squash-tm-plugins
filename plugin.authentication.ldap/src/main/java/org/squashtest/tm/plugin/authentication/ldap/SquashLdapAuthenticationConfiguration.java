package org.squashtest.tm.plugin.authentication.ldap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.ldap.core.support.LdapContextSource;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.authentication.configuration.GlobalAuthenticationConfigurerAdapter;
import org.springframework.security.ldap.authentication.BindAuthenticator;
import org.springframework.security.ldap.authentication.LdapAuthenticationProvider;
import org.springframework.security.ldap.authentication.LdapAuthenticator;
import org.springframework.security.ldap.search.FilterBasedLdapUserSearch;
import org.squashtest.tm.api.security.authentication.AuthenticationProviderFeatures;
import org.squashtest.tm.service.internal.security.SquashUserDetailsManager;

import java.util.Collections;
import java.util.Optional;

/**
 * @author Vitaly Ogoltsov &lt;vitaly.ogoltsov@me.com&gt;
 */
@Configuration
@ConditionalOnProperty(value = "authentication.ldap.enabled", havingValue = "true")
@EnableConfigurationProperties(SquashLdapAuthenticationConfigurationProperties.class)
public class SquashLdapAuthenticationConfiguration {

    private static final Logger log = LoggerFactory.getLogger(SquashLdapAuthenticationConfiguration.class);


    private final SquashLdapAuthenticationConfigurationProperties properties;


    public SquashLdapAuthenticationConfiguration(SquashLdapAuthenticationConfigurationProperties properties) {
        this.properties = properties;
    }


    @Bean
    public LdapContextSource squashLdapContextSource() {
        LdapContextSource ldapContextSource = new LdapContextSource();
        ldapContextSource.setUrl(properties.getServer().getUrl());
        ldapContextSource.setUserDn(properties.getServer().getManagerDn());
        ldapContextSource.setPassword(properties.getServer().getManagerPassword());
        ldapContextSource.setBaseEnvironmentProperties(Collections.emptyMap());
        return ldapContextSource;
    }

    @Bean
    public LdapAuthenticator squashLdapAuthenticator(LdapContextSource squashLdapContextSource) {
        BindAuthenticator bindAuthenticator = new BindAuthenticator(squashLdapContextSource);
        if (properties.getUser().getDnPatterns() != null) {
            log.info("Using dn pattern for user search: " + properties.getUser().getDnPatterns());
            bindAuthenticator.setUserDnPatterns(new String[]{properties.getUser().getDnPatterns()});
        } else if (properties.getUser().getSearchBase() != null && properties.getUser().getSearchFilter() != null) {
            log.info(
                    "Using filter based user search at {}: {}",
                    properties.getUser().getSearchBase(),
                    properties.getUser().getSearchFilter()
            );
            FilterBasedLdapUserSearch filterBasedLdapUserSearch = new FilterBasedLdapUserSearch(
                    properties.getUser().getSearchBase(),
                    properties.getUser().getSearchFilter(),
                    squashLdapContextSource
            );
            Optional.ofNullable(properties.getUser().getSearchSubtree()).ifPresent(filterBasedLdapUserSearch::setSearchSubtree);
            bindAuthenticator.setUserSearch(filterBasedLdapUserSearch);
        } else {
            throw new IllegalArgumentException("Cannot create a bind authenticator - no user search option configured");
        }
        return bindAuthenticator;
    }

    @Bean
    public AuthenticationProvider squashLdapAuthenticationProvider(LdapAuthenticator squashLdapAuthenticator, SquashUserDetailsManager squashUserDetailsManager) {
        LdapAuthenticationProvider squashLdapAuthenticationProvider = new SquashLdapAuthenticationProvider(squashLdapAuthenticator);
        squashLdapAuthenticationProvider.setUserDetailsContextMapper(new SquashLdapUserDetailsMapper(squashUserDetailsManager));
        return squashLdapAuthenticationProvider;
    }

    @Bean
    public AuthenticationProviderFeatures squashLdapAuthenticationProviderFeatures(AuthenticationProvider squashLdapAuthenticationProvider) {
        return (SquashLdapAuthenticationProvider) squashLdapAuthenticationProvider;
    }


    /**
     * @see org.squashtest.tm.service.SecurityConfig.InternalAuthenticationConfig
     */
    @Configuration
    @Order(0) // WebSecurityConfigurerAdapter default order is 100, we need to init this before
    public static class SpringSecurityConfigurer extends GlobalAuthenticationConfigurerAdapter {

        private final AuthenticationProvider squashLdapAuthenticationProvider;


        public SpringSecurityConfigurer(AuthenticationProvider squashLdapAuthenticationProvider) {
            this.squashLdapAuthenticationProvider = squashLdapAuthenticationProvider;
        }


        @Override
        public void configure(AuthenticationManagerBuilder auth) throws Exception {
            auth.authenticationProvider(squashLdapAuthenticationProvider);
        }
    }

}
