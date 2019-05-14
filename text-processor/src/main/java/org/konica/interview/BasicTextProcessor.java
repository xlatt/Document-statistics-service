package org.konica.interview;

import java.io.IOException;

import org.slf4j.LoggerFactory;
import spark.Request;
import spark.Response;

public class BasicTextProcessor extends TextProcessor {
    public BasicTextProcessor(String textExtractorLocation) throws IOException {
        super(textExtractorLocation);
        logger = LoggerFactory.getLogger(BasicTextProcessor.class);
    }

    public Object parseAll(Request request, Response response) throws IOException {
        Document document = super.createDocument(request);
        if (document == null) {
            logger.error(request.uri() + " failed");
            response.status(INTERNAL_ERROR);
            return "";
        }

        return super.parseAll(document);
    }

    public Object paragraphCount(Request request, Response response) throws IOException {
        Document document = super.createDocument(request);
        if (document == null) {
            logger.error(request.uri() + " failed");
            response.status(INTERNAL_ERROR);
            return "";
        }

        return super.paragraphCount(document);
    }

    public Object paragraphLengthMax(Request request, Response response) throws IOException {
        Document document = createDocument(request);
        if (document == null) {
            logger.error(request.uri() + " failed");
            response.status(INTERNAL_ERROR);
            return "";
        }

        return super.paragraphLengthMax(document);
    }

    public Object paragraphLengthMin(Request request, Response response) throws IOException {
        Document document = createDocument(request);
        if (document == null) {
            logger.error(request.uri() + " failed");
            response.status(INTERNAL_ERROR);
            return "";
        }

        return super.paragraphLengthMin(document);
    }

    public Object paragraphLengthAvg(Request request, Response response) throws IOException {
        Document document = createDocument(request);
        if (document == null) {
            logger.error(request.uri() + " failed");
            response.status(INTERNAL_ERROR);
            return "";
        }

        return super.paragraphLengthAvg(document);
    }

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