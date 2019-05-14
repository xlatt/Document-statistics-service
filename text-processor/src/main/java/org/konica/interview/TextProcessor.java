package org.konica.interview;

import java.io.IOException;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import spark.Request;
import spark.Response;

public abstract class TextProcessor {
    protected TextExtractor textExtractor;
    protected ObjectMapper mapper;

    protected static final String TEXT_PDF   = "text/pdf";
    protected static final String TEXT_PLAIN = "text/plain";
    protected static final String TEXT_WORD  = "text/word";

    protected static final String PARAGRAPH_COUNT = "ParagraphCount";
    protected static final String PARAGRAPH_LEN_MAX = "ParagraphLengthMax";
    protected static final String PARAGRAPH_LEN_MIN = "ParagraphLengthMin";
    protected static final String PARAGRAPH_LEN_AVG = "ParagraphLengthAvg";
    protected static final String WORD_FREQENCY = "WordFrequency";
    protected static final String DOCUMENT_UUID = "Uuid";

    public TextProcessor(String textExtractorLocation) throws IOException {
        textExtractor = new TextExtractor(textExtractorLocation);
        mapper = new ObjectMapper();
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
            System.out.println("[WARN] Unrecognized type of document. Trying extraction by Tika.");
            String content = textExtractor.bytesToText(request.bodyAsBytes());
            document = new Document(content);
        }

        return document;
    }

    protected Document createDocument(Request request, Response response) throws IOException {
        String contentType = request.headers("Content-Type");
        return extractByContent(request, contentType);
    }

    public String toJson(String name, String value) {
        ObjectNode objectNode1 = mapper.createObjectNode();
        objectNode1.put(name, value);
        return objectNode1.toString();
    }

    public abstract Object paragraphCount(Request request, Response response) throws IOException;

    public abstract Object paragraphLengthMax(Request request, Response response) throws IOException;

    public abstract Object paragraphLengthMin(Request request, Response response) throws IOException;

    public abstract Object paragraphLengthAvg(Request request, Response response) throws IOException;

    public abstract Object wordFrequency(Request request, Response response) throws IOException;
}