package ru.perm.kefir.dating.security;

import org.codehaus.groovy.grails.plugins.springsecurity.GrailsUser;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;
import java.util.Collections;

/**
 * Authentication token by email
 *
 * @author Vitaliy Samolovskih aka Kefir
 */
public class MailAuthenticationToken extends AbstractAuthenticationToken {
    private final Object principal;
    private final String code;

    public MailAuthenticationToken(String code) {
        //noinspection unchecked
        super(Collections.EMPTY_SET);

        if ((code == null) || ("".equals(code))) {
            throw new IllegalArgumentException("Cannot pass null or empty values to constructor");
        }

        this.code = code;
        this.principal = null;
        setAuthenticated(false);
    }

    public MailAuthenticationToken(String code, Object principal, Collection<? extends GrantedAuthority> authorities) {
        super(authorities);

        if ((code == null) || ("".equals(code)) || (principal == null) || "".equals(principal)) {
            throw new IllegalArgumentException("Cannot pass null or empty values to constructor");
        }

        this.code = code;
        this.principal = principal;
        setAuthenticated(true);
    }

    @Override
    public Object getCredentials() {
        return code;
    }

    @Override
    public Object getPrincipal() {
        return principal;
    }

    @Override
    public String getName() {
        if (principal != null && principal instanceof GrailsUser) {
            return ((GrailsUser) principal).getUsername();
        } else {
            return null;
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof MailAuthenticationToken)) return false;
        if (!super.equals(o)) return false;

        MailAuthenticationToken that = (MailAuthenticationToken) o;

        return code.equals(that.code);
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + code.hashCode();
        return result;
    }

    @Override
    public String toString() {
        return "MailAuthenticationToken{" +
                "principal=" + principal +
                ", code='" + code + '\'' +
                '}';
    }
}
