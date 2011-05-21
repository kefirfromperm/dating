package com.greetim

import java.text.SimpleDateFormat
import org.codehaus.groovy.grails.commons.ConfigurationHolder

class SitemapController {
    def index = {
        def serverUrl = ConfigurationHolder.config.grails.serverURL;
        def dateFormat = new SimpleDateFormat("yyyy-MM-dd")
        dateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
        List<Profile> profiles = Profile.list();
        render(contentType:"text/xml", encoding: "UTF-8"){
            urlset(xmlns:'http://www.sitemaps.org/schemas/sitemap/0.9'){
                for(profile in profiles){
                    url {
                        loc "${serverUrl}/look/${profile.alias.encodeAsURL()}"
                        lastmod dateFormat.format(profile.lastUpdated);
                        changefreq 'weekly'
                    }
                }
            }
        }
    }
}
