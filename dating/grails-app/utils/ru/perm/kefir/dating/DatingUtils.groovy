package ru.perm.kefir.dating

import javax.servlet.ServletRequest

/**
 * @author Vitaliy Samolovskih aka Kefir
 */
class DatingUtils {
    public static final CURRENT_ACCOUNT_ATTR_NAME = 'currentAccount'
    public static final CURRENT_PROFILE_ATTR_NAME = 'currentProfile'

    public static Account currentAccount(ServletRequest request) {
        return (Account) request.getAttribute(CURRENT_ACCOUNT_ATTR_NAME);
    }

    public static Profile currentProfile(ServletRequest request) {
        return (Profile) request.getAttribute(CURRENT_PROFILE_ATTR_NAME);
    }
}
