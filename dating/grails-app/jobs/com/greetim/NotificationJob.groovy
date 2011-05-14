package com.greetim


class NotificationJob {
    def group = "Greetim";
    def concurrent = false;

    NotificationService notificationService;

    def triggers = {
        simple(name: 'notificationTrigger', startDelay: 60000, repeatInterval: 60000);
    }

    def execute() {
        notificationService.sendNotifications();
    }
}
