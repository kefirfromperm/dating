import grails.plugins.springsecurity.SecurityConfigType
import pl.burningice.plugins.image.engines.RenderingEngine

// locations to search for config files that get merged into the main config
// config files can either be Java properties files or ConfigSlurper scripts

// grails.config.locations = [ "classpath:${appName}-config.properties",
//                             "classpath:${appName}-config.groovy",
//                             "file:${userHome}/.grails/${appName}-config.properties",
//                             "file:${userHome}/.grails/${appName}-config.groovy"]

// if(System.properties["${appName}.config.location"]) {
//    grails.config.locations << "file:" + System.properties["${appName}.config.location"]
// }

grails.project.groupId = appName // change this to alter the default package name and Maven publishing destination
grails.mime.file.extensions = true // enables the parsing of file extensions from URLs into the request format
grails.mime.use.accept.header = false
grails.mime.types = [html: ['text/html', 'application/xhtml+xml'],
        xml: ['text/xml', 'application/xml'],
        text: 'text/plain',
        js: 'text/javascript',
        rss: 'application/rss+xml',
        atom: 'application/atom+xml',
        css: 'text/css',
        csv: 'text/csv',
        all: '*/*',
        json: ['application/json', 'text/json'],
        form: 'application/x-www-form-urlencoded',
        multipartForm: 'multipart/form-data'
]

// URL Mapping Cache Max Size, defaults to 5000
//grails.urlmapping.cache.maxsize = 1000

// The default codec used to encode data with ${}
grails.views.default.codec = "none" // none, html, base64
grails.views.gsp.encoding = "UTF-8"
grails.converters.encoding = "UTF-8"
// enable Sitemesh preprocessing of GSP pages
grails.views.gsp.sitemesh.preprocess = true
// scaffolding templates configuration
grails.scaffolding.templates.domainSuffix = 'Instance'

// Set to false to use the new Grails 1.2 JSONBuilder in the render method
grails.json.legacy.builder = false
// enabled native2ascii conversion of i18n properties files
grails.enable.native2ascii = true
// whether to install the java.util.logging bridge for sl4j. Disable for AppEngine!
grails.logging.jul.usebridge = true
// packages to include in Spring bean scanning
grails.spring.bean.packages = []

// set per-environment serverURL stem for creating absolute links
environments {
    production {
        grails.serverURL = "http://greetim.com"
    }
    development {
        grails.serverURL = "http://localhost:8080/${appName}"
    }
    test {
        grails.serverURL = "http://localhost:8080/${appName}"
    }

}

// log4j configuration
log4j = {
    // Example of changing the log pattern for the default console
    // appender:
    //
    //appenders {
    //    console name:'stdout', layout:pattern(conversionPattern: '%c{2} %m%n')
    //}
    root {
        warn()
    }

    error 'org.codehaus.groovy.grails.web.servlet',  //  controllers
            'org.codehaus.groovy.grails.web.pages', //  GSP
            'org.codehaus.groovy.grails.web.sitemesh', //  layouts
            'org.codehaus.groovy.grails.web.mapping.filter', // URL mapping
            'org.codehaus.groovy.grails.web.mapping', // URL mapping
            'org.codehaus.groovy.grails.commons', // core / classloading
            'org.codehaus.groovy.grails.plugins', // plugins
            'org.codehaus.groovy.grails.orm.hibernate', // hibernate integration
            'org.springframework',
            'org.hibernate',
            'net.sf.ehcache.hibernate'

    warn 'org.mortbay.log'

    // My classes
    debug 'grails.app.controller.com.greetim.MessageController',
            'dating.LogFilters'
}

// Validation for non domain classes
grails.validateable.packages = ['com.greetim']

// Spring Security configuration
grails.plugins.springsecurity.active = true

/** login user class fields  */
grails.plugins.springsecurity.userLookup.userDomainClassName = "com.greetim.Account"
grails.plugins.springsecurity.userLookup.usernamePropertyName = 'mail'
grails.plugins.springsecurity.userLookup.passwordPropertyName = 'passwordDigest'
grails.plugins.springsecurity.userLookup.authoritiesPropertyName = 'roles'
grails.plugins.springsecurity.userLookup.enabledPropertyName = 'enabled'
grails.plugins.springsecurity.userLookup.accountLockedPropertyName = 'locked'

grails.plugins.springsecurity.authority.className = "com.greetim.Role"
grails.plugins.springsecurity.authority.nameField = 'authority'

// Cache options
grails.plugins.springsecurity.cacheUsers = true
grails.plugins.springsecurity.rememberMe.key = 'I love PostgreSQL 9.0'
grails.plugins.springsecurity.dao.reflectionSaltSourceProperty = 'salt'

// Default URL after authentication
grails.plugins.springsecurity.successHandler.defaultTargetUrl = '/profile/show'

// We use annotations access control
grails.plugins.springsecurity.securityConfigType = SecurityConfigType.Annotation

// Prevent session-fixation attack
grails.plugins.springsecurity.useSessionFixationPrevention = true

// Code authentication
grails.plugins.springsecurity.providerNames = [
        'daoAuthenticationProvider', 'rememberMeAuthenticationProvider',
        'mailAuthenticationProvider', 'anonymousAuthenticationProvider'
];

// Filters
grails.plugins.springsecurity.useSwitchUserFilter = true

grails.plugins.springsecurity.filterChain.chainMap = [
        '/j_mail_check': 'securityContextPersistenceFilter,mailAuthenticationFilter,anonymousAuthenticationFilter,exceptionTranslationFilter,filterInvocationInterceptor',
        '/**': 'JOINED_FILTERS',
]

// Mail configuration
environments {
    development {
        // Mail configuration
        grails.mail.disabled = true
    }
    test {
        grails.mail.disabled = true
    }
    production {
        // Mail configuration
        grails.mail.host = "localhost"
        grails.mail.props = ["mail.transport.protocol": "smtp"]
        grails.mail.default.from = "noreply@greetim.com"
    }
}

// Burning image configuration
environments {
    production {
        bi.renderingEngine = RenderingEngine.IMAGE_MAGICK
    }
}

// Saga files
environments {
    development {
        saga.file.cache.dir = 'd:/tmp/dating'
    }
    production {
        saga.file.cache.dir = '/home/dating/files'
        grails.plugins.sendfile.nginx = true
    }
}

// Our configuration
dating.page.max = 30;
dating.photo.vertical.size = 300;
dating.photo.horizontal.size = 200;