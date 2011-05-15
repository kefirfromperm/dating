package com.greetim

/**
 * Notifications for users about events. New message for example.
 * It must be abstract, but it can't be abstract, because GORM don't support
 * inheritance from abstract classes.
 * We use table-per-hierarchy inheritance strategy for velocity.
 */
class Notification {
    // We link notification with profile. Because profile represent people, not account.
    // User, who must be receive notification.
    Profile recipient;

    NotificationStatus status = NotificationStatus.CREATED;

    // Auto timestamp
    Date dateCreated;

    // Sent date can be null
    Date sentDate;

    static mapping = {
    }

    static constraints = {
        recipient(nullable: false);
        status(nullable: false);
        sentDate(nullable:true);
    }
}

enum NotificationStatus {
    CREATED, SENT, ERROR, CANCELED
}
