package ru.perm.kefir.dating.security;

import org.apache.commons.lang.StringUtils;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * AuthenticationFilter for mail authentication
 *
 * @author Vitaliy Samolovskih aka Kefir
 */
public class MailAuthenticationFilter extends AbstractAuthenticationProcessingFilter {
    public static final String LOGIN_URL = "/j_mail_check";

    protected MailAuthenticationFilter() {
        super(LOGIN_URL);
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response)
            throws AuthenticationException, IOException, ServletException {
        // Check code
        String code = StringUtils.trimToNull(request.getParameter("code"));
        Authentication authentication = new MailAuthenticationToken(code);
        return this.getAuthenticationManager().authenticate(authentication);
    }

}
