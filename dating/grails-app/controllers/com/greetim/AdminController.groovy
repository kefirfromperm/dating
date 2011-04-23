package com.greetim

import grails.plugins.springsecurity.Secured

@Secured('ROLE_ADMIN')
class AdminController {
    def index = {
        return [
                accountCount:Account.count(),
                profileCount:Profile.count()
        ];
    }
}
