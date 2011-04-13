package ru.perm.kefir.dating

import grails.plugins.springsecurity.Secured

@Secured('ROLE_USER')
class BookmarkController {

    ProfileService profileService;
    BookmarkService bookmarkService;

    def index = {
        redirect(controller: 'profile', action: 'list');
    }

    private change = {BookmarkStatus status ->
        Profile current = DatingUtils.currentProfile(request);
        Profile profile = profileService.findByAlias(params.alias);
        if (current && profile) {
            Bookmark bookmark = bookmarkService.find(current, profile);
            bookmark.status = status;
            bookmark.save(flush: true);
            flash.message="bookmark.change.status.${status.name().toLowerCase()}";
            redirect(controller: 'profile', action: 'show', params: [alias: profile.alias]);
        } else {
            flash.message = 'profile.not.found';
            flash.error = true;
            redirect(controller: 'profile');
        }
    }

    def ban = change.curry(BookmarkStatus.BAN);
    def remove = change.curry(BookmarkStatus.NEUTRAL);
    def add = change.curry(BookmarkStatus.CONFIRMED);

    def content = {
        Profile profile = DatingUtils.currentProfile(request);
        if (profile) {
            render(
                    template: '/bookmark/bookmarksContent',
                    model: [
                            bookmarks: bookmarkService.confirmed(profile),
                            incomings: bookmarkService.incoming(profile)
                    ]
            );
        } else {
            render(status: 404);
        }
    }
}
