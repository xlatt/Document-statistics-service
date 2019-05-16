package org.konica.interview;

import com.fasterxml.jackson.databind.ObjectMapper;

import com.fasterxml.jackson.databind.ser.FilterProvider;
import com.fasterxml.jackson.databind.ser.impl.SimpleBeanPropertyFilter;
import com.fasterxml.jackson.databind.ser.impl.SimpleFilterProvider;
import com.mongodb.client.model.UpdateOptions;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;

import com.mongodb.client.result.DeleteResult;
import org.bson.Document;

import java.io.IOException;
import java.util.HashMap;
import java.util.UUID;

/** Represents storage for documents
 *
 * This storage is backed by cache and DB. When document is stored, it is stored in
 * cache and after some time (ttl attribute) it is removed from cache and stored in
 * database from where it can be restored later using respective UUID.
 */
public class DocumentStore {
    private DocumentCache cache;
    private MongoCollection<org.bson.Document> db;
    private ObjectMapper objectMapper;
    private FilterProvider all;

    /**
     * Periodically check for cache entries which are older than TTL value.
     * Those which are marked as dead are stored in database.
     */
    public class CacheInvalidator implements Runnable {
        private int ttl;

        /**
         * @param ttl Time To Live for cache entry
         */
        public CacheInvalidator(int ttl) {
            this.ttl = ttl;
        }

        private void safeStoreToDb(final UUID uuid, final org.konica.interview.Document document) {
            try {
                DocumentStore.this.storeDb(uuid, document);
            } catch (Exception ex) {
                throw new RuntimeException(ex);
            }
        }

        /**
         * Periodically check for dead documents. If found some, store them in database.
         */
        @Override
        public void run() {
            while (true) {
                try {
                    Thread.sleep(ttl * 1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                HashMap<UUID, org.konica.interview.Document> dead = cache.invalidate(ttl);
                if (dead == null) {
                    continue;
                }
                dead.forEach(this::safeStoreToDb);
            }
        }
    }

    /**
     * Constructor for DocumentStore
     *
     * Init cache and connect to a database.
     * @param location URL of database
     */
    public DocumentStore(String location) {
        SimpleBeanPropertyFilter propertyFilter = SimpleBeanPropertyFilter.serializeAllExcept("");
        all = new SimpleFilterProvider().addFilter("Document", propertyFilter);
        objectMapper = new ObjectMapper();

        cache = new DocumentCache();

        Thread cacheInvalidator = new Thread(new CacheInvalidator(5));
        cacheInvalidator.start();

        MongoClient mongoClient = MongoClients.create(location);
        MongoDatabase db = mongoClient.getDatabase("text-processor");
        this.db = db.getCollection("documents");
    }

    /**
     * Generate UUID for Document and store that Document
     * @param document Document to be stored
     * @return UUID associated with particular Document
     */
    public UUID store(org.konica.interview.Document document) {
        UUID uuid = UUID.randomUUID();
        cache.store(uuid, document);
        return uuid;
    }

    /**
     * Store Document and UUID associated with that Document to database
     * @param uuid of Document
     * @param document Document to be stored
     * @throws IOException
     */
    private void storeDb(UUID uuid, org.konica.interview.Document document) throws IOException {
        String doc = objectMapper.writer(all).writeValueAsString(document);
        db.updateOne(new Document("id", uuid.toString()), new Document("$set", new Document("content", doc)), new UpdateOptions().upsert(true));
    }

    /**
     * Delete Document from cache and from database
     *
     * @param uuid uuid associated with Document which should be deleted
     * @return result of operation
     */
    public boolean delete(UUID uuid) {
        boolean cd = cache.delete(uuid);
        boolean dd = deleteFromDb(uuid);

        return cd || dd;
    }

    /**
     * Delete Document from database
     *
     * @param uuid uuid associated with Document which should be deleted
     * @return result of operation
     */
    private boolean deleteFromDb(UUID uuid) {
        DeleteResult res = db.deleteOne(new Document("id", uuid.toString()));
        return res.wasAcknowledged();
    }

    /**
     * Retrieve Document either from cache or database
     * @param uuid uuid associated with Document which should be returned
     * @return Document
     * @throws IOException
     */
    public org.konica.interview.Document get(UUID uuid) throws  IOException {
        org.konica.interview.Document document = cache.get(uuid);

        if (document == null) {
            org.bson.Document d = this.db.find(new org.bson.Document("id", uuid.toString())).first();

            if (d == null) return null;

            document = objectMapper.readValue(d.get("content").toString(), org.konica.interview.Document.class);
            cache.store(uuid, document);
        }
        return document;
    }
}
