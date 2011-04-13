class UrlMappings {
    static mappings = {
        // File saved in DB
        "/file/$id/$name?"(controller: 'sagaFile', action: 'download');

        // Profile
        "/search"(controller: 'profile', action: 'list');
        "/luck"(controller: 'profile', action: 'luck');
        "/look/$alias"(controller: 'profile', action: 'show');
        "/im/$alias" (controller: 'message', action:'messenger');

        // Bookmarks
        "/bookmark/$action/$alias?"(controller:'bookmark');

        // Common mapping
        "/$controller/$action?/$id?" {
            constraints {
                // apply constraints here
            }
        }

        "/"(controller:'profile', action:'index');
        "500"(view: '/error');
    }
}
