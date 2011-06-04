grails.project.class.dir = 'target/classes'
grails.project.test.class.dir = 'target/test-classes'
grails.project.test.reports.dir = 'target/test-reports'
grails.project.docs.output.dir = 'docs' // for backwards-compatibility, the docs are checked into gh-pages branch

grails.project.dependency.resolution = {

	inherits 'global'

	log 'warn'

	repositories {
		grailsPlugins()
		grailsHome()
		grailsCentral()

		ebr() // SpringSource  http://www.springsource.com/repository
	}

	dependencies {
		runtime('org.springframework.security:org.springframework.security.core:3.0.4.RELEASE') {
			transitive = false
		}

		runtime('org.springframework.security:org.springframework.security.web:3.0.4.RELEASE') {
			transitive = false
		}
	}
}

coverage {
	enabledByDefault = true
	sourceInclusions = ['grails-app/conf']
	exclusionListOverride = [
		'*GrailsPlugin*',
		'DataSource*',
		'*Config*',
		'test/**'
	]
}
