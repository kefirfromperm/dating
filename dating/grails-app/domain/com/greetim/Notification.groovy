package com.greetim

/**
 * Notifications for users about events. New message for example.
 */
abstract class Notification {
    // We link notification with profile. Because profile represent people, not account.
    // User, who must be receive notification.
    Profile recipient;

    NotificationStatus status = NotificationStatus.CREATED;

    // Auto timestamp
    Date dateCreated;

    // Sent date can be null
    Date sentDate;

    static mapping = {
        // Use one table for all notifications
        tablePerHierarchy false;
    }

    static constraints = {
        recipient(nullable: false);
        status(nullable: false);
    }
}

enum NotificationStatus {
    CREATED, SENT, ERROR, CANCELED
}
