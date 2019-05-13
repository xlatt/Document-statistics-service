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
        return documentStore.get(uuid);
    }

    public Object paragraphCount(Request request, Response response) throws IOException {
        Document document = loadDocument(request, response);
        String val = document.parseParagraphCount().toString();
        return toJson(PARAGRAPH_COUNT, val);
    }

    public Object paragraphLengthMax(Request request, Response response) throws IOException {
        Document document = loadDocument(request, response);
        String val = document.parseParagraphMaxLength().toString();
        return toJson(PARAGRAPH_LEN_MAX, val);
    }

    public Object paragraphLengthMin(Request request, Response response) throws IOException {
        Document document = loadDocument(request, response);
        String val = document.parseParagraphMinLength().toString();
        return toJson(PARAGRAPH_LEN_MIN, val);
    }

    public Object paragraphLengthAvg(Request request, Response response) throws IOException {
        Document document = loadDocument(request, response);
        String val = document.parseParagraphAvgLength().toString();
        return toJson(PARAGRAPH_LEN_AVG, val);
    }

    public Object wordFrequency(Request request, Response response) throws IOException {
        Document document = loadDocument(request, response);
        String val = document.parseWordFrequency().toString();
        return toJson(WORD_FREQENCY, val);
    }

    public Object saveDocument(Request request, Response response) throws IOException {
        Document document = createDocument(request, response);
        String val = documentStore.store(document).toString();
        return toJson(DOCUMENT_UUID, val);
    }

    public Object deleteDocument(Request request, Response response) {
        UUID uuid = UUID.fromString(request.params(":id"));
        documentStore.delete(uuid);
        response.status(202);
        return "";
    }
}