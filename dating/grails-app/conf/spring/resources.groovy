import com.greetim.security.DatingUserDetailsService
import com.greetim.security.MailAuthenticationFilter
import com.greetim.security.MailAuthenticationProvider
import com.greetim.template.BbDirective
import freemarker.template.utility.XmlEscape
import org.codehaus.groovy.grails.commons.ConfigurationHolder
import org.springframework.cache.ehcache.EhCacheFactoryBean
import org.springframework.cache.ehcache.EhCacheManagerFactoryBean
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler
import org.springframework.web.servlet.view.freemarker.FreeMarkerConfigurer
import com.greetim.l10n.CookieTimeZoneResolver

// Place your Spring DSL code here
beans = {
    // Freemarker configuration
    bbDirective(BbDirective) {
        bbService = ref('bbService')
    }

    freemarkerConfig(FreeMarkerConfigurer) {bean ->
        templateLoaderPath = "/WEB-INF/templates/";
        defaultEncoding = "UTF-8";
        freemarkerVariables = [
                xml_escape: new XmlEscape(),
                serverUrl: ConfigurationHolder.config.grails.serverURL,
                bb: ref('bbDirective')
        ];
    }

    // Security
    userDetailsService(DatingUserDetailsService)

    mailAuthenticationFilter(MailAuthenticationFilter) {
        authenticationManager = ref("authenticationManager");
        authenticationSuccessHandler = {SimpleUrlAuthenticationSuccessHandler handler ->
            defaultTargetUrl = '/account/edit'
        };
        authenticationFailureHandler = ref('authenticationFailureHandler')
    }

    mailAuthenticationProvider(MailAuthenticationProvider) {
        accountService = ref("accountService");
        transactionManager = ref("transactionManager");
        userDetailsService = ref("userDetailsService");
    }

    // Caches
    cacheManager(EhCacheManagerFactoryBean)

    accountProfileCache(EhCacheFactoryBean) {
        cacheManager = ref('cacheManager');
        cacheName = 'accountProfileCache';
    }

    aliasProfileCache(EhCacheFactoryBean) {
        cacheManager = ref('cacheManager');
        cacheName = 'accountProfileCache';
    }

    rankCache(EhCacheFactoryBean) {
        cacheManager = ref('cacheManager');
        cacheName = 'rankCache';
    }

    // The user time zone resolver
    timeZoneResolver(CookieTimeZoneResolver);
}
