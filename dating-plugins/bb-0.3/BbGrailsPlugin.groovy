class BbGrailsPlugin {
    // the plugin version
    def version = "0.3"
    // the version or versions of Grails the plugin is designed for
    def grailsVersion = "1.1.2 > *"
    // resources that are excluded from plugin packaging
    def pluginExcludes = [
            "grails-app/views/error.gsp",
            "grails-app/conf/kefirbb.xml",
            "grails-app/conf/spec.xml",
            "grails-app/conf/DataSource.groovy",
            "grails-app/conf/UrlMappings.groovy"
    ]

    // TODO Fill in these fields
    def author = "Vitaliy Samolovskih aka Kefir"
    def authorEmail = "kefir@perm.ru"
    def title = "Support bb codes with KefirBB"
    def description = '''\\
Its' support text processors with KefirBB
'''

    // URL to the plugin's documentation
    def documentation = "http://kefir-bb.sourceforge.net"
}
