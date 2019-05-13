package org.konica.interview;

import spark.Request;
import spark.Response;

import java.io.IOException;
import java.util.UUID;

public class PersistentTextProcessor extends TextProcessor {
    private static DocumentStore documentStore;

    public PersistentTextProcessor(String textExtractorLocation, String documentStoreLocation) throws IOException {
        super(textExtractorLocation);
        documentStore = new DocumentStore(documentStoreLocation);
    }

    private Document loadDocument(Request request, Response response) throws IOException {
        UUID uuid = UUID.fromString(request.params(":id"));
        System.out.println("Going to load document with UUID" + uuid.toString());
        return documentStore.get(uuid);
    }

    public Object paragraphCount(Request request, Response response) throws IOException {
        Document document = loadDocument(request, response);
        return document.parseParagraphCount().toString() + "\n";
    }

    public Object paragraphLengthMax(Request request, Response response) throws IOException {
        Document document = loadDocument(request, response);
        return document.parseParagraphMaxLength().toString() + "\n";
    }

    public Object paragraphLengthMin(Request request, Response response) throws IOException {
        Document document = loadDocument(request, response);
        return document.parseParagraphMinLength().toString() + "\n";
    }

    public Object paragraphLengthAvg(Request request, Response response) throws IOException {
        Document document = loadDocument(request, response);
        return document.parseParagraphAvgLength().toString() + "\n";
    }

    public Object wordFrequency(Request request, Response response) throws IOException {
        Document document = loadDocument(request, response);
        return document.parseWordFrequency().toString() + "\n";
    }

    public Object saveDocument(Request request, Response response) throws IOException {
        Document document = createDocument(request, response);
        return documentStore.store(document).toString();
    }

    public Object deleteDocument(Request request, Response response) throws IOException {
        UUID uuid = UUID.fromString(request.params(":id"));
        documentStore.delete(uuid);
        return "Done\n";
    }
}