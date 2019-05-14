package org.konica.interview;

import org.apache.commons.cli.*;

import java.io.*;

import static spark.Spark.put;
import static spark.Spark.post;
import static spark.Spark.get;
import static spark.Spark.delete;

public class Main {
    private static String textExtractorUrl = "http://localhost:9998/tika";
    private static String documentStoreUrl = "mongodb://localhost:27017";

    public static void main(String[] args) throws IOException {
        processArgs(args);

        BasicTextProcessor basicTextProcessor = new BasicTextProcessor(textExtractorUrl);
        PersistentTextProcessor persistentTextProcessor = new PersistentTextProcessor(textExtractorUrl, documentStoreUrl);

        put("/stats/paragraph/count",               basicTextProcessor::paragraphCount);
        put("/stats/paragraph/length/max",          basicTextProcessor::paragraphLengthMax);
        put("/stats/paragraph/length/min",          basicTextProcessor::paragraphLengthMin);
        put("/stats/paragraph/length/avg",          basicTextProcessor::paragraphLengthAvg);
        put("/stats/word/frequency",                basicTextProcessor::wordFrequency);

        post("/document",                           persistentTextProcessor::saveDocument);
        get("/document/:id",                        persistentTextProcessor::parseAll);
        get("/document/:id/paragraph/count",        persistentTextProcessor::paragraphCount);
        get("/document/:id/paragraph/length/max",   persistentTextProcessor::paragraphLengthMax);
        get("/document/:id/paragraph/length/min",   persistentTextProcessor::paragraphLengthMin);
        get("/document/:id/paragraph/length/avg",   persistentTextProcessor::paragraphLengthAvg);
        get("/document/:id/word/frequency",         persistentTextProcessor::wordFrequency);
        delete("/document/:id",                     persistentTextProcessor::deleteDocument);
    }

    static void processArgs(String[] args) {
        Options options = new Options();

        Option input = new Option("t", "tika", true, "URL of Tika server");
        input.setRequired(true);
        options.addOption(input);

        Option output = new Option("db", "database", true, "URL of Database server");
        output.setRequired(true);
        options.addOption(output);

        CommandLineParser parser = new DefaultParser();
        HelpFormatter formatter = new HelpFormatter();
        CommandLine cmd;

        try {
            cmd = parser.parse(options, args);
            textExtractorUrl = cmd.getOptionValue("tika");
            documentStoreUrl = cmd.getOptionValue("database");
        } catch (ParseException e) {
            System.out.println(e.getMessage());
            formatter.printHelp("text-processor", options);

            System.exit(1);
        }
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