package com.greetim

import grails.plugins.springsecurity.Secured
import grails.plugins.springsecurity.SpringSecurityService
import org.apache.commons.lang.StringUtils
import org.codehaus.groovy.grails.commons.ConfigurationHolder
import org.springframework.web.multipart.MultipartFile
import org.springframework.web.multipart.MultipartRequest
import pl.burningice.plugins.image.BurningImageService
import pl.burningice.plugins.image.container.SaveCommand
import ru.permintel.saga.SagaFile
import ru.permintel.saga.SagaFileService

class ProfileController {
    static allowedMethods = [save: "POST", update: "POST", uploadPhoto: 'POST', delete: "POST"]
    def afterInterceptor = [action: this.&bookmarks, only: ['show', 'list']];

    ProfileService profileService;
    AccountService accountService;
    SpringSecurityService springSecurityService;
    GravatarService gravatarService;
    SagaFileService sagaFileService;
    BurningImageService burningImageService;
    BookmarkService bookmarkService;
    TrafficLightService trafficLightService;

    /**
     * After interceptor. Find bookmarks for user.
     */
    def bookmarks(model) {
        Profile current = DatingUtils.currentProfile(request);
        if (current) {
            model.putAll([
                    bookmarks: bookmarkService.confirmed(current),
                    incomings: bookmarkService.incoming(current)
            ]);
        }
    }

    /**
     * Show current profile or create it
     */
    def index = {
        Profile current = DatingUtils.currentProfile(request);
        if (current) {
            redirect(action: 'show', params: [alias: current.alias]);
        } else if (springSecurityService.isLoggedIn()) {
            flash.message = 'profile.create.message';
            redirect(action: 'create');
        } else {
            redirect(action: 'list');
        }
    }

    /**
     * Find random profile for current user
     */
    def luck = {
        Profile current = DatingUtils.currentProfile(request);
        Profile profile = null;
        if (current) {
            profile = Profile.get(trafficLightService.random(current.id));
        }

        if (profile == null) {
            profile = Profile.find("from Profile order by random()");
        }

        if (profile != null) {
            redirect(action: 'show', params: [alias: profile.alias]);
        } else {
            flash.message = 'profile.not.luck';
            redirect(action: 'list');
        }
    }

    /**
     * path = /search
     */
    def list = {
        params.max = ConfigurationHolder.config.dating.page.max;
        String query = StringUtils.trimToNull(params.q);

        List<Profile> profiles = [];
        long total;

        if (query) {
            // full text search
            total = profileService.fullTextSearchCount(query);
            if (total > 0) {
                profiles = profileService.fullTextSearch(query, params);
            }
        } else {
            // Last registered
            if (params.sort == null) {
                params.sort = 'createDate';
                params.order = 'desc';
            }
            total = Profile.count();
            if (total > 0) {
                profiles = Profile.list(params);
            }
        }

        Map lights = [:];
        Profile current = DatingUtils.currentProfile(request);
        if (current) {
            profiles.each {Profile profile ->
                lights.put(profile, trafficLightService.light(current.id, profile.id));
            }
        }

        [list: profiles, total: total, lights: lights];
    }

    /**
     * path = /profile/create
     */
    @Secured('ROLE_USER')
    def create = {
        Profile current = DatingUtils.currentProfile(request);
        if (current) {
            redirect(action: 'edit');
            return;
        }

        // Create new profile
        Profile profile = new Profile();

        // Set current account
        def account = DatingUtils.currentAccount(request);
        profile.account = account;

        // Upload data from Gravatar
        Map data = gravatarService.requestGravatarProfile(account.mail);
        profile.name = data.name;
        profile.about = data.about;
        String alias = StringUtils.lowerCase(data.alias);
        if (alias?.matches(Profile.ALIAS_REGEX)) {
            profile.alias = alias;
        }
        profile.useGravatar = data.useGravatar ?: false;

        return [profile: profile];
    }

