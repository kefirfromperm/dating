package com.greetim

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
            flash.message = "bookmark.change.status.${status.name().toLowerCase()}";
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
            boolean update = true;
            if (params['timestamp']) {
                long timestamp = params.long('timestamp');
                List list = Bookmark.executeQuery(
                        "select max(b.lastUpdated) from Bookmark b where b.owner=:owner and b.status<>:status",
                        [owner: profile, status: BookmarkStatus.BAN]
                );
                if (list && !list.empty) {
                    Date max = (Date) list[0];
                    update = timestamp < max.time;
                } else {
                    update = false;
                }
            }

            if (update) {
                render(
                        template: '/bookmark/bookmarksContent',
                        model: bookmarkService.bookmarkModel(profile)
                );
            } else {
                render(status: 304);
            }
        } else {
            render(status: 404);
        }
    }
}
