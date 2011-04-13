package ru.perm.kefir.dating

import org.apache.commons.lang.StringUtils

/**
 * User account
 */
class Account {
    // Authorities
    Set<Role> roles;

    // User mail
    String mail;

    // Password digest MD5
    String passwordDigest = '';
    String salt = '';

    // Mail confirm code
    String confirmCode;

    // Registration date
    Date date = new Date();

    // Mark account as enabled
    boolean enabled = false;
    boolean locked = false;

    static mapping = {
        cache usage:'nonstrict-read-write';
        roles lazy:false, cache:true;
    }

    static hasMany = [roles: Role];

    static constraints = {
        mail(nullable: false, blank: false, email: true, unique: true, maxSize: 320);
        passwordDigest(nullable: false, blank: true, maxSize:80);
        salt(nullable:true, blank:true, maxSize:40);
        confirmCode(nullable: true, unique: true, maxSize:80);
        date(nullable: false);
    }

    static transients = ['accountExpired', 'passwordExpired'];

    public boolean getAccountExpired() {
        return false;
    }

    public boolean getPasswordExpired() {
        return false;
    }

    /**
     * Prepare mail for save to DB or search
     */
    public static String prepareMail(String string) {
        return StringUtils.trimToEmpty(string).toLowerCase();
    }

    boolean equals(o) {
        if (this.is(o)) return true;
        if (!(o instanceof Account)) return false;

        Account account = (Account) o;

        if (id != account.id) return false;

        return true;
    }

    int hashCode() {
        return (int) (id ^ (id >>> 32));
    }
}
