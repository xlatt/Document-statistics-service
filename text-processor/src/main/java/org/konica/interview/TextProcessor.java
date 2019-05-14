package org.konica.interview;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import com.fasterxml.jackson.databind.ser.FilterProvider;
import com.fasterxml.jackson.databind.ser.impl.SimpleBeanPropertyFilter;
import com.fasterxml.jackson.databind.ser.impl.SimpleFilterProvider;
import spark.Request;
import spark.Response;

public abstract class TextProcessor {
    protected TextExtractor textExtractor;
    protected ObjectMapper mapper;

    protected static final int OPERATION_ACCEPTED   = 202;
    protected static final int NOT_FOUND            = 404;
    protected static final int INTERNAL_ERROR       = 500;

    protected static final String TEXT_PDF   = "text/pdf";
    protected static final String TEXT_PLAIN = "text/plain";
    protected static final String TEXT_WORD  = "text/word";

    protected static final String PARAGRAPH_COUNT = "ParagraphCount";
    protected static final String PARAGRAPH_LEN_MAX = "ParagraphLengthMax";
    protected static final String PARAGRAPH_LEN_MIN = "ParagraphLengthMin";
    protected static final String PARAGRAPH_LEN_AVG = "ParagraphLengthAvg";
    protected static final String WORD_FREQUENCY = "WordFrequency";
    protected static final String DOCUMENT_UUID = "Uuid";

    protected SimpleBeanPropertyFilter propertyFilter;
    protected FilterProvider excp;

    protected Logger logger;

    public TextProcessor(String textExtractorLocation) throws IOException {
        textExtractor = new TextExtractor(textExtractorLocation);
        mapper = new ObjectMapper();

        // workaround to ensure Document class is loaded so that JSON fileter
        // can be created
        new Document();

        propertyFilter = SimpleBeanPropertyFilter.serializeAllExcept("content", "paragraphs");
        excp = new SimpleFilterProvider().addFilter("Document", propertyFilter);

        logger = LoggerFactory.getLogger(TextProcessor.class);
    }

    protected Document extractByContent(Request request, String type) throws IOException {
        Document document;

        if (type.equals(TEXT_PDF) || type.equals(TEXT_WORD)) {
            String content = textExtractor.bytesToText(request.bodyAsBytes());
            document = new Document(content);
        } else if (type.equals(TEXT_PLAIN)){
            String content = request.body();
            document = new Document(content);
        } else {
            logger.warn("[WARN] Unrecognized type of document. Trying extraction by Tika.");
            String content = textExtractor.bytesToText(request.bodyAsBytes());
            document = new Document(content);
        }

        return document;
    }

    protected Document createDocument(Request request) throws IOException {
        String contentType = request.headers("Content-Type");
        return extractByContent(request, contentType);
    }

    public String toJson(String name, String value) {
        ObjectNode objectNode1 = mapper.createObjectNode();
        objectNode1.put(name, value);
        return objectNode1.toString();
    }

    public Object parseAll(Document document) throws IOException {
        document.parseParagraphCount();
        document.parseParagraphMaxLength();
        document.parseParagraphMinLength();
        document.parseParagraphAvgLength();
        document.parseWordFrequency();

        return mapper.writer(excp).writeValueAsString(document);
    }

    public Object paragraphCount(Document document){
        String val = document.parseParagraphCount().toString();
        return toJson(PARAGRAPH_COUNT, val);
    }

    public Object paragraphLengthMax(Document document) {
        String val =  document.parseParagraphMaxLength().toString();
        return  toJson(PARAGRAPH_LEN_MAX, val);
    }

    public Object paragraphLengthMin(Document document) {
        String val = document.parseParagraphMinLength().toString();
        return toJson(PARAGRAPH_LEN_MIN, val);
    }

    public Object paragraphLengthAvg(Document document) {
        String val = document.parseParagraphAvgLength().toString();
        return toJson(PARAGRAPH_LEN_AVG, val);
    }

    public Object wordFrequency(Document document) {
        String val = document.parseWordFrequency().toString();
        return toJson(WORD_FREQUENCY, val);
    }
}