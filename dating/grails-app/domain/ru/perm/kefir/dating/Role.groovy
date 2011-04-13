package ru.perm.kefir.dating

class Role {
    static mapping = {
        version false;
        cache usage:'read-only';
    }

    /** ROLE String  */
    String authority;
    static constraints = {
        authority(blank: false, unique: true)
    }
}
