package ru.perm.kefir.dating.security;

import org.springframework.security.authentication.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionTemplate;
import ru.perm.kefir.dating.Account;
import ru.perm.kefir.dating.AccountService;

/**
 * Authentication provider for mail authentication
 *
 * @author Vitaliy Samolovskih aka Kefir
 */
public class MailAuthenticationProvider implements AuthenticationProvider {
    private AccountService accountService;
    private UserDetailsService userDetailsService;
    private PlatformTransactionManager transactionManager;

    @Override
    public Authentication authenticate(final Authentication authentication) throws AuthenticationException {
        if (!supports(authentication.getClass())) {
            return null;
        }

        if (authentication.isAuthenticated() || authentication.getCredentials() == null) {
            return authentication;
        }

        final String code = (String) ((MailAuthenticationToken) authentication).getCredentials();

        UserDetails user = new TransactionTemplate(transactionManager).execute(
                new TransactionCallback<UserDetails>() {
                    @Override
                    public UserDetails doInTransaction(TransactionStatus transactionStatus) {
                        Account account = accountService.findByCode(code);
                        if (account != null) {
                            account.setEnabled(true);
                            return userDetailsService.loadUserByUsername(account.getMail());
                        } else {
                            return null;
                        }
                    }
                }
        );

        if (user != null) {
            if (!user.isAccountNonLocked()) {
                throw new LockedException("User account is locked.", user);
            }

            if (!user.isEnabled()) {
                throw new DisabledException("User account is disabled.", user);
            }

            if (!user.isAccountNonExpired()) {
                throw new AccountExpiredException("User account has expired.");
            }

            return new MailAuthenticationToken(code, user, user.getAuthorities());
        } else {
            throw new BadCredentialsException("Can't find user account by code.");
        }
    }

    @Override
    public boolean supports(Class<?> aClass) {
        return (MailAuthenticationToken.class.isAssignableFrom(aClass));
    }

    public void setAccountService(AccountService accountService) {
        this.accountService = accountService;
    }

    public void setTransactionManager(PlatformTransactionManager transactionManager) {
        this.transactionManager = transactionManager;
    }

    public void setUserDetailsService(UserDetailsService userDetailsService) {
        this.userDetailsService = userDetailsService;
    }
}
