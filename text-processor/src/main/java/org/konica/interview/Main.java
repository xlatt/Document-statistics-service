package org.konica.interview;


import static spark.Spark.get;
import static spark.Spark.put;
import static spark.Spark.post;

import java.io.*;

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

        String textExtractorLocation = "http://localhost:9998/tika";
        String documentStoreLocation = "mongodb://localhost:27017";

        TextProcessor.init(textExtractorLocation, documentStoreLocation);

        put("/stats/paragraph/count",               TextProcessor.paragraphCount);
        put("/stats/paragraph/length/max",          TextProcessor.paragraphLengthMax);
        put("/stats/paragraph/length/min",          TextProcessor.paragraphLengthMin);
        put("/stats/paragraph/length/avg",          TextProcessor.paragraphLengthAvg);
        put("/stats/word/frequency",                TextProcessor.wordFrequency);

        post("/document",                           TextProcessor.saveDocument);
        get("/document/:id/paragraph/count",        TextProcessor.paragraphCount);
        get("/document/:id/paragraph/length/max",   TextProcessor.paragraphLengthMax);
        get("/document/:id/paragraph/length/min",   TextProcessor.paragraphLengthMin);
        get("/document/:id/paragraph/length/avg",   TextProcessor.paragraphLengthAvg);
        get("/document/:id/word/frequency",         TextProcessor.wordFrequency);

        // delete("/document/:id", TextProcessor.deleteDocument);
        // put("/document/:id", TextProcessor.updateDocument);
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