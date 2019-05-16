package org.konica.interview;

import java.io.IOException;

import org.slf4j.LoggerFactory;
import spark.Request;
import spark.Response;


/** Represent basic text processor
 *
 *  Create Document object and call respective methods
 *  to extract statistics about text content of Document.
 *  Extraction is implemented in super class. This class
 *  deals only with how the Document is created.
 */
public class BasicTextProcessor extends TextProcessor {
    /**
     * Constructor for BasicTextProcessor. Initialize also TextProcessor.
     *
     * @param textExtractorLocation URL of Tika server.
     * @throws IOException
     */
    public BasicTextProcessor(String textExtractorLocation) throws IOException {
        super(textExtractorLocation);
        logger = LoggerFactory.getLogger(BasicTextProcessor.class);
    }

    /**
     * Create Document out of bytes which are representing document which was uploaded
     * by caller and parse and return statistics.
     *
     * @param request contains document which was uploaded
     * @param response contains message header which will be returned back to caller
     * @return String which holds response body
     * @throws IOException
     */
    public Object parseAll(Request request, Response response) throws IOException {
        Document document = super.createDocument(request);
        if (document == null) {
            logger.error(request.uri() + " failed");
            response.status(INTERNAL_ERROR);
            return "";
        }

        return super.parseAll(document);
    }

    /**
     * Create Document out of bytes which are representing document which was uploaded
     * by caller and parse and return statistics.
     *
     * @param request contains document which was uploaded
     * @param response contains message header which will be returned back to caller
     * @return String which holds response body
     * @throws IOException
     */
    public Object paragraphCount(Request request, Response response) throws IOException {
        Document document = super.createDocument(request);
        if (document == null) {
            logger.error(request.uri() + " failed");
            response.status(INTERNAL_ERROR);
            return "";
        }

        return super.paragraphCount(document);
    }

    /**
     * Create Document out of bytes which are representing document which was uploaded
     * by caller and parse and return statistics.
     *
     * @param request contains document which was uploaded
     * @param response contains message header which will be returned back to caller
     * @return String which holds response body
     * @throws IOException
     */
    public Object paragraphLengthMax(Request request, Response response) throws IOException {
        Document document = createDocument(request);
        if (document == null) {
            logger.error(request.uri() + " failed");
            response.status(INTERNAL_ERROR);
            return "";
        }

        return super.paragraphLengthMax(document);
    }

    /**
     * Create Document out of bytes which are representing document which was uploaded
     * by caller and parse and return statistics.
     *
     * @param request contains document which was uploaded
     * @param response contains message header which will be returned back to caller
     * @return String which holds response body
     * @throws IOException
     */
    public Object paragraphLengthMin(Request request, Response response) throws IOException {
        Document document = createDocument(request);
        if (document == null) {
            logger.error(request.uri() + " failed");
            response.status(INTERNAL_ERROR);
            return "";
        }

        return super.paragraphLengthMin(document);
    }

    /**
     * Create Document out of bytes which are representing document which was uploaded
     * by caller and parse and return statistics.
     *
     * @param request contains document which was uploaded
     * @param response contains message header which will be returned back to caller
     * @return String which holds response body
     * @throws IOException
     */
    public Object paragraphLengthAvg(Request request, Response response) throws IOException {
        Document document = createDocument(request);
        if (document == null) {
            logger.error(request.uri() + " failed");
            response.status(INTERNAL_ERROR);
            return "";
        }

        return super.paragraphLengthAvg(document);
    }

    /**
     * Create Document out of bytes which are representing document which was uploaded
     * by caller and parse and return word frequency.
     *
     * @param request contains document which was uploaded
     * @param response contains message header which will be returned back to caller
     * @return String which holds response body
     * @throws IOException
     */
    public Object wordFrequency(Request request, Response response) throws IOException {
        Document document = createDocument(request);
        if (document == null) {
            logger.error(request.uri() + " failed");
            response.status(INTERNAL_ERROR);
            return "";
        }

        return super.wordFrequency(document);
    }
}