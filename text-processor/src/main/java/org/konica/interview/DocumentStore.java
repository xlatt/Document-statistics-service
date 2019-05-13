package org.konica.interview;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.UpdateOptions;
import org.bson.Document;

import java.io.IOException;
import java.util.HashMap;
import java.util.UUID;


public class DocumentStore {
    private DocumentCache cache;
    private MongoCollection<org.bson.Document> db;
    private ObjectMapper objectMapper;

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
        String doc = objectMapper.writeValueAsString(document);
        db.updateOne(new Document("id", uuid.toString()), new Document("$set", new Document("content", doc)), new UpdateOptions().upsert(true));
    }


    public void delete(UUID uuid) {
        cache.delete(uuid);
        deleteFromDb(uuid);
    }

    private void deleteFromDb(UUID uuid) {
        db.deleteOne(new Document("id", uuid.toString()));
    }

    public org.konica.interview.Document get(UUID uuid) throws  IOException {
        org.konica.interview.Document document = cache.get(uuid);

        if (document == null) {
            org.bson.Document d = this.db.find(new org.bson.Document("id", uuid.toString())).first();


            document = objectMapper.readValue(d.get("content").toString(), org.konica.interview.Document.class);
            cache.store(uuid, document);
        }
        return document;
    }
}
