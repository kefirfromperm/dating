package com.greetim.l10n;

import javax.servlet.ServletRequestEvent;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.TimeZone;

/**
 * Resolve Time zone for request, response pair
 *
 * @author Vitaliy Samolovskih aka Kefir
 */
public interface TimeZoneResolver {
    /**
     * Resolve the user time zone by request.
     *
     * @param request user request
     * @return time zone
     */
    public TimeZone resolveTimeZone(HttpServletRequest request);

    /**
     * Set time zone for user.
     *
     * @param request  HTTP request
     * @param response HTTP response
     * @param timeZone time zon for setting
     */
    public void setTimeZone(HttpServletRequest request, HttpServletResponse response, TimeZone timeZone);
}
