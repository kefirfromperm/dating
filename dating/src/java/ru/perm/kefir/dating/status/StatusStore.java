package ru.perm.kefir.dating.status;

import ru.perm.kefir.dating.Profile;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ConcurrentMap;

/**
 * @author Vitaliy Samolovskih aka Kefir
 */
public class StatusStore {
    private final ConcurrentMap<Long, ProfileStatus> statuses = new ConcurrentHashMap<Long, ProfileStatus>();
    private final Queue<ProfileStatus> clearQueue = new ConcurrentLinkedQueue<ProfileStatus>();

    public ProfileStatus findProfileStatus(Profile profile){
        return null;
    }
}
