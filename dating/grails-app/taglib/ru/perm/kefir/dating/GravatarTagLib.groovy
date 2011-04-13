package ru.perm.kefir.dating

class GravatarTagLib {
    static namespace = 'gr';
    GravatarService gravatarService;

    /**
     * Create link to avatar
     */
    def imageUrl = {attrs ->
        out << gravatarService.imageUrl(attrs.mail, attrs.size ?: 80, 'r', 'monsterid');
    }

    /**
     * Profile URL
     */
    def profileUrl = {attrs ->
        out << gravatarService.profileUrl(attrs.mail);
    }
}
