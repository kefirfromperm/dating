package com.greetim

import org.apache.http.HttpEntity
import org.apache.http.HttpResponse
import org.apache.http.HttpStatus
import org.apache.http.client.HttpClient
import org.apache.http.client.methods.HttpGet
import org.apache.http.impl.client.DefaultHttpClient

class GravatarService {
    static transactional = false

    GravatarService() {
    }

    boolean hasGravatar(def mail) {
        def code = 0;
        HttpClient httpClient = new DefaultHttpClient();
        try {
            HttpGet get = new HttpGet(profileXmlUrl(mail));
            HttpResponse response = httpClient.execute(get);
            code = response.getStatusLine().statusCode;
            HttpEntity entity = response.getEntity();
            if (entity) {
                entity.consumeContent();
            }
        } catch (Exception e) {
            log.error("Error on get gravatar profile for email \"${mail}\"", e);
        } finally {
            httpClient.getConnectionManager().shutdown();
        }
        return code == HttpStatus.SC_OK;
    }

    Map requestGravatarProfile(def mail) {
        def profile = [:];
        HttpClient httpClient = new DefaultHttpClient();
        try {
            HttpGet get = new HttpGet(profileXmlUrl(mail));
            HttpResponse response = httpClient.execute(get);
            int code = response.getStatusLine().statusCode;
            HttpEntity entity = response.getEntity();
            if (entity) {
                if (code == HttpStatus.SC_OK) {
                    def e = new XmlSlurper().parse(entity.getContent()).entry;
                    profile.put('useGravatar', !e.thumbnailUrl.isEmpty());
                    if (!e.name.isEmpty()) {
                        profile.put('name', "${e.name.givenName.text()} ${e.name.familyName.text()}");
                    } else if (!e.displayName.isEmpty()) {
                        profile.put('name', e.displayName.text());
                    } else {
                        profile.put('name', e.preferredUsername.text());
                    }
                    profile.put('alias', e.preferredUsername.text());
                    profile.put('about', e.aboutMe.text());
                }
                entity.consumeContent();
            }
        } catch (Exception e) {
            log.error("Error on get gravatar profile for email \"${mail}\"", e);
        } finally {
            httpClient.getConnectionManager().shutdown();
        }
        return profile;
    }

    /**
     * Get profile url
     */
    String profileUrl(def mail) {
        return "http://www.gravatar.com/${hash(mail)}";
    }

    String profileXmlUrl(def mail) {
        return profileUrl(mail)+'.xml';
    }
    /**
     * Get image url
     */
    String imageUrl(def mail, def size = 80, def rating = 'x', def defaultImage = 404) {
        return "http://www.gravatar.com/avatar/${hash(mail)}?s=${size.encodeAsURL()}&d=${defaultImage.encodeAsURL()}&r=${rating?.encodeAsURL()}";
    }

    /**
     * Get hash code by mail
     */
    String hash(def mail) {
        if (mail == null) {
            throw new IllegalArgumentException('Argument mail can not be null!');
        }
        return mail.toString().trim().toLowerCase().encodeAsMD5();
    }
}
