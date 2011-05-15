package com.greetim

import ru.perm.kefir.asynchronousmail.AsynchronousMailService
import grails.util.Environment
import org.springframework.transaction.TransactionStatus
import org.codehaus.groovy.grails.commons.ConfigurationHolder

class NotificationService {
    static transactional = true;

    AsynchronousMailService asynchronousMailService;
    TemplateService templateService;

    /**
     * Notify (message.to) user about new message.
     */
    public void notifyUser(Message message) {
        new MessageNotification(
                recipient: message.to,
                status: NotificationStatus.CREATED,
                message: message
        ).save(flush: true);
    }

    /**
     * Send notifications for all users
     */
    public void sendNotifications() {
        Calendar c = Calendar.getInstance();
        c.add(Calendar.MINUTE, -ConfigurationHolder.config.dating.notification.interval);
        def last = c.getTime();
        List<Profile> recipients = Profile.executeQuery(
                "select distinct n.recipient from Notification n where n.status=:status and not exists(" +
                        "select n1 from Notification n1 " +
                        "where n1.recipient=n.recipient and n1.status=:sent and n1.dateCreated>:last" +
                        ")",
                [status: NotificationStatus.CREATED, sent: NotificationStatus.SENT, last: last]
        );
        recipients.each {Profile recipient ->
            sendNotifications(recipient);
        }
    }

    /**
     * Send notifications to user
     * @param recipient notification recipient
     */
    public void sendNotifications(Profile recipient) {
        Notification.withTransaction {TransactionStatus transactionStatus ->
            List<Notification> notifications = Notification.findAllByRecipientAndStatus(
                    recipient,
                    NotificationStatus.CREATED,
                    [sort: 'dateCreated', lock: true]
            );

            if (notifications) {
                // Mark all notifications as ERROR. It guarantee that message will be sent max one time.
                def now = new Date();
                notifications.each {Notification notification ->
                    notification.status = NotificationStatus.ERROR;
                    notification.sentDate = now;
                    notification.save(flush: true);
                }

                try {
                    // Send notifications by mail
                    asynchronousMailService.sendAsynchronousMail {
                        to recipient.account.mail;
                        Map params = [notifications: notifications, recipient: recipient];
                        subject templateService.process("notificationSubject", params);
                        html templateService.process("notificationBody", params);
                        immediate true

                        if (Environment.current == Environment.PRODUCTION) {
                            delete true;
                        }
                    }

                    // Mark all notifications as SENT
                    notifications.each {Notification notification ->
                        notification.status = NotificationStatus.SENT;
                        notification.save(flush: true);
                    }
                } catch (Exception e) {
                    log.error(e);
                }
            }
        }
    }
}