    /**
     * path = /profile/save
     */
    @Secured('ROLE_USER')
    def save = {
        Profile current = DatingUtils.currentProfile(request);
        if (current) {
            redirect(action: 'edit');
            return;
        }

        def profile = new Profile(params);
        profile.setAccount(DatingUtils.currentAccount(request));
        profile.createDate = new Date();

        if (profile.save(flush: true)) {
            flash.message = 'profile.created.message';
            redirect(action: "show", model: [alias: profile.alias]);
        } else {
            render(view: "create", model: [profile: profile]);
        }
    }

    /**
     * path = /look/$alias
     */
    def show = {
        Profile current = DatingUtils.currentProfile(request);
        Profile profile;
        if (params.alias) {
            profile = profileService.findByAlias(params.alias);
        } else {
            profile = current;
        }

        if (profile) {
            Map model = [
                    profile: profile,
                    managed: profileService.access(profile)
            ];

            if (current && current != profile) {
                LastMessagesCommand messagesCommand = new LastMessagesCommand();
                messagesCommand.profile1 = current;
                messagesCommand.profile2 = profile;
                messagesCommand.execute();

                model.putAll([
                        bookmark: Bookmark.findByOwnerAndTarget(current, profile),
                        messagesCommand: messagesCommand,
                        light: trafficLightService.light(current.id, profile.id)
                ]);
            }

            return model;
        } else if (params.alias) {
            flash.message = 'profile.not.found';
            redirect(action: "list")
        } else {
            redirect(action: 'create');
        }
    }

    private Closure withManagedProfile(Closure cl) {
        return {
            Profile profile;
            boolean access;
            if (params.id) {
                profile = Profile.get(params.id);
                access = profileService.access(profile);
            } else {
                profile = DatingUtils.currentProfile(request);
                access = true;
            }

            if (profile && access) {
                return cl(profile);
            } else if (!access) {
                flash.message = 'default.access.denied';
                flash.error = true;
                redirect(action: 'show');
            } else {
                flash.message = 'profile.not.found';
                flash.error = true;
                redirect(action: "list")
            }
        };
    }

    /**
     * path = /profile/edit
     */
    @Secured('ROLE_USER')
    def edit = withManagedProfile {Profile profile ->
        return [profile: profile];
    }

    /**
     * path = /profile/update
     */
    @Secured('ROLE_USER')
    def update = withManagedProfile {Profile profile ->
        if (params.version) {
            def version = params.long('version')
            if (profile.version > version) {
                profile.errors.rejectValue("version", "profile.optimistic.locking.failure", "Another user has updated this Profile while you were editing")
                render(view: "edit", model: [profile: profile])
                return;
            }
        }

        bindData(profile, params, [exclude: ['createDate', 'account', 'photo']]);

        if (!profile.hasErrors() && profile.save(flush: true)) {
            flash.message = 'profile.updated.message';
            redirect(action: "show", params: [alias: profile.alias]);
        } else {
            render(view: "edit", model: [profile: profile])
        }
    }

    @Secured('ROLE_USER')
    def photo = withManagedProfile {Profile profile ->
        [profile: profile];
    }

