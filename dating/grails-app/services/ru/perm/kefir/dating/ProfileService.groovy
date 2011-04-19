package ru.perm.kefir.dating

import grails.plugins.springsecurity.SpringSecurityService
import net.sf.ehcache.Ehcache
import net.sf.ehcache.Element
import org.codehaus.groovy.grails.plugins.springsecurity.SpringSecurityUtils
import org.hibernate.Criteria
import org.hibernate.Session
import org.hibernate.criterion.Order
import org.hibernate.criterion.Projections
import org.hibernate.criterion.Restrictions
import org.hibernate.type.StringType
import org.hibernate.type.Type

class ProfileService {
    private static final Type HIBERNATE_STRING_TYPE = new StringType();

    static transactional = true;

    AccountService accountService;
    SpringSecurityService springSecurityService;

    // Caches
    Ehcache accountProfileCache;
    Ehcache aliasProfileCache;

    boolean access(Profile profile) {
        boolean flag = SpringSecurityUtils.ifAllGranted('ROLE_ADMIN');

        if (!flag && SpringSecurityUtils.ifAllGranted('ROLE_USER') && springSecurityService.isLoggedIn()) {
            flag = (profile.account != null && springSecurityService.principal != null &&
                    profile.account.id == springSecurityService.principal.id);
        }

        return flag;
    }

    /**
     * Find profile of current user
     */
    Profile current() {
        Account account = accountService.current();
        if (account) {
            return findByAccount(account);
        } else {
            return null;
        }
    }

    /**
     * Find profile by account with caching
     */
    public Profile findByAccount(Account account) {
        Profile profile = null;
        if (account != null) {
            Long id = (Long) accountProfileCache.get(account.id)?.value;
            if (id) {
                profile = Profile.get(id);
                if (profile==null || profile.account != account) {
                    accountProfileCache.remove(account.id);
                    profile = null;
                }
            }

            if (profile == null) {
                profile = Profile.findByAccount(account);
                if (profile) {
                    accountProfileCache.put(new Element(account.id, profile.id));
                }
            }
        }
        return profile;
    }

    /**
     * Find profile by alias. Cached.
     */
    public Profile findByAlias(String alias) {
        Profile profile=null;

        Long id = (Long) aliasProfileCache.get(alias)?.value;
        if (id) {
            profile = Profile.get(id);
            if(profile==null || profile.alias != alias){
                aliasProfileCache.remove(alias);
                profile = null;
            }
        }

        if(profile==null){
            profile = Profile.findByAlias(alias);
            if (profile) {
                aliasProfileCache.put(new Element(alias, profile.id));
            }
        }

        return profile;
    }

    /**
     * Find all profiles by search pattern
     */
    List<Profile> fullTextSearch(String query, Map paginate) {
        return Profile.withSession {Session session ->
            Criteria c = createFullTextSearchCriteria(session, query);

            // Sort order
            c.addOrder(Order.desc('createDate'));

            // Paginate
            if (paginate.max) {
                c.setMaxResults(paginate.max as int);
            }

            if (paginate.offset) {
                c.setFirstResult(paginate.offset as int);
            }

            return c.list();
        }
    }

    /**
     * Return count of search results
     */
    int fullTextSearchCount(String query) {
        return Profile.withSession {Session session ->
            Criteria c = createFullTextSearchCriteria(session, query);
            c.setProjection(Projections.rowCount());
            return ((Number) c.uniqueResult()).intValue();
        }
    }

    private Criteria createFullTextSearchCriteria(Session session, String query) {
        Criteria c = session.createCriteria(Profile.class).add(
                Restrictions.sqlRestriction(
                        "to_tsvector(\"name\")||to_tsvector(about) @@ plainto_tsquery(?)",
                        query, HIBERNATE_STRING_TYPE
                )
        )
        return c;
    }
}
