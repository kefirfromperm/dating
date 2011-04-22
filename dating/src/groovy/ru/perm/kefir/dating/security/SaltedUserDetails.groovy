package ru.perm.kefir.dating.security

import org.codehaus.groovy.grails.plugins.springsecurity.GrailsUser
import org.springframework.security.core.GrantedAuthority

/**
 * @author Vitalii Samolovskikh aka Kefir
 */
class SaltedUserDetails extends GrailsUser {
    final String salt;

    SaltedUserDetails(
    String username, String password, boolean enabled,
    boolean accountNonExpired, boolean credentialsNonExpired, boolean accountNonLocked,
    Collection<GrantedAuthority> authorities, Object id, String salt
    ) {
        super(
                username, password, enabled,
                accountNonExpired, credentialsNonExpired, accountNonLocked, authorities, id
        )
        this.salt = salt
    }
}
