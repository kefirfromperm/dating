package com.greetim

/**
 * Notification about new message for user
 */
class MessageNotification extends Notification {
    // Message.
    Message message;
    static constraints = {
        message(nullable:false);
    }
}
