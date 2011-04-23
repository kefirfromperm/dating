package com.greetim

import org.apache.log4j.LogManager
import org.apache.log4j.Logger

class LogFilters {
    private static final Logger log = LogManager.getLogger(LogFilters);

    def filters = {
        all(controller:'*', action:'*') {
            before = {
                log.debug("begin request: ${request.pathInfo}");
            }
            after = {
                log.debug("complete request: ${request.pathInfo}");
            }
            afterView = {
                log.debug("after view: ${request.pathInfo}");
            }
        }
    }
}
