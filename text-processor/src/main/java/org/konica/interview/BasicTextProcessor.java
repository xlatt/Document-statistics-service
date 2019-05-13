package org.konica.interview;

import spark.Request;
import spark.Response;

import java.io.IOException;

public class BasicTextProcessor extends TextProcessor {
    public BasicTextProcessor(String textExtractorLocation) throws IOException {
        super(textExtractorLocation);
    }

    public Object paragraphCount(Request request, Response response) throws IOException {
        Document document = super.createDocument(request, response);
        return document.parseParagraphCount().toString() + "\n";
    }

    public Object paragraphLengthMax(Request request, Response response) throws IOException {
        Document document = createDocument(request, response);
        return document.parseParagraphMaxLength().toString() + "\n";
    }

    public Object paragraphLengthMin(Request request, Response response) throws IOException {
        Document document = createDocument(request, response);
        return document.parseParagraphMinLength().toString() + "\n";
    }

    public Object paragraphLengthAvg(Request request, Response response) throws IOException {
        Document document = createDocument(request, response);
        return document.parseParagraphAvgLength().toString() + "\n";
    }

    public Object wordFrequency(Request request, Response response) throws IOException {
        Document document = createDocument(request, response);
        return document.parseWordFrequency().toString() + "\n";
    }
}