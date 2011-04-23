package com.greetim

import com.greetim.security.MailAuthenticationFilter
import grails.plugins.springsecurity.SpringSecurityService
import grails.util.Environment
import org.codehaus.groovy.grails.plugins.springsecurity.SpringSecurityUtils
import ru.perm.kefir.asynchronousmail.AsynchronousMailService

class AccountService {
    static transactional = true

    SpringSecurityService springSecurityService;
    AsynchronousMailService asynchronousMailService;
    TemplateService templateService;

    /**
     * Restore password. Generate new code and send mail message.
     */
    def restore(Account account, Locale locale){
        String code = generateCode();
        account.confirmCode = encode(code);
        account.save(flush:true);

        String confirmUrl = "${MailAuthenticationFilter.LOGIN_URL}?code=${code}";
        asynchronousMailService.sendAsynchronousMail {
            to account.mail
            Map params = [url:confirmUrl];
            subject templateService.process("restoreSubject", params, locale);
            html templateService.process("restoreBody", params, locale);
            immediate true

            if(Environment.current == Environment.PRODUCTION){
                delete true;
            }
        }
    }

    /**
     * Generate confirm code
     */
    String generateCode() {
        return UUID.randomUUID().toString()
    }

    /**
     * Encode code
     */
    private String encode(String code) {
        springSecurityService.encodePassword(code, 'No fate but what we make!');
    }

    /**
     * Find account by code
     */
    Account findByCode(String code){
        Account.findByConfirmCode(encode(code));
    }

    /**
     * Account of current user,
     * null, if not logged in
     */
    Account current(){
        return (Account) springSecurityService.currentUser;
    }

    /**
     * Check access to edit this account
     */
    boolean access(Account account){
        boolean flag = SpringSecurityUtils.ifAllGranted('ROLE_ADMIN');

        if(!flag){
            flag = (springSecurityService.principal?.id == account.id);
        }

        return flag;
    }
}
