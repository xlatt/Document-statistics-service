package org.konica.interview;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;

public class Document {
    private String content;
    private Integer paragraphCount;
    private Integer paragraphMaxLength;
    private Integer paragraphMinLength;
    private Integer paragraphAvgLength;
    private HashMap<String, Integer> wordFrequency;
    private ArrayList<String> paragraphs;

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

    public Integer getParagraphCount() {
        if (paragraphCount == Integer.MIN_VALUE) {
            parseParagraphCount();
        }

        return paragraphCount;
    }

    public Integer getParagraphMaxLength() {
        if (paragraphMaxLength == Integer.MIN_VALUE) {
            parseParagraphMaxLength();
        }

        return paragraphMaxLength;
    }

    public Integer getParagraphMinLength() {
        if (paragraphMinLength == Integer.MIN_VALUE) {
            parseParagraphMinLength();
        }

        return paragraphMinLength;
    }

    public Integer getParagraphAvgLength() {
        if (paragraphAvgLength == Integer.MIN_VALUE) {
            parseParagraphAvgLength();
        }

        return paragraphAvgLength;
    }

    public HashMap<String, Integer> getWordFrequency() {
        if (wordFrequency == null) {
            wordFrequency = new HashMap<>();
            parseWordFrequency();
        }

        return wordFrequency;
    }

    private void splitByParagraphs() {
        String[] p = content.split("\n");
        paragraphs = new ArrayList<>(Arrays.asList(p));

        paragraphs.removeIf(String::isEmpty);
    }

    private void parseParagraphCount() {
        paragraphCount = paragraphs.size();
    }

    private void parseParagraphMaxLength() {
        String longest = paragraphs.stream().max(Comparator.comparingInt(String::length)).get();
        System.out.println(longest);
        paragraphMaxLength = longest.length();
    }

    private void parseParagraphMinLength() {
        String longest = paragraphs.stream().min(Comparator.comparingInt(String::length)).get();
        paragraphMinLength = longest.length();
    }

    private void parseParagraphAvgLength() {
        paragraphAvgLength = paragraphs.stream().mapToInt(String::length).sum() / getParagraphCount();
    }

    private void parseWordFrequency() {
        wordFrequency.put("gregos", 1);
    }
}