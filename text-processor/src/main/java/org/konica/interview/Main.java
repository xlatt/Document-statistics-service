package org.konica.interview;

import java.io.*;

import static spark.Spark.put;
import static spark.Spark.post;
import static spark.Spark.get;
import static spark.Spark.delete;

/*
    // matches "GET /hello/foo" and "GET /hello/bar"
    // request.params(":name") is 'foo' or 'bar'

    get("/document/:id", (request, response) -> {
        return "Doc id is : " + request.params(":id");
    });

**/

public class Main {
    public static void main(String[] args) throws IOException {

        if (args.length == 1 && (args[0].equals("-h") || args[0].equals("--help"))) {
            printHelp();
            return;
        }

        String textExtractorUrl = "http://localhost:9998/tika";
        String documentStoreUrl = "mongodb://localhost:27017";

        BasicTextProcessor basicTextProcessor = new BasicTextProcessor(textExtractorUrl);
        PersistentTextProcessor persistentTextProcessor = new PersistentTextProcessor(textExtractorUrl, documentStoreUrl);

        put("/stats/paragraph/count",               basicTextProcessor::paragraphCount);
        put("/stats/paragraph/length/max",          basicTextProcessor::paragraphLengthMax);
        put("/stats/paragraph/length/min",          basicTextProcessor::paragraphLengthMin);
        put("/stats/paragraph/length/avg",          basicTextProcessor::paragraphLengthAvg);
        put("/stats/word/frequency",                basicTextProcessor::wordFrequency);

        post("/document",                           persistentTextProcessor::saveDocument);
        get("/document/:id/paragraph/count",        persistentTextProcessor::paragraphCount);
        get("/document/:id/paragraph/length/max",   persistentTextProcessor::paragraphLengthMax);
        get("/document/:id/paragraph/length/min",   persistentTextProcessor::paragraphLengthMin);
        get("/document/:id/paragraph/length/avg",   persistentTextProcessor::paragraphLengthAvg);
        get("/document/:id/word/frequency",         persistentTextProcessor::wordFrequency);
        delete("/document/:id",                     persistentTextProcessor::deleteDocument);
    }

    static void printHelp() {
        System.out.println("help");
    }
}

/**
 *
 *
 * PUT /stats/paragraph/count
 * PUT /stats/paragraph/length/max
 * PUT /stats/paragraph/length/min
 * PUT /stats/paragraph/length/avg
 *
 * PUT /stats/paragraph/word/frequency
 *
 *
 * POST /document -> returns doc ID
 *
 * GET /document/:id/paragraph/count
 * GET /document/:id/paragraph/length/max
 * GET /document/:id/paragraph/length/min
 * GET /document/:id/paragraph/length/avg
 *
 * GET /document/:id/word/frequency
 *
 * DELETE /document/{id}
 *
 * */