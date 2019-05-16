package org.konica.interview;

import java.time.Instant;
import java.util.HashMap;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.stream.Collectors;

/** Represents caching system
 *
 * DocumentCache is able to store documents for interval of 5 seconds.
 * If document is not accessed in any way in this interval it is removed from cache.
 */
public class DocumentCache {
    private ReadWriteLock lock;
    private HashMap<UUID, Document> cache;
    private HashMap<UUID, Long> timestamps;

    /**
     * Initialize cache
     */
    public DocumentCache() {
        this.lock = new ReentrantReadWriteLock();
        this.cache = new HashMap<>();
        this.timestamps = new HashMap<>();
    }

    /**
     * Periodically check for documents which were not accessed in any way for time period longer than invalidate
     * parameter sets. Documents with timestamp older than this threshold are deleted from cache and returned to DocumentStore
     * @param invalidate sets how old documents can stay in cache
     * @return Documents to be stored in database
     * @see DocumentStore
     * @see Document
     */
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

    /**
     * Saves Document to cache and records timestamp.
     *
     * @param uuid identifies Document
     * @param document Document to be stored
     * @see UUID
     * @see Document
     */
    public void store(UUID uuid, Document document) {
        lockWrite();
            cache.put(uuid, document);
            timestamps.put(uuid, Instant.now().getEpochSecond());
        unlockWrite();
    }

    /**
     * Retrieve document from cache and update timestamp with current time.
     *
     * @param uuid identifies Document to be retrieved
     * @return Document
     * @see Document
     */
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

    /**
     * Delete Document from cache. If document is not present return false
     * otherwise true to indicate operation was successful.
     *
     * @param uuid identifies Document to be deleted
     * @return status of operation
     */
    public boolean delete(UUID uuid) {
        return cache.remove(uuid) != null;
    }

    /**
     * Lock reading lock
     */
    private void lockRead() {
        lock.readLock().lock();
    }

    /**
     * Unlock reading lock
     */
    private void unlockRead() {
        lock.readLock().unlock();
    }

    /**
     * Lock writing lock
     */
    private void lockWrite() {
        lock.writeLock().lock();
    }

    /**
     * Unlock writing lock
     */
    private void unlockWrite() {
        lock.writeLock().unlock();
    }
}
