package org.konica.interview;

import com.fasterxml.jackson.annotation.JsonFilter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Represents document. Holds content of document retrieved from TextExtractor.
 * Holds logic for extracting statistics from document.
 *
 */
@JsonFilter("Document")
public class Document {
    private String content;
    private Integer paragraphCount;
    private Integer paragraphMaxLength;
    private Integer paragraphMinLength;
    private Integer paragraphAvgLength;
    private HashMap<String, Long> wordFrequency;
    private ArrayList<String> paragraphs;

    /**
     * Constructor for empty Document
     */
    public Document() {
        this.content = null;
        this.paragraphCount = Integer.MIN_VALUE;
        this.paragraphMaxLength = Integer.MIN_VALUE;
        this.paragraphMinLength = Integer.MIN_VALUE;
        this.paragraphAvgLength = Integer.MIN_VALUE;
        this.wordFrequency = null;
        this.paragraphs = null;
    }

    /**
     * Constructor for Document
     * @param content holds content of document
     */
    public Document(String content) {
        this.content = content;
        this.paragraphCount = Integer.MIN_VALUE;
        this.paragraphMaxLength = Integer.MIN_VALUE;
        this.paragraphMinLength = Integer.MIN_VALUE;
        this.paragraphAvgLength = Integer.MIN_VALUE;
        this.wordFrequency = null;
        this.paragraphs = null;
        splitByParagraphs();
    }


    /**
     * @param paragraphCount
     */
    public void setParagraphCount(Integer paragraphCount) {
        this.paragraphCount = paragraphCount;
    }

    /**
     * @param paragraphMaxLength
     */
    public void setParagraphMaxLength(Integer paragraphMaxLength) {
        this.paragraphMaxLength = paragraphMaxLength;
    }

    /**
     * @param paragraphMinLength
     */
    public void setParagraphMinLength(Integer paragraphMinLength) {
        this.paragraphMinLength = paragraphMinLength;
    }

    /**
     * @param paragraphAvgLength
     */
    public void setParagraphAvgLength(Integer paragraphAvgLength) {
        this.paragraphAvgLength = paragraphAvgLength;
    }

    /**
     * @param wordFrequency
     */
    public void setWordFrequency(HashMap<String, Long> wordFrequency) {
        this.wordFrequency = wordFrequency;
    }

    /**
     * @param paragraphs
     */
    public void setParagraphs(ArrayList<String> paragraphs) {
        this.paragraphs = paragraphs;
    }

    /**
     * @return paragraph count
     */
    public Integer getParagraphCount() {
        return paragraphCount;
    }

    /**
     * @return length of the longest paragraph
     */
    public Integer getParagraphMaxLength() {
        return paragraphMaxLength;
    }

    /**
     * @return length of the shortest paragraph
     */
    public Integer getParagraphMinLength() {
        return paragraphMinLength;
    }

    /**
     * @return average length of the paragraph
     */
    public Integer getParagraphAvgLength() {
        return paragraphAvgLength;
    }

    /**
     * @return list of word ordered in descending order with occasions counter
     */
    public HashMap<String, Long> getWordFrequency() {
        return wordFrequency;
    }

    /**
     * @return array of paragraphs
     */
    public ArrayList<String> getParagraphs(){
        return paragraphs;
    }

    /**
     * Split content to paragraphs
     */
    private void splitByParagraphs() {
        if (content == null)
            return;

        String[] p = content.split("\n");
        paragraphs = new ArrayList<>(Arrays.asList(p));

        paragraphs.removeIf(String::isEmpty);
    }

    /**
     * Count paragraphs
     * @return paragraph count
     */
    public Integer parseParagraphCount() {
        paragraphCount = paragraphs.size();
        return paragraphCount;
    }

    /**
     * Find the longest paragraph
     * @return length of the longest paragraph
     */
    public Integer parseParagraphMaxLength() {
        String longest = paragraphs.stream().max(Comparator.comparingInt(String::length)).get();
        paragraphMaxLength = longest.length();
        return paragraphMaxLength;
    }

    /**
     * Find the shortest paragraph
     * @return length of the shortest paragraph
     */
    public Integer parseParagraphMinLength() {
        String longest = paragraphs.stream().min(Comparator.comparingInt(String::length)).get();
        paragraphMinLength = longest.length();
        return paragraphMinLength;
    }

    /**
     * Find the average length of paragraph
     * @return average length of the paragraph
     */
    public Integer parseParagraphAvgLength() {
        paragraphAvgLength = paragraphs.stream().mapToInt(String::length).sum() / parseParagraphCount();
        return paragraphAvgLength;
    }

    /**
     * Count occurrences of particular word then sort by occurrences and store in reversed order.
     *
     * @return word frequency in descending order
     */
    public HashMap<String, Long> parseWordFrequency() {
        wordFrequency = new HashMap<>();

        for (String p : paragraphs) {
            String[] words = p.split("\\W+");
            for (String word : words) {
                Long freq = wordFrequency.get(word);
                freq = freq == null ? 1 : ++freq;
                wordFrequency.put(word, freq);
            }
        }

        wordFrequency = wordFrequency
                .entrySet()
                .stream()
                .sorted(Map.Entry.comparingByValue(Comparator.reverseOrder()))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e1, LinkedHashMap::new));
        return wordFrequency;
    }
}