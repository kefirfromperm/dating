package com.greetim

class NotificationService {

    static transactional = true

    public void notify(Message message) {
        new MessageNotification(
                recipient: message.to,
                status: NotificationStatus.CREATED,
                message: message
        ).save(flush: true);
    }
}
