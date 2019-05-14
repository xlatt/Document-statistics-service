package org.konica.interview;

import java.io.IOException;
import java.util.UUID;

import spark.Request;
import spark.Response;

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

    public Object parseAll(Request request, Response response) throws IOException {
        Document document = loadDocument(request, response);

        return super.parseAll(document);
    }

    public Object paragraphCount(Request request, Response response) throws IOException {
        Document document = loadDocument(request, response);
        if (document == null) {
            response.status(NOT_FOUND);
            return "";
        }

        String val = document.parseParagraphCount().toString();
        return toJson(PARAGRAPH_COUNT, val);
    }

    public Object paragraphLengthMax(Request request, Response response) throws IOException {
        Document document = loadDocument(request, response);
        if (document == null) {
            response.status(NOT_FOUND);
            return "";
        }

        String val = document.parseParagraphMaxLength().toString();
        return toJson(PARAGRAPH_LEN_MAX, val);
    }

    public Object paragraphLengthMin(Request request, Response response) throws IOException {
        Document document = loadDocument(request, response);
        if (document == null) {
            response.status(NOT_FOUND);
            return "";
        }

        String val = document.parseParagraphMinLength().toString();
        return toJson(PARAGRAPH_LEN_MIN, val);
    }

    public Object paragraphLengthAvg(Request request, Response response) throws IOException {
        Document document = loadDocument(request, response);
        if (document == null) {
            response.status(NOT_FOUND);
            return "";
        }

        String val = document.parseParagraphAvgLength().toString();
        return toJson(PARAGRAPH_LEN_AVG, val);
    }

    public Object wordFrequency(Request request, Response response) throws IOException {
        Document document = loadDocument(request, response);
        if (document == null) {
            response.status(NOT_FOUND);
            return "";
        }

        String val = document.parseWordFrequency().toString();
        return toJson(WORD_FREQUENCY, val);
    }

    public Object saveDocument(Request request, Response response) throws IOException {
        Document document = createDocument(request, response);
        if (document == null) {
            response.status(NOT_FOUND);
            return "";
        }

        String val = documentStore.store(document).toString();
        return toJson(DOCUMENT_UUID, val);
    }

    public Object deleteDocument(Request request, Response response) {
        UUID uuid = UUID.fromString(request.params(":id"));
        int s = documentStore.delete(uuid) ? OPERATION_ACCEPTED : NOT_FOUND;

        response.status(s);
        return "";
    }
}