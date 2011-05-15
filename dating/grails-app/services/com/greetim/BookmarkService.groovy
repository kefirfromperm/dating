package com.greetim

class BookmarkService {
    static transactional = true

    Bookmark find(Profile owner, Profile target) {
        Bookmark bookmark = Bookmark.findByOwnerAndTarget(owner, target);
        if (bookmark == null) {
            bookmark = new Bookmark(owner: owner, target: target);
            bookmark.save(flush: true);
        }
        return bookmark;
    }

    Bookmark incrementIncoming(Profile owner, Profile target) {
        Bookmark bookmark = find(owner, target);
        bookmark.incoming++;
        bookmark.save(flush: true);
        return bookmark;
    }

    Bookmark clearIncoming(Profile owner, Profile target) {
        Bookmark bookmark = Bookmark.findByOwnerAndTarget(owner, target);
        if (bookmark != null && bookmark.incoming > 0) {
            bookmark.incoming = 0;
            bookmark.save(flush: true);
        }
        return bookmark;
    }

    /**
     * Get all confirmed bookmarks for user
     */
    public List<Bookmark> confirmed(Profile profile) {
        Bookmark.executeQuery(
                "select b from Bookmark b join b.target t where b.owner=:owner and b.status=:status order by t.name asc",
                [owner: profile, status: BookmarkStatus.CONFIRMED]
        )
    }

    /**
     * Get all neutral bookmarks with incoming messages
     */
    public List<Bookmark> incoming(Profile profile) {
        Bookmark.executeQuery(
                "select b from Bookmark b join b.target t where b.owner=:owner and b.status=:status and b.incoming>0 order by t.name asc",
                [owner: profile, status: BookmarkStatus.NEUTRAL]
        )
    }

    /**
     * Create bookmark model
     */
    public Map bookmarkModel(Profile profile) {
        List<Bookmark> confirmed = confirmed(profile);
        List<Bookmark> incoming = incoming(profile);

        long bookmarkTimestamp = Math.max(
                confirmed*.lastUpdated.max()?.getTime()?:0,
                incoming*.lastUpdated.max()?.getTime()?:0
        );

        return [
                bookmarkTimestamp: bookmarkTimestamp,
                bookmarks: confirmed,
                incomings: incoming
        ];
    }
}
