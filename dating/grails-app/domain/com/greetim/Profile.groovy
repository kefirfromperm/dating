package com.greetim

import ru.permintel.saga.SagaFile

/**
 * User profile
 */
class Profile {
    public static final String ALIAS_REGEX = /^[a-z\d][-\_a-z\d]*$/;

    // link to account
    Account account;

    // Alias for link to profile
    String alias;

    // Profile data
    String name;
    String about;

    // Photo
    SagaFile photo;
    boolean useGravatar = false;

    // Auto timestamp
    Date dateCreated;
    Date lastUpdated;

    static mapping = {
        about type: 'text';
        cache usage: 'nonstrict-read-write';
        account lazy: false;
    }

    static constraints = {
        account(nullable: false, unique: true);
        alias(nullable: false, blank: false, size: 1..63, matches: ALIAS_REGEX);
        name(nullable: false, blank: false, maxSize: 255);
        about(nullable: true, blank: true, maxSize: 4095);
        photo(nullable: true);
    }

    def beforeValidate(){
        alias = prepareAlias(alias);
    }

    /**
     * Prepare alias for insert or search
     */
    public static String prepareAlias(String alias){
        return alias?.trim()?.toLowerCase();
    }

    boolean equals(o) {
        if (this.is(o)) return true;
        if (!(o instanceof Profile)) return false;

        Profile profile = (Profile) o;

        if (id != profile.id) return false;

        return true;
    }

    int hashCode() {
        return (int) (id ^ (id >>> 32));
    }
}
