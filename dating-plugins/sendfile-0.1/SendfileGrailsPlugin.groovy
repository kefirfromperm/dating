import java.nio.ByteBuffer
import java.nio.channels.ReadableByteChannel

class SendfileGrailsPlugin {
    // the plugin version
    def version = "0.1"
    // the version or versions of Grails the plugin is designed for
    def grailsVersion = "1.3.6 > *"
    // the other plugins this plugin depends on
    def dependsOn = [:]
    // resources that are excluded from plugin packaging
    def pluginExcludes = [
            "grails-app/views/error.gsp"
    ]

    def author = "Vitaliy Samolovskih"
    def authorEmail = "kefir@perm.ru"
    def title = "Sendfile for Tomcat, Apache, Nginx"
    def description = '''\\
Allow to use senfile or X-sendfile functionality of web-servers.
'''

    // URL to the plugin's documentation
    def documentation = "http://grails.org/plugin/sendfile"

    def loadAfter = ['controllers']
    def observe = ['controllers']
    def influences = ['controllers']

    def doWithDynamicMethods = { ctx ->
        configureSendFile(application, ctx);
    }

    def onChange = { event ->
        configureSendFile(event.application, event.ctx);
    }

    def onConfigChange = { event ->
        configureSendFile(event.application, event.ctx);
    }

    private configureSendFile(application, context){
        //adding sendFile to controllers
        application.controllerClasses*.metaClass*.sendFile = {File file, Map params=[:] ->
            context.sendFileService?.sendFile(servletContext, request, response, file, params);
        }

        application.controllerClasses*.metaClass*.sendFile = {String path, Map params=[:] ->
            context.sendFileService?.sendFile(servletContext, request, response, path, params);
        }

        application.controllerClasses*.metaClass*.sendFile = {byte[] content, Map params=[:] ->
            context.sendFileService?.sendFile(request, response, content, params);
        }

        application.controllerClasses*.metaClass*.sendFile = {InputStream is, Map params=[:] ->
            context.sendFileService?.sendFile(request, response, is, params);
        }

        application.controllerClasses*.metaClass*.sendFile = {ByteBuffer content, Map params=[:] ->
            context.sendFileService?.sendFile(request, response, content, params);
        }

        application.controllerClasses*.metaClass*.sendFile = {ReadableByteChannel channel, Map params=[:] ->
            context.sendFileService?.sendFile(request, response, channel, params);
        }
    }
}
