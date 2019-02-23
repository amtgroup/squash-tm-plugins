package org.squashtest.tm.plugin.authentication.ldap;

import org.springframework.ldap.core.DirContextAdapter;
import org.springframework.ldap.core.DirContextOperations;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.ldap.userdetails.LdapUserDetailsMapper;
import org.squashtest.tm.api.security.acls.Roles;
import org.squashtest.tm.service.internal.security.SquashUserDetailsManager;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

/**
 * @author Vitaly Ogoltsov &lt;vitaly.ogoltsov@me.com&gt;
 */
class SquashLdapUserDetailsMapper extends LdapUserDetailsMapper {

    private final SquashUserDetailsManager squashUserDetailsManager;


    SquashLdapUserDetailsMapper(SquashUserDetailsManager squashUserDetailsManager) {
        Objects.requireNonNull(squashUserDetailsManager);
        this.squashUserDetailsManager = squashUserDetailsManager;
    }


    @Override
    public UserDetails mapUserFromContext(DirContextOperations ctx, String username, Collection<? extends GrantedAuthority> authorities) {
        Collection<GrantedAuthority> squashUserAuthorities = new ArrayList<>(this.squashUserDetailsManager.loadAuthoritiesByUsername(username));
        if (squashUserAuthorities.isEmpty()) {
            squashUserAuthorities.add(new SimpleGrantedAuthority(Roles.ROLE_TM_USER));
        }
        squashUserAuthorities.addAll(authorities);
        return super.mapUserFromContext(ctx, username, squashUserAuthorities);
    }

    @Override
    public void mapUserToContext(UserDetails userDetails, DirContextAdapter dirContextAdapter) {
        throw new UnsupportedOperationException("SquashLdapUserDetailsMapper only supports reading from a context");
    }

}
