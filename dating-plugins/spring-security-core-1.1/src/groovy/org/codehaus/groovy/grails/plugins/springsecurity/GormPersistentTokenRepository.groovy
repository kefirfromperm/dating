/* Copyright 2006-2010 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.codehaus.groovy.grails.plugins.springsecurity

import org.apache.log4j.Logger
import org.codehaus.groovy.grails.commons.ApplicationHolder as AH
import org.codehaus.groovy.grails.plugins.springsecurity.SpringSecurityUtils

import org.springframework.security.web.authentication.rememberme.JdbcTokenRepositoryImpl
import org.springframework.security.web.authentication.rememberme.PersistentRememberMeToken
import org.springframework.security.web.authentication.rememberme.PersistentTokenRepository

/**
 * GORM-based PersistentTokenRepository implementation, based on {@link JdbcTokenRepositoryImpl}.
 *
 * @author <a href='mailto:burt@burtbeckwith.com'>Burt Beckwith</a>
 */
class GormPersistentTokenRepository implements PersistentTokenRepository {

	private final Logger log = Logger.getLogger(getClass())

	/**
	 * {@inheritDoc}
	 * @see org.springframework.security.web.authentication.rememberme.PersistentTokenRepository#createNewToken(
	 * 	org.springframework.security.web.authentication.rememberme.PersistentRememberMeToken)
	 */
	void createNewToken(PersistentRememberMeToken token) {
		// join an existing transaction if one is active
		def clazz = lookupDomainClass()
		if (!clazz) return

		clazz.withTransaction { status ->
			clazz.newInstance(username: token.username, series: token.series,
					token: token.tokenValue, lastUsed: token.date).save()
		}
	}

	/**
	 * {@inheritDoc}
	 * @see org.springframework.security.web.authentication.rememberme.PersistentTokenRepository#getTokenForSeries(
	 * 	java.lang.String)
	 */
	PersistentRememberMeToken getTokenForSeries(String seriesId) {
		def persistentToken
		def clazz = lookupDomainClass()
		if (clazz) {
			persistentToken = clazz.get(seriesId)
		}
		if (!persistentToken) {
			return null
		}

		return new PersistentRememberMeToken(persistentToken.username, persistentToken.series,
				persistentToken.token, persistentToken.lastUsed)
	}

	/**
	 * {@inheritDoc}
	 * @see org.springframework.security.web.authentication.rememberme.PersistentTokenRepository#removeUserTokens(
	 * 	java.lang.String)
	 */
	void removeUserTokens(String username) {
		def clazz = lookupDomainClass()
		if (!clazz) return

		// join an existing transaction if one is active
		clazz.withTransaction { status ->
			clazz.executeUpdate(
					"DELETE FROM $clazz.name pl WHERE pl.username=:username",
					[username: username])
		}
	}

	/**
	 * {@inheritDoc}
	 * @see org.springframework.security.web.authentication.rememberme.PersistentTokenRepository#updateToken(
	 * 	java.lang.String, java.lang.String, java.util.Date)
	 */
	void updateToken(String series, String tokenValue, Date lastUsed) {
		def clazz = lookupDomainClass()
		if (!clazz) return

		// join an existing transaction if one is active
		clazz.withTransaction { status ->
			def persistentLogin = clazz.get(series)
			persistentLogin?.token = tokenValue
			persistentLogin?.lastUsed = lastUsed
		}
	}

	protected Class lookupDomainClass() {
		def conf = SpringSecurityUtils.securityConfig
		String domainClassName = conf.rememberMe.persistentToken.domainClassName ?: ''
		def clazz = AH.application.getClassForName(domainClassName)
		if (!clazz) {
			log.error "Persistent token class not found: '${domainClassName}'"
		}
		clazz
	}
}
