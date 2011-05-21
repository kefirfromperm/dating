package com.greetim

import org.apache.commons.lang.StringUtils
import com.greetim.l10n.TimeZoneResolver

/**
 * Resolve time zone by request parameters
 */
class TimeZoneFilterFilters {
    TimeZoneResolver timeZoneResolver;

    def filters = {
        all(controller: '*', action: '*') {
            before = {
                // Parse time zone ID
                String timeZoneID = request.getParameter("timeZoneID");
                if (!StringUtils.isBlank(timeZoneID)) {
                    timeZoneResolver.setTimeZone(request, response, TimeZone.getTimeZone(timeZoneID));
                    return;
                }

                // Parse time zone offset
                String strTimeZoneOffset = request.getParameter("timeZoneOffset");
                if (!StringUtils.isBlank(strTimeZoneOffset) && strTimeZoneOffset.matches(/^(\+|\-)?\d+$/)) {
                    // Parse time zone offset in milliseconds
                    int timeZoneOffset = Integer.decode(strTimeZoneOffset) * 60 * 1000;
                    String[] ids = TimeZone.getAvailableIDs(timeZoneOffset);
                    if (ids != null && ids.length > 0) {
                        timeZoneResolver.setTimeZone(request, response, TimeZone.getTimeZone(ids[0]));
                    }
                }
            }
        }
    }

}
