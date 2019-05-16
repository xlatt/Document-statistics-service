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

public class DocumentStore {
    private DocumentCache cache;
    private MongoCollection<org.bson.Document> db;
    private ObjectMapper objectMapper;
    private FilterProvider all;

    public class CacheInvalidator implements Runnable {
        private int ttl;

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

    public UUID store(org.konica.interview.Document document) {
        UUID uuid = UUID.randomUUID();
        cache.store(uuid, document);
        return uuid;
    }

    private void storeDb(UUID uuid, org.konica.interview.Document document) throws IOException {
        String doc = objectMapper.writer(all).writeValueAsString(document);
        db.updateOne(new Document("id", uuid.toString()), new Document("$set", new Document("content", doc)), new UpdateOptions().upsert(true));
    }

    public boolean delete(UUID uuid) {
        boolean cd = cache.delete(uuid);
        boolean dd = deleteFromDb(uuid);

        return cd || dd;
    }

    private boolean deleteFromDb(UUID uuid) {
        DeleteResult res = db.deleteOne(new Document("id", uuid.toString()));
        return res.wasAcknowledged();
    }

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
