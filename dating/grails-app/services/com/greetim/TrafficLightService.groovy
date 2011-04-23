package com.greetim

import net.sf.ehcache.Ehcache
import net.sf.ehcache.Element
import org.hibernate.Session
import org.hibernate.SessionFactory

/**
 * From rating between profiles based on message activity
 */
class TrafficLightService {
    SessionFactory sessionFactory;

    Ehcache rankCache;

    /**
     * Get light between profiles
     */
    Light light(long fromId, long toId) {
        RankKey key = new RankKey(fromId, toId);
        Light light = (Light) rankCache.get(key)?.value;
        if (light) {
            return light;
        } else {
            Session session = sessionFactory.currentSession;
            Number res = (Number) session.createSQLQuery(
                    'select rank from dating.rank where owner_id=? and target_id=?;'
            ).setLong(0, toId).setLong(1, fromId).uniqueResult();

            if (res == null) {
                res = (Number) session.createSQLQuery(
                        'select sum(lm.val*r.rank) from dating.rank as r ' +
                                'join dating.like_matrix as lm on lm.min_id=r.owner_id or lm.max_id=r.owner_id ' +
                                'where target_id=? and (lm.min_id=? or lm.max_id=?) and lm.val>0;'
                ).setLong(0, fromId).setLong(1, toId).setLong(2, toId).uniqueResult();
            }

            light = Light.UNDEFINED;
            if (res != null) {
                if (res > 0) {
                    light = Light.GREEN;
                } else if (res < 0) {
                    light = Light.RED;
                } else {
                    light = Light.YELLOW;
                }
            }

            rankCache.put(new Element(key, light));
        }

        return light;
    }

    /**
     * Get random profile, better for user
     */
    long random(long fromId) {
        Session session = sessionFactory.currentSession;

        List<Number> ids = session.createSQLQuery(
                'select id from dating.profile where id<>? order by random() limit 16;'
        ).setLong(0, fromId).list();

        long foundId = -1;
        Light foundL1 = Light.UNDEFINED;
        Light foundL2 = Light.UNDEFINED;
        for (long id: ids*.longValue()) {
            Light l1 = light(fromId, id);
            Light l2 = light(id, fromId);
            if (l1 >= foundL1 && l2 >= foundL2) {
                foundL1 = l1;
                foundL2 = l2;
                foundId = id;
            }

            if (foundL1 == Light.GREEN && foundL2 == Light.GREEN) {
                break;
            }
        }

        return foundId;
    }
}

enum Light {
    UNDEFINED, RED, YELLOW, GREEN
}

class RankKey implements Serializable, Comparable<RankKey> {
    final long from;
    final long to;

    RankKey(long from, long to) {
        this.from = from
        this.to = to
    }

    @Override
    int compareTo(RankKey that) {
        return this.from != that.from ? this.from - that.from : this.to - that.to;
    }

    @Override
    boolean equals(o) {
        if (this.is(o)) return true;
        if (!(o instanceof RankKey)) return false;

        RankKey rankKey = (RankKey) o;

        if (from != rankKey.from) return false;
        if (to != rankKey.to) return false;

        return true;
    }

    @Override
    int hashCode() {
        int result;
        result = (int) (from ^ (from >>> 32));
        result = 31 * result + (int) (to ^ (to >>> 32));
        return result;
    }
}
