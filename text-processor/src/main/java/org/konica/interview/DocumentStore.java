package org.konica.interview;

import com.mongodb.*;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;

import java.util.HashMap;
import java.util.UUID;;
import org.bson.Document;

public class DocumentStore {
    private DocumentCache cache;

    public class CacheInvalidator implements Runnable {
        private int ttl;

        public CacheInvalidator(int ttl) {
            this.ttl = ttl;
        }

        @Override
        public void run() {
            while (true) {
                try {
                    Thread.sleep(ttl);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                HashMap<UUID, org.konica.interview.Document> dead = cache.invalidate(ttl);
                dead.forEach(DocumentStore.this::storeDb);
            }
        }
    }

    public DocumentStore(String location) {
        cache = new DocumentCache();
        Thread cacheInvalidator = new Thread(new CacheInvalidator(5));
        cacheInvalidator.start();

        Block<Document> printBlock = new Block<org.bson.Document>() {
            @Override
            public void apply(final org.bson.Document document) {
                System.out.println(document.toJson());
            }
        };

        MongoClient mongoClient = MongoClients.create(location);
        MongoDatabase database = mongoClient.getDatabase("documents");
        MongoCollection<org.bson.Document> coll = database.getCollection("content");
        coll.find().forEach(printBlock);
    }

    public void update(UUID uuid, org.konica.interview.Document document) {
        // check if in cache, if yes update else
        // update in DB
        return;
    }

    public UUID store(org.konica.interview.Document document) {
        UUID uuid = UUID.randomUUID();
        cache.store(uuid, document);
        return uuid;
    }

    private void storeDb(UUID uuid, org.konica.interview.Document document) {
        System.out.println("Storting to db " + uuid.toString());
        // store in db
    }

    public org.konica.interview.Document get(UUID uuid) {
        org.konica.interview.Document document = cache.get(uuid);

        // get from DB if null
        return document;
    }
}
