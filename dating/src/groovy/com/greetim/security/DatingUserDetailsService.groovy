package com.greetim.security

import com.greetim.Account
import org.codehaus.groovy.grails.plugins.springsecurity.GrailsUserDetailsService
import org.codehaus.groovy.grails.plugins.springsecurity.SpringSecurityUtils
import org.springframework.security.core.authority.GrantedAuthorityImpl
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UsernameNotFoundException

/**
 * @author Vitalii Samolovskikh aka Kefir
 */
class DatingUserDetailsService implements GrailsUserDetailsService {
    static final List NO_ROLES = [new GrantedAuthorityImpl(SpringSecurityUtils.NO_ROLE)]

    UserDetails loadUserByUsername(String username, boolean loadRoles) throws UsernameNotFoundException {
        return loadUserByUsername(username);
    }

    UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return Account.withTransaction { status ->
            Account account = Account.findByMail(username);
            if (!account) {
                throw new UsernameNotFoundException('User not found', username)
            }

            def authorities = account.roles.collect {
                new GrantedAuthorityImpl(it.authority)
            }

            return new SaltedUserDetails(
                    account.mail, account.passwordDigest, account.enabled,
                    !account.accountExpired, !account.passwordExpired, !account.locked,
                    authorities ?: NO_ROLES, account.id, account.salt
            );
        }
    }
}
