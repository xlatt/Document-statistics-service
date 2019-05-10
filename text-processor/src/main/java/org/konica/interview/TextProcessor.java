package org.konica.interview;

import spark.Request;
import spark.Response;
import spark.Route;

import java.io.IOException;

public class TextProcessor {
    private static TextExtractor textExtractor;
    private static DocumentStore documentStore;
    private static DocumentCache documentCache;

    private static final String TEXT_PDF   = "text/pdf";
    private static final String TEXT_PLAIN = "text/plain";
    private static final String TEXT_WORD  = "text/word";

    public static void init(String textExtractorLocation, String documentStoreLocation) throws IOException {
        textExtractor = new TextExtractor(textExtractorLocation);
        documentStore = new DocumentStore(documentStoreLocation);
        documentCache = new DocumentCache();
    }

    public static Route paragraphCount =        TextProcessor::paragraphCount;
    public static Route paragraphLengthMax =    TextProcessor::paragraphLengthMax;
    public static Route paragraphLengthMin =    TextProcessor::paragraphLengthMin;
    public static Route paragraphLengthAvg =    TextProcessor::paragraphLengthAvg;
    public static Route wordFrequency =         TextProcessor::wordFrequency;
    public static Route saveDocument =          TextProcessor::saveDocument;


    private static Document extractByContent(Request request, String type) throws IOException {
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

    private static Document createDocument(Request request, Response response) throws IOException {
        String contentType = request.headers("Content-Type");
        return extractByContent(request, contentType);
    }

    private static Object paragraphCount(Request request, Response response) throws IOException {
        Document document = createDocument(request, response);
        return document.getParagraphCount().toString() + "\n";
    }

    private static Object paragraphLengthMax(Request request, Response response) throws IOException {
        Document document = createDocument(request, response);
        return document.getParagraphMaxLength().toString() + "\n";
    }

    private static Object paragraphLengthMin(Request request, Response response) throws IOException {
        Document document = createDocument(request, response);
        return document.getParagraphMinLength().toString() + "\n";
    }

    private static Object paragraphLengthAvg(Request request, Response response) throws IOException {
        Document document = createDocument(request, response);
        return document.getParagraphAvgLength().toString() + "\n";
    }

    private static Object wordFrequency(Request request, Response response) throws IOException {
        Document document = createDocument(request, response);
        return "Not yet implemented\n";
    }

    private static Object saveDocument(Request request, Response response) throws IOException {
        Document document = createDocument(request, response);
        return "Not yet implemented\n";
    }
}