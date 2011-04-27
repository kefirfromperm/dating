package com.greetim

/**
 * User's bookmark to another user
 */
class Bookmark {
    Profile owner;
    Profile target;
    BookmarkStatus status = BookmarkStatus.NEUTRAL;
    int incoming = 0;

    // Auto timestamp
    Date dateCreated;
    Date lastUpdated;

    static mapping = {
        cache usage: 'read-write';
    }

    static belongsTo = [owner: Profile, target: Profile];

    static constraints = {
        owner(nullable: false);
        target(nullable: false, unique: 'owner');
        status(nullable: false);
        incoming(min: 0);
    }
}

enum BookmarkStatus {
    BAN, NEUTRAL, CONFIRMED
}
