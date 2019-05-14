package org.konica.interview;

import java.io.IOException;
import java.util.UUID;

import org.slf4j.LoggerFactory;
import spark.Request;
import spark.Response;

public class PersistentTextProcessor extends TextProcessor {
    private static DocumentStore documentStore;

    public PersistentTextProcessor(String textExtractorLocation, String documentStoreLocation) throws IOException {
        super(textExtractorLocation);
        documentStore = new DocumentStore(documentStoreLocation);
        logger = LoggerFactory.getLogger(PersistentTextProcessor.class);
    }

    private Document loadDocument(Request request) throws IOException {
        UUID uuid = UUID.fromString(request.params(":id"));
        return documentStore.get(uuid);
    }

    public Object parseAll(Request request, Response response) throws IOException {
        Document document = loadDocument(request);
        if (document == null) {
            logger.error(request.uri() + " failed");
            response.status(NOT_FOUND);
            return "";
        }

        return super.parseAll(document);
    }

    public Object paragraphCount(Request request, Response response) throws IOException {
        Document document = loadDocument(request);
        if (document == null) {
            logger.error(request.uri() + " failed");
            response.status(NOT_FOUND);
            return "";
        }

        return super.paragraphCount(document);
    }

    public Object paragraphLengthMax(Request request, Response response) throws IOException {
        Document document = loadDocument(request);
        if (document == null) {
            logger.error(request.uri() + " failed");
            response.status(NOT_FOUND);
            return "";
        }

        return super.paragraphLengthMax(document);
    }

    public Object paragraphLengthMin(Request request, Response response) throws IOException {
        Document document = loadDocument(request);
        if (document == null) {
            logger.error(request.uri() + " failed");
            response.status(NOT_FOUND);
            return "";
        }

        return super.paragraphLengthMin(document);
    }

    public Object paragraphLengthAvg(Request request, Response response) throws IOException {
        Document document = loadDocument(request);
        if (document == null) {
            logger.error(request.uri() + " failed");
            response.status(NOT_FOUND);
            return "";
        }

        return super.paragraphLengthAvg(document);
    }

    public Object wordFrequency(Request request, Response response) throws IOException {
        Document document = loadDocument(request);
        if (document == null) {
            logger.error(request.uri() + " failed");
            response.status(NOT_FOUND);
            return "";
        }

        return super.wordFrequency(document);
    }

    public Object saveDocument(Request request, Response response) throws IOException {
        Document document = createDocument(request);
        if (document == null) {
            logger.error(request.uri() + " failed");
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