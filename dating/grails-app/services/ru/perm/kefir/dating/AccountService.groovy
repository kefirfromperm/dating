package ru.perm.kefir.dating

import grails.plugins.springsecurity.SpringSecurityService
import org.codehaus.groovy.grails.plugins.springsecurity.SpringSecurityUtils
import ru.perm.kefir.asynchronousmail.AsynchronousMailService
import ru.perm.kefir.dating.security.MailAuthenticationFilter

class AccountService {
    static transactional = true

    SpringSecurityService springSecurityService;
    AsynchronousMailService asynchronousMailService;
    TemplateService templateService;

    /**
     * Restore password. Generate new code and send mail message.
     */
    def restore(Account account){
        String code = generateCode();
        account.confirmCode = encode(code);
        account.save(flush:true);

        String confirmUrl = "${MailAuthenticationFilter.LOGIN_URL}?code=${code}";
        asynchronousMailService.sendAsynchronousMail {
            to account.mail
            Map params = [url:confirmUrl];
            subject templateService.process("restoreSubject", params);
            html templateService.process("restoreBody", params);
            immediate true

            // Uncomment when for production
            //delete true
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