    @Secured('ROLE_USER')
    def uploadPhoto = withManagedProfile {Profile profile ->
        MultipartFile mpf = null;

        // Check if request is multipart
        if (request instanceof MultipartRequest) {
            mpf = request.getFile('photo');
        } else {
            profile.errors.rejectValue('photo', 'profile.photo.not.multipart', 'Error!');
        }

        // Check file content
        if (mpf == null || mpf.empty || !mpf.contentType.startsWith('image')) {
            profile.errors.rejectValue(
                    'photo', 'profile.photo.content.invalid',
                    'File is empty or content type is not accessible!'
            );
        }

        // Check version
        if (params.version) {
            def version = params.long('version');
            if (profile.version > version) {
                profile.errors.rejectValue("version", "profile.optimistic.locking.failure", "Another user has updated this Profile while you were editing")
            }
        }

        // Upload and scale image
        if (!profile.hasErrors()) {
            try {
                PhotoCommand command = new PhotoCommand();
                command.sagaFileService = sagaFileService;
                command.multipartFile = mpf;
                burningImageService.doWith(mpf).execute(command, {
                    it.scaleApproximate(
                            grailsApplication.config.dating.photo.horizontal.size,
                            grailsApplication.config.dating.photo.vertical.size
                    );
                });
                profile.photo = command.file;
                profile.useGravatar = false;
            } catch (Exception e) {
                log.error('Can\'t scale image.', e);
                profile.errors.rejectValue('photo', 'profile.photo.content.invalid', 'Error!');
            }
        }

        // Save profile
        if (!profile.hasErrors() && profile.save(flush: true)) {
            flash.message = 'profile.photo.upload.message';
            redirect(action: 'photo', id: profile.id);
        } else {
            render(view: 'photo', model: [profile: profile]);
        }
    }

    /**
     * Remove user avatar
     */
    @Secured('ROLE_USER')
    def deletePhoto = withManagedProfile {Profile profile ->
        // Check version
        if (params.version) {
            def version = params.long('version');
            if (profile.version > version) {
                profile.errors.rejectValue("version", "profile.optimistic.locking.failure", "Another user has updated this Profile while you were editing")
                render(view: "photo", model: [profile: profile])
                return;
            }
        }

        profile.photo = null;
        profile.useGravatar = false;
        profile.save(flush: true);
        flash.message = 'profile.photo.deleted.message';
        redirect(action: 'photo', id: profile.id);
    }

    @Secured('ROLE_USER')
    def useGravatar = withManagedProfile {Profile profile ->
        // Check version
        if (params.version) {
            def version = params.long('version');
            if (profile.version > version) {
                profile.errors.rejectValue("version", "profilet.optimistic.locking.failure", "Another user has updated this Profile while you were editing");
                render(view: "photo", model: [profile: profile]);
                return;
            }
        }

        profile.useGravatar = !profile.useGravatar;
        profile.save(flush: true);
        redirect(action: 'photo', id: profile.id);
    }

    @Secured('ROLE_USER')
    def confirmDelete = withManagedProfile {Profile profile ->
    }

    /**
     * path = /profile/delete
     */
    @Secured('ROLE_USER')
    def delete = withManagedProfile {Profile profile ->
        String alias = profile.alias;
        if (params.confirm == message(code:'profile.delete.confirm.value')) {
            try {
                profile.delete(flush: true)
                flash.message = 'profile.deleted.message';
                redirect(action: "list")
            } catch (org.springframework.dao.DataIntegrityViolationException e) {
                flash.message = 'profile.not.deleted.message';
                flash.error = true;
                redirect(action: "show", params: [alias: alias]);
            }
        } else {
            flash.message = 'profile.invalid.confirm.message';
            flash.error = true;
            redirect(action: "show", params: [alias: alias]);
        }
    }

    @Secured('ROLE_ADMIN')
    def deleteAndLock = {
        Profile profile = Profile.get(params.id);
        if (profile) {
            try {
                Account account = profile.getAccount();
                account.locked = true;
                account.save(flush: true);

                profile.delete(fluah: true);

                flash.message = 'profile.deleted.locked.message';
                redirect(action: 'list');
            } catch (org.springframework.dao.DataIntegrityViolationException e) {
                flash.message = 'profile.not.deleted.message';
                flash.error = true;
                redirect(action: "show", params: [alias: alias]);
            }
        } else {
            flash.message = 'profile.not.found';
            flash.error = true;
            redirect(action: 'list');
        }
    }
}

/**
 * Command for save photo
 */
class PhotoCommand implements SaveCommand {
    MultipartFile multipartFile;
    SagaFile file;
    SagaFileService sagaFileService;

    @Override
    void execute(byte[] source, String extension) {
        file = sagaFileService.save(
                [name: multipartFile.originalFilename, mimetype: multipartFile.contentType, content: source]
        );
    }
}
