package com.greetim.l10n;

import org.codehaus.groovy.grails.web.util.WebUtils;
import org.springframework.web.util.CookieGenerator;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.TimeZone;

/**
 * Resolve the user time zone by cookie
 *
 * @author Vitaliy Samolovskih aka Kefir
 */
public class CookieTimeZoneResolver extends CookieGenerator implements TimeZoneResolver {
    /**
     * Request attribute for store the user time zone.
     */
    private static final String TIME_ZONE_REQUEST_ATTRIBUTE_NAME = CookieTimeZoneResolver.class.getName() + ".TIMEZONE";

    /**
     * Cookie name for store the user time zone.
     */
    private static final String TIME_ZONE_COOKIE_NAME = "GREETIM_USER_TIME_ZONE";

    /**
     * The default time zone is UTC.
     */
    private static final TimeZone DEFAULT_TIME_ZONE = TimeZone.getTimeZone("UTC");

    public CookieTimeZoneResolver() {
        setCookieName(TIME_ZONE_COOKIE_NAME);
    }

    /**
     * Resolve the user time zone by request. Find in request attribute first. Find in cookie.
     * Or return default UTC value.
     *
     * @param request user request
     * @return time zone
     */
    @Override
    public TimeZone resolveTimeZone(HttpServletRequest request) {
        // Find time zone by request attribute
        TimeZone timeZone = (TimeZone) request.getAttribute(TIME_ZONE_REQUEST_ATTRIBUTE_NAME);
        if (timeZone != null) {
            return timeZone;
        }

        // Parse cookie value
        Cookie cookie = WebUtils.getCookie(request, TIME_ZONE_COOKIE_NAME);
        if (cookie != null) {
            timeZone = TimeZone.getTimeZone(cookie.getValue());
            if (timeZone != null) {
                request.setAttribute(TIME_ZONE_REQUEST_ATTRIBUTE_NAME, timeZone);
                return timeZone;
            }
        }

        // If can't find time zone then return default UTC
        return DEFAULT_TIME_ZONE;
    }

    /**
     * Set time zone for user. Set cookie and request attribute.
     *
     * @param request  HTTP request
     * @param response HTTP response
     * @param timeZone time zon for setting
     */
    @Override
    public void setTimeZone(HttpServletRequest request, HttpServletResponse response, TimeZone timeZone) {
        if (timeZone != null) {
            request.setAttribute(TIME_ZONE_REQUEST_ATTRIBUTE_NAME, timeZone);
            addCookie(response, timeZone.getID());
        } else {
            request.setAttribute(TIME_ZONE_REQUEST_ATTRIBUTE_NAME, DEFAULT_TIME_ZONE);
            addCookie(response, DEFAULT_TIME_ZONE.getID());
        }
    }
}
