class DbfileGrailsPlugin {
    // the plugin version
    def version = "0.2"
    // the version or versions of Grails the plugin is designed for
    def grailsVersion = "1.3.6 > *"
    // the other plugins this plugin depends on
    def dependsOn = ['hibernate': '1.3.6 > *', 'sendfile':'0.1 > *']
    // resources that are excluded from plugin packaging
    def pluginExcludes = [
            "grails-app/views/error.gsp",
            "grails-app/conf/DataSource.groovy",
            "grails-app/conf/UrlMappings.groovy"
    ]

    def author = "Vitaliy Samolovskih"
    def authorEmail = "kefir@perm.ru"
    def title = "Store files in DB"
    def description = '''\\
Store files in Db and get them.
'''

    // URL to the plugin's documentation
    def documentation = ""
}
