package org.konica.interview;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/** Used for communication with Tika
 *
 * This sends documents to Tika service and receives content of documents
 * in plain text.
 */
public class TextExtractor {
    private  URL url;

    /**
     * @param url URL of Tika
     * @throws IOException
     */
    public TextExtractor(String url) throws IOException {
        this.url = new URL(url);
    }

    /**
     * Send bytes (of document) to Tika for text extraction.
     *
     * @param bytes byte representation od document
     * @return text which was extracted from document
     * @throws IOException
     */
    public String bytesToText(byte [] bytes) throws IOException {
        HttpURLConnection connection;

        connection = (HttpURLConnection) this.url.openConnection();
        connection.setDoOutput(true);
        connection.setRequestMethod("PUT");
        connection.setRequestProperty("Accept", "text/plain");

        DataOutputStream wr = new DataOutputStream (connection.getOutputStream());
        wr.write(bytes);
        wr.flush();
        wr.close();

        BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream()));
        String line;
        StringBuilder builder = new StringBuilder();

        while ((line = br.readLine()) != null) {
            if (line.equals("")) {
                builder.append("\n");
                continue;
            }

            builder.append(line);
        }

        return builder.toString();
    }
}
