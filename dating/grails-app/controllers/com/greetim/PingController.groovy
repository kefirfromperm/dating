package com.greetim

import java.text.SimpleDateFormat

class PingController {
    static defaultAction = 'ping';

    def ping = {
        render(text: 'OK', contentType: 'text/plain');
    }

    def time = {
        render(
                text: new SimpleDateFormat('yyyy-MM-dd HH:mm:ss.SSS zzzz').format(new Date()),
                contentType: 'text/plain'
        );
    }
}
