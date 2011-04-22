dataSource {
    pooled = true
}
hibernate {
    default_schema = 'dating'
    dialect = 'org.hibernate.dialect.PostgreSQLDialect'

    cache.use_second_level_cache = true
    cache.use_query_cache = true
    cache.provider_class = 'net.sf.ehcache.hibernate.EhCacheProvider'

    net.sf.ehcache.configurationResourceName='ehcache-hibernate.xml'
}
// environment specific settings
environments {
    development {
        dataSource {
            driverClassName = "org.postgresql.Driver"
            username = "postgres"
            password = "postgres"
            dbCreate = "update" // one of 'create', 'create-drop','update'
            url = "jdbc:postgresql://localhost:5432/dating"
        }
        hibernate{
            show_sql = true
        }
    }
    test {
        dataSource {
            driverClassName = "org.hsqldb.jdbcDriver"
            username = "sa"
            password = ""
            dbCreate = "update"
            url = "jdbc:hsqldb:mem:testDb"
        }
    }
    production {
        dataSource {
            jndiName = "java:comp/env/jdbc/dating"
            dbCreate = "validate"
        }
    }
}
