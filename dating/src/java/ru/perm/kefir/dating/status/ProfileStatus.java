package ru.perm.kefir.dating.status;

import java.util.Date;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * Contains data about last profile activity.
 *
 * @author Vitaliy Samolovskih aka Kefir
 */
public class ProfileStatus {
    private static final Date MIN_DATE = new Date(Long.MIN_VALUE);

    private final long profileId;

    private Date lastActivityTime;
    private Date lastChangeTime = MIN_DATE;

    // Lock
    private final ReadWriteLock lock = new ReentrantReadWriteLock();

    public ProfileStatus(long profileId) {
        this.profileId = profileId;
        lastActivityTime = new Date();
    }

    public ProfileStatus(long profileId, Date lastActivityTime) {
        this.profileId = profileId;
        this.lastActivityTime = lastActivityTime;
    }

    public long getProfileId() {
        return profileId;
    }

    public Date getLastActivityTime() {
        lock.readLock().lock();
        try {
            return lastActivityTime;
        } finally {
            lock.readLock().unlock();
        }
    }

    public void setLastActivityTime(Date lastActivityTime) {
        lock.writeLock().lock();
        try {
            this.lastActivityTime = lastActivityTime;
        } finally {
            lock.writeLock().unlock();
        }
    }

    public Date getLastChangeTime() {
        lock.readLock().lock();
        try {
            return lastChangeTime;
        } finally {
            lock.readLock().unlock();
        }
    }

    public void setLastChangeTime(Date lastChangeTime) {
        lock.writeLock().lock();
        try {
            this.lastChangeTime = lastChangeTime;
        } finally {
            lock.writeLock().unlock();
        }
    }
}
