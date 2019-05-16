package org.konica.interview;

import java.io.IOException;
import java.util.UUID;

import org.slf4j.LoggerFactory;
import spark.Request;
import spark.Response;

/** Represents text processor which can store data
 *
 * This text processor uses DocumentStore to store documents
 * for later processing.
 *
 * @see DocumentStore
 */
public class PersistentTextProcessor extends TextProcessor {
    private static DocumentStore documentStore;

    /**
     * @param textExtractorLocation URL of Tika server.
     * @param documentStoreLocation URL of database server.
     * @throws IOException
     */
    public PersistentTextProcessor(String textExtractorLocation, String documentStoreLocation) throws IOException {
        super(textExtractorLocation);
        documentStore = new DocumentStore(documentStoreLocation);
        logger = LoggerFactory.getLogger(PersistentTextProcessor.class);
    }

    /**
     * @param request contains UUID which corresponds to a Document
     * @return Document object
     * @throws IOException
     */
    private Document loadDocument(Request request) throws IOException {
        UUID uuid = UUID.fromString(request.params(":id"));
        return documentStore.get(uuid);
    }

    /**
     * Load Document either from cache or from database, parse and return statistics.
     *
     * @param request contains UUID of document
     * @param response contains message header which will be returned back to caller
     * @return String which holds response body
     * @throws IOException
     */
    public Object parseAll(Request request, Response response) throws IOException {
        Document document = loadDocument(request);
        if (document == null) {
            logger.error(request.uri() + " failed");
            response.status(NOT_FOUND);
            return "";
        }

        return super.parseAll(document);
    }

    /**
     * Load Document either from cache or from database, parse and return paragraph count.
     *
     * @param request contains UUID of document
     * @param response contains message header which will be returned back to caller
     * @return String which holds response body
     * @throws IOException
     */
    public Object paragraphCount(Request request, Response response) throws IOException {
        Document document = loadDocument(request);
        if (document == null) {
            logger.error(request.uri() + " failed");
            response.status(NOT_FOUND);
            return "";
        }

        return super.paragraphCount(document);
    }

    /**
     * Load Document either from cache or from database, parse and return longest paragraph.
     *
     * @param request contains UUID of document
     * @param response contains message header which will be returned back to caller
     * @return String which holds response body
     * @throws IOException
     */
    public Object paragraphLengthMax(Request request, Response response) throws IOException {
        Document document = loadDocument(request);
        if (document == null) {
            logger.error(request.uri() + " failed");
            response.status(NOT_FOUND);
            return "";
        }

        return super.paragraphLengthMax(document);
    }

    /**
     * Load Document either from cache or from database, parse and return shortest paragraph.
     *
     * @param request contains UUID of document
     * @param response contains message header which will be returned back to caller
     * @return String which holds response body
     * @throws IOException
     */
    public Object paragraphLengthMin(Request request, Response response) throws IOException {
        Document document = loadDocument(request);
        if (document == null) {
            logger.error(request.uri() + " failed");
            response.status(NOT_FOUND);
            return "";
        }

        return super.paragraphLengthMin(document);
    }

    /**
     * Load Document either from cache or from database, parse and return average length of paragraph.
     *
     * @param request contains UUID of document
     * @param response contains message header which will be returned back to caller
     * @return String which holds response body
     * @throws IOException
     */
    public Object paragraphLengthAvg(Request request, Response response) throws IOException {
        Document document = loadDocument(request);
        if (document == null) {
            logger.error(request.uri() + " failed");
            response.status(NOT_FOUND);
            return "";
        }

        return super.paragraphLengthAvg(document);
    }

    /**
     * Load Document either from cache or from database, parse and return word frequency in descending order.
     *
     * @param request contains UUID of document
     * @param response contains message header which will be returned back to caller
     * @return String which holds response body
     * @throws IOException
     */
    public Object wordFrequency(Request request, Response response) throws IOException {
        Document document = loadDocument(request);
        if (document == null) {
            logger.error(request.uri() + " failed");
            response.status(NOT_FOUND);
            return "";
        }

        return super.wordFrequency(document);
    }

    /**
     * Save Document to cache, return UUID associated with document which is being stored.
     *
     * @param request contains document to be stored
     * @param response contains message header which will be returned back to caller
     * @return UUID associated with document which was stored
     * @throws IOException
     */
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

    /**
     * Delete Document from store.
     *
     * @param request contains UUID of Document which should be deleted
     * @param response response for the caller, HTTP status code is set to indicate status of operation
     * @return empty body
     * @throws IOException
     */
    public Object deleteDocument(Request request, Response response) {
        UUID uuid = UUID.fromString(request.params(":id"));
        int s = documentStore.delete(uuid) ? OPERATION_ACCEPTED : NOT_FOUND;

        response.status(s);
        return "";
    }
}