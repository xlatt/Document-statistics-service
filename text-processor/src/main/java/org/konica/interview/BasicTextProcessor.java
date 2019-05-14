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
        Document document = super.createDocument(request, response);
        if (document == null) {
            logger.error(request.uri() + " failed");
            response.status(INTERNAL_ERROR);
            return "";
        }

        return super.parseAll(document);
    }

    public Object paragraphCount(Request request, Response response) throws IOException {
        Document document = super.createDocument(request, response);
        if (document == null) {
            logger.error(request.uri() + " failed");
            response.status(INTERNAL_ERROR);
            return "";
        }

        String val = document.parseParagraphCount().toString();
        return toJson(PARAGRAPH_COUNT, val);
    }

    public Object paragraphLengthMax(Request request, Response response) throws IOException {
        Document document = createDocument(request, response);
        if (document == null) {
            logger.error(request.uri() + " failed");
            response.status(INTERNAL_ERROR);
            return "";
        }

        String val =  document.parseParagraphMaxLength().toString();
        return  toJson(PARAGRAPH_LEN_MAX, val);
    }

    public Object paragraphLengthMin(Request request, Response response) throws IOException {
        Document document = createDocument(request, response);
        if (document == null) {
            logger.error(request.uri() + " failed");
            response.status(INTERNAL_ERROR);
            return "";
        }

        String val = document.parseParagraphMinLength().toString();
        return toJson(PARAGRAPH_LEN_MIN, val);
    }

    public Object paragraphLengthAvg(Request request, Response response) throws IOException {
        Document document = createDocument(request, response);
        if (document == null) {
            logger.error(request.uri() + " failed");
            response.status(INTERNAL_ERROR);
            return "";
        }

        String val = document.parseParagraphAvgLength().toString();
        return toJson(PARAGRAPH_LEN_AVG, val);
    }

    public Object wordFrequency(Request request, Response response) throws IOException {
        Document document = createDocument(request, response);
        if (document == null) {
            logger.error(request.uri() + " failed");
            response.status(INTERNAL_ERROR);
            return "";
        }

        String val = document.parseWordFrequency().toString();
        return toJson(WORD_FREQUENCY, val);
    }
}