package com.greetim

import grails.plugins.springsecurity.Secured
import java.text.DateFormat
import java.text.SimpleDateFormat
import javax.servlet.http.HttpServletRequest
import org.apache.commons.lang.StringUtils

@Secured('ROLE_USER')
class MessageController {

    ProfileService profileService;
    BookmarkService bookmarkService;
    NotificationService notificationService;

    def index = {
        redirect(controller: 'profile', action: 'list');
    }

    /**
     * Show messenger in new window
     */
    def messenger = {
        Profile current = DatingUtils.currentProfile(request);
        Profile profile = profileService.findByAlias(params.alias);
        if (current && profile && current != profile) {
            LastMessagesCommand messagesCommand = new LastMessagesCommand();
            messagesCommand.profile1 = current;
            messagesCommand.profile2 = profile;
            messagesCommand.execute();

            render(
                    view: 'window',
                    model: [
                            bookmark: Bookmark.findByOwnerAndTarget(current, profile),
                            messagesCommand: messagesCommand,
                            profile: profile
                    ]
            );
        } else {
            render(status: 404);
        }
    }

    private Closure withProfiles(Closure cl) {
        return {
            Profile current = DatingUtils.currentProfile(request);
            Profile profile = Profile.get(params.profile.id);
            if (current && profile) {
                return cl(current, profile);
            } else {
                if (ajax(request)) {
                    if (acceptJson(request)) {
                        render(contentType: "text/json") {
                            status = 'error';
                        }
                    } else {
                        render(status: 500);
                    }
                } else {
                    flash.message = 'profile.not.found';
                    flash.error = true;
                    redirect(controller: 'profile', action: 'list');
                }
            }
        };
    }

    /**
     * Send single message
     */
    def send = withProfiles {Profile current, Profile other ->
        Message messageInstance = new Message(params);
        messageInstance.from = current;
        messageInstance.to = other;
        messageInstance.date = new Date();
        messageInstance.validate();
        if (!messageInstance.hasErrors() && messageInstance.save(flush: true)) {
            // Add undelivered message count to recipient
            bookmarkService.incrementIncoming(messageInstance.to, messageInstance.from);

            // Mark all messages from recipient as delivered
            bookmarkService.clearIncoming(messageInstance.from, messageInstance.to);

            // Send notification for user
            notificationService.notifyUser(messageInstance);

            if (ajax(request)) {
                render(contentType: "text/json") {
                    status = 'success';
                }
                return;
            }
        } else {
            if (ajax(request)) {
                def errorMessages = messageInstance.errors.allErrors.collect {message('error': it)};
                render(contentType: "text/json") {
                    status = 'error';
                    delegate."errors" = errorMessages;
                }
                return;
            }
        }

        if (params.window) {
            redirect(controller: 'message', action: 'messenger', params: [alias: other.alias]);
        } else {
            redirect(controller: 'profile', action: 'show', params: [alias: other.alias]);
        }
    }

    /**
     * Get all messages after timestamp
     * AJAX only
     */
    def last = withProfiles {Profile current, Profile other ->
        def lastMessageTimestamp = System.currentTimeMillis();
        List<Message> messageList = Message.after(current, other, params.long('timestamp'));
        if (messageList.size() > 0) {
            lastMessageTimestamp = messageList.last().date.time;
        }
        boolean hasNew = false;
        for (Message message: messageList) {
            if (message.to == current) {
                hasNew = true;
                break;
            }
        }
        DateFormat dateFormat = new SimpleDateFormat(message(code: 'message.date.format', default: 'MM-dd HH:mm:ss'));
        render(contentType: "text/json") {
            timestamp = lastMessageTimestamp;
            incoming = hasNew;
            delegate.messages = array {
                for (Message m: messageList) {
                    item([
                            date: dateFormat.format(m.date),
                            from: m.from.alias.encodeAsHTML(),
                            text: m.text.encodeAsHTML(),
                            direction: m.from == current ? 'out' : 'in'
                    ]);
                }
            }
        }
    }

    /**
     * Load ten messages before timestamp
     * AJAX only
     */
    def before = withProfiles {Profile current, Profile other ->
        List<Message> messageList = Message.before(current, other, params.long('timestamp'), 10);

        long firstMessageTimestamp = 0;
        if (messageList.size() > 0) {
            firstMessageTimestamp = messageList.last().date.time;
        }

        DateFormat dateFormat = new SimpleDateFormat();
        render(contentType: "text/json") {
            timestamp = firstMessageTimestamp;
            delegate.messages = array {
                for (Message m: messageList) {
                    item([
                            date: dateFormat.format(m.date),
                            from: m.from.alias.encodeAsHTML(),
                            text: m.text.encodeAsHTML(),
                            direction: m.from == current ? 'out' : 'in'
                    ]);
                }
            }
        }
    }

    def markAsDelivered = withProfiles {Profile current, Profile other ->
        // Mark all messages from recipient as delivered
        bookmarkService.clearIncoming(current, other);

        if (ajax(request)) {
            render(status: 200);
        } else {
            redirect(controller: 'profile', action: 'show', params: [alias: other.alias]);
        }
    }

    private boolean ajax(HttpServletRequest request) {
        return StringUtils.equalsIgnoreCase(request.getHeader('X-Requested-With'), 'XMLHttpRequest');
    }

    private boolean acceptJson(request) {
        if (!(request instanceof HttpServletRequest)) {
            return false;
        }

        String acceptHeader = ((HttpServletRequest) request).getHeader('Accept');
        return StringUtils.containsIgnoreCase(acceptHeader, 'text/json') ||
                StringUtils.containsIgnoreCase(acceptHeader, 'application/json');
    }
}
