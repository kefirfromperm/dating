package ru.perm.kefir.dating

import org.codehaus.groovy.grails.plugins.springsecurity.SpringSecurityUtils
import grails.plugins.springsecurity.SpringSecurityService
import grails.plugins.springsecurity.Secured
import org.codehaus.groovy.grails.commons.ConfigurationHolder
import org.codehaus.groovy.grails.validation.Validateable

class AccountController {
    static allowedMethods = [apply: "POST", update: "POST", delete: "POST"]

    SpringSecurityService springSecurityService;
    AccountService accountService;

    def index = {
        if (SpringSecurityUtils.ifAllGranted('ROLE_ADMIN')) {
            redirect(action: "list");
        } else if (springSecurityService.isLoggedIn()) {
            redirect(action: 'edit');
        } else {
            redirect(action: 'registration');
        }
    }

    @Secured('ROLE_ADMIN')
    def list = {
        params.max = ConfigurationHolder.config.dating.page.max;
        [list: Account.list(params), total: Account.count()]
    }

    def registration = {
        render(view: 'registration');
    }

    /**
     * Apply registration
     */
    def apply = {
        // Find account
        String mail = Account.prepareMail(params.mail);
        Account account = Account.findByMail(mail);

        // Create account if not found
        if (account == null) {
            account = new Account(mail: mail);
            account.addToRoles(Role.findByAuthority('ROLE_USER'));
            if (account.hasErrors() || null == account.save(flush: true)) {
                render(view: 'registration', model: [account: account]);
                return;
            }
        }

        if (!account.locked) {
            // Send message to restore account
            accountService.restore(account);
            flash.mail = account.mail;
            redirect(action: 'success');
        } else {
            flash.message = 'account.locked';
            flash.error = true;
            redirect(action: 'registration');
        }
    }

    /**
     * Success registration message
     */
    def success = {
        render(view: 'success');
    }

    private Closure withCurrent(Closure cl) {
        return {
            Account current = DatingUtils.currentAccount(request);
            if (current) {
                return cl(current);
            } else {
                flash.message='account.current.not.found';
                flash.error = true;
                redirect(action: 'registration');
            }
        };
    }

    @Secured('ROLE_ADMIN')
    def show = withManagedAccount { Account account ->
        [account: account];
    }

    private Closure withManagedAccount(Closure cl) {
        if (SpringSecurityUtils.ifAllGranted('ROLE_ADMIN') && params.id) {
            return withAccountById(cl);
        } else {
            return withCurrent(cl);
        }
    }

    @Secured('ROLE_USER')
    def edit = withManagedAccount {Account account ->
        EditAccountCommand command = new EditAccountCommand();
        command.account = account;
        command.init();
        return [command: command];
    }

    @Secured('ROLE_USER')
    def update = withManagedAccount {Account account ->
        EditAccountCommand command = new EditAccountCommand();
        command.springSecurityService = springSecurityService;
        command.account = account;
        command.init();

        bindData(command, params, [exclude: ['account']]);
        command.validate();

        if (!command.hasErrors()) {
            command.execute();
            flash.message='account.updated.message';
            if (SpringSecurityUtils.ifAllGranted('ROLE_ADMIN')) {
                redirect(action: 'show', id: account.id);
            } else {
                redirect(controller: 'profile');
            }
        } else {
            render(view: 'edit', model: [command: command]);
        }
    }

    /**
     * For admin
     */
    private Closure withAccountById(Closure cl) {
        return {
            Account account = Account.get(params.id);
            if (account) {
                return cl(account);
            } else {
                redirect(action: 'list');
            }
        };
    }

    private Closure changeAccountProperties(Closure cl) {
        return {Account account ->
            cl(account);
            account.save(flush: true);
            flash.message='account.updated.message';
            redirect(action: 'show', id: account.id);
        };
    }

    @Secured('ROLE_ADMIN')
    def lock = withAccountById(changeAccountProperties({Account account ->
        account.locked = true;
    }));

    @Secured('ROLE_ADMIN')
    def unlock = withAccountById(changeAccountProperties({Account account ->
        account.locked = false;
    }));

    @Secured('ROLE_ADMIN')
    def delete = withAccountById {Account account ->
        try {
            account.delete(flush: true)
            flash.message = 'account.deleted.message';
            redirect(action: "list")
        } catch (org.springframework.dao.DataIntegrityViolationException e) {
            flash.message = 'account.not.deleted.message';
            redirect(action: "show", id: params.id)
        }
    }
}

/**
 * Command for account edit
 */
@Validateable
class EditAccountCommand {
    SpringSecurityService springSecurityService;

    Account account;

    // Hidden field, account version
    long version;

    // User fields
    String mail;
    String password;
    String confirm;

    // Admin fields
    boolean enabled;
    boolean locked;

    boolean user;
    boolean admin;

    static constraints = {
        version(validator: {long val, EditAccountCommand cmd -> val == cmd.account.version});
        mail(
                nullable: false, blank: false, email: true, maxSize: 320,
                validator: {String val, EditAccountCommand cmd ->
                    Account.withNewSession {
                        Account indb = Account.findByMail(val);
                        return indb == null || indb.id == cmd.account.id;
                    }
                }
        );
        password(nullable: true, blank: false, size: 6..63);
        confirm(
                nullable: true, blank: true,
                validator: {String val, EditAccountCommand cmd -> val == cmd.password}
        );
    }

    /**
     * Init commend before binding and execute
     */
    public void init() {
        this.version = account.version;
        this.mail = account.mail;

        if (SpringSecurityUtils.ifAllGranted('ROLE_ADMIN')) {
            this.enabled = account.enabled;
            this.locked = account.locked;

            Collection<String> authorities = account.roles*.authority;
            this.user = authorities.contains('ROLE_USER');
            this.admin = authorities.contains('ROLE_ADMIN');
        }
    }

    /**
     * Execute command after data binding
     */
    public boolean execute() {
        if (password) {
            String salt = UUID.randomUUID().toString();
            account.salt = salt;
            account.passwordDigest = springSecurityService.encodePassword(password, salt);
        }

        if (SpringSecurityUtils.ifAllGranted('ROLE_ADMIN')) {
            account.mail = this.mail;

            account.enabled = this.enabled;
            account.locked = this.locked;

            Role userRole = Role.findByAuthority('ROLE_USER');
            Role adminRole = Role.findByAuthority('ROLE_ADMIN');

            if (user) {
                account.addToRoles(userRole);
            } else {
                account.removeFromRoles(userRole);
            }

            if (admin) {
                account.addToRoles(adminRole);
            } else {
                account.removeFromRoles(adminRole);
            }
        }

        return null != account.save(flush: true);
    }

    /**
     * Prepare mail trim and lower
     */
    public void setMail(String mail) {
        this.mail = Account.prepareMail(mail);
    }
}
