package org.konica.interview;

import java.time.Instant;
import java.util.HashMap;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.stream.Collectors;

public class DocumentCache {
    private ReadWriteLock lock;
    private HashMap<UUID, Document> cache;
    private HashMap<UUID, Long> timestamps;

    public DocumentCache() {
        this.lock = new ReentrantReadWriteLock();
        this.cache = new HashMap<>();
        this.timestamps = new HashMap<>();
    }

    public HashMap<UUID, org.konica.interview.Document> invalidate(Integer invalidate) {
        Long now = Instant.now().getEpochSecond();

        lockRead();
            if (timestamps.isEmpty()) {
                unlockRead();
                return null;
            }

            Set<UUID> deadOnes = timestamps
                    .entrySet()
                    .stream()
                    .filter(e -> (now - e.getValue()) > invalidate)
                    .map(e -> e.getKey())
                    .collect(Collectors.toSet());

            if (deadOnes.isEmpty()) {
                unlockRead();
                return null;
            }

            HashMap<UUID, Document> store = new HashMap<>();
            for (UUID uuid : deadOnes) {
                store.put(uuid, cache.get(uuid));
            }
        unlockRead();

        lockWrite();
            cache.values().removeAll(deadOnes);
            timestamps.keySet().removeAll(deadOnes);
        unlockWrite();

        return store;
    }

    public void store(UUID uuid, Document document) {
        lockWrite();
            cache.put(uuid, document);
            timestamps.put(uuid, Instant.now().getEpochSecond());
        unlockWrite();
    }

    public Document get(UUID uuid) {
        Long now = Instant.now().getEpochSecond();

        lockWrite();
            Long t = timestamps.replace(uuid, now);
            if (t == null) {
                unlockWrite();
                return null;
            }
        unlockWrite();

        lockRead();
            Document document = cache.get(uuid);
        unlockRead();
        return document;
    }

    public boolean delete(UUID uuid) {
        return cache.remove(uuid) != null;
    }

    private void lockRead() {
        lock.readLock().lock();
    }

    private void unlockRead() {
        lock.readLock().unlock();
    }

    private void lockWrite() {
        lock.writeLock().lock();
    }

    private void unlockWrite() {
        lock.writeLock().unlock();
    }
}
