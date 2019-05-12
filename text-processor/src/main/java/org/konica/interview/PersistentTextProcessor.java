package org.konica.interview;

import spark.Request;
import spark.Response;

import java.io.IOException;
import java.util.UUID;

public class PersistentTextProcessor extends TextProcessor {
    private static DocumentStore documentStore;
    private static DocumentCache documentCache;

    public PersistentTextProcessor(String textExtractorLocation, String documentStoreLocation) throws IOException {
        super(textExtractorLocation);
        documentStore = new DocumentStore(documentStoreLocation);
        documentCache = new DocumentCache();
    }

    private Document loadDocument(Request request, Response response) {
        // TODO
        //  1. load UUID from request
        //  2. check if Document is present in cache.
        //      2.1 if yes, load and return
        //  3. check if Document is in DB
        //      3.1 if yes, load and transform from JSON to Document and return
        return new Document("EMPTY PICO");
    }

    public Object paragraphCount(Request request, Response response) throws IOException {
        Document document = loadDocument(request, response);
        return document.getParagraphCount().toString() + "\n";
    }

    public Object paragraphLengthMax(Request request, Response response) throws IOException {
        Document document = loadDocument(request, response);
        return document.getParagraphMaxLength().toString() + "\n";
    }

    public Object paragraphLengthMin(Request request, Response response) throws IOException {
        Document document = loadDocument(request, response);
        return document.getParagraphMinLength().toString() + "\n";
    }

    public Object paragraphLengthAvg(Request request, Response response) throws IOException {
        Document document = loadDocument(request, response);
        return document.getParagraphAvgLength().toString() + "\n";
    }

    public Object wordFrequency(Request request, Response response) throws IOException {
        Document document = loadDocument(request, response);
        return document.getWordFrequency().toString() + "\n";
    }

    public Object saveDocument(Request request, Response response) throws IOException {
        Document document = createDocument(request, response);
        // TODO
        //  1. insert Document into cache
        //      1.1 after two seconds of class being not used serialize it and save to DB.
        //      1.2 delete object from cache
        return "Not yet implemented\n";
    }

    public Object deleteDocument(Request request, Response response) throws IOException {
        // TODO
        //  1. check if Document in cache.
        //      1.2 if yes, delete
        //  2. check if Document in DB
        //      2.1 if yes, delete
        return "Not yet implemented\n";
    }
}