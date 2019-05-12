package org.konica.interview;

import spark.Request;
import spark.Response;

import java.io.IOException;

public abstract class TextProcessor {
    protected TextExtractor textExtractor;

    protected static final String TEXT_PDF   = "text/pdf";
    protected static final String TEXT_PLAIN = "text/plain";
    protected static final String TEXT_WORD  = "text/word";

    public TextProcessor(String textExtractorLocation) throws IOException {
        textExtractor = new TextExtractor(textExtractorLocation);
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

    public abstract Object paragraphCount(Request request, Response response) throws IOException;

    public abstract Object paragraphLengthMax(Request request, Response response) throws IOException;

    public abstract Object paragraphLengthMin(Request request, Response response) throws IOException;

    public abstract Object paragraphLengthAvg(Request request, Response response) throws IOException;

    public abstract Object wordFrequency(Request request, Response response) throws IOException;
}