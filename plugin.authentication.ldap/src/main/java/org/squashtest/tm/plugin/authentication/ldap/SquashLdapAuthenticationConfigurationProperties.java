package org.squashtest.tm.plugin.authentication.ldap;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.ldap.core.support.BaseLdapPathContextSource;

/**
 * @author Vitaly Ogoltsov &lt;vitaly.ogoltsov@me.com&gt;
 */
@ConfigurationProperties(prefix = "authentication.ldap")
public class SquashLdapAuthenticationConfigurationProperties {

    /**
     * Ldap server connection.
     */
    private Server server = new Server();
    /**
     * Ldap user search settings.
     */
    private User user = new User();


    public Server getServer() {
        return server;
    }

    public void setServer(Server server) {
        this.server = server;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }


    public static class Server {

        /**
         * Server url.
         */
        private String url = "ldap://localhost:389";

        /**
         * Manager account to be used to connect to LDAP server.
         */
        private String managerDn;

        /**
         * Manager password to be used to connect to LDAP server.
         */
        private String managerPassword;


        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }

        public String getManagerDn() {
            return managerDn;
        }

        public void setManagerDn(String managerDn) {
            this.managerDn = managerDn;
        }

        public String getManagerPassword() {
            return managerPassword;
        }

        public void setManagerPassword(String managerPassword) {
            this.managerPassword = managerPassword;
        }

    }


    public static class User {

        /**
         * Whether to fetch user attributes.
         */
        private boolean fetchAttributes = true;

        /**
         * @see org.springframework.security.ldap.authentication.BindAuthenticator#setUserDnPatterns(String[])
         */
        private String dnPatterns = null;

        /**
         * @see org.springframework.security.ldap.search.FilterBasedLdapUserSearch#FilterBasedLdapUserSearch(String, String, BaseLdapPathContextSource)
         */
        private String searchFilter = null;

        /**
         * @see org.springframework.security.ldap.search.FilterBasedLdapUserSearch#FilterBasedLdapUserSearch(String, String, BaseLdapPathContextSource)
         */
        private String searchBase = null;

        /**
         * @see org.springframework.security.ldap.search.FilterBasedLdapUserSearch#setSearchSubtree(boolean)
         */
        private Boolean searchSubtree;


        public boolean isFetchAttributes() {
            return fetchAttributes;
        }

        public void setFetchAttributes(boolean fetchAttributes) {
            this.fetchAttributes = fetchAttributes;
        }

        public String getDnPatterns() {
            return dnPatterns;
        }

        public void setDnPatterns(String dnPatterns) {
            this.dnPatterns = dnPatterns;
        }

        public String getSearchFilter() {
            return searchFilter;
        }

        public void setSearchFilter(String searchFilter) {
            this.searchFilter = searchFilter;
        }

        public String getSearchBase() {
            return searchBase;
        }

        public void setSearchBase(String searchBase) {
            this.searchBase = searchBase;
        }

        public Boolean getSearchSubtree() {
            return searchSubtree;
        }

        public void setSearchSubtree(Boolean searchSubtree) {
            this.searchSubtree = searchSubtree;
        }
    }

}
