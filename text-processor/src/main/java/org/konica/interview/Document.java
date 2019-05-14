package org.konica.interview;

import com.fasterxml.jackson.annotation.JsonFilter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Collectors;

@JsonFilter("Document")
public class Document {
    private String content;
    private Integer paragraphCount;
    private Integer paragraphMaxLength;
    private Integer paragraphMinLength;
    private Integer paragraphAvgLength;
    private HashMap<String, Long> wordFrequency;
    private ArrayList<String> paragraphs;

    public void setParagraphCount(Integer paragraphCount) {
        this.paragraphCount = paragraphCount;
    }

    public void setParagraphMaxLength(Integer paragraphMaxLength) {
        this.paragraphMaxLength = paragraphMaxLength;
    }

    public void setParagraphMinLength(Integer paragraphMinLength) {
        this.paragraphMinLength = paragraphMinLength;
    }

    public void setParagraphAvgLength(Integer paragraphAvgLength) {
        this.paragraphAvgLength = paragraphAvgLength;
    }

    public void setWordFrequency(HashMap<String, Long> wordFrequency) {
        this.wordFrequency = wordFrequency;
    }

    public void setParagraphs(ArrayList<String> paragraphs) {
        this.paragraphs = paragraphs;
    }

    public Document() {
        this.content = null;
        this.paragraphCount = Integer.MIN_VALUE;
        this.paragraphMaxLength = Integer.MIN_VALUE;
        this.paragraphMinLength = Integer.MIN_VALUE;
        this.paragraphAvgLength = Integer.MIN_VALUE;
        this.wordFrequency = null;
        this.paragraphs = null;
    }

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
        return paragraphCount;
    }

    public Integer getParagraphMaxLength() {
        return paragraphMaxLength;
    }

    public Integer getParagraphMinLength() {
        return paragraphMinLength;
    }

    public Integer getParagraphAvgLength() {
        return paragraphAvgLength;
    }

    public HashMap<String, Long> getWordFrequency() {
        return wordFrequency;
    }

    public ArrayList<String> getParagraphs(){
        return paragraphs;
    }

    private void splitByParagraphs() {
        if (content == null)
            return;

        String[] p = content.split("\n");
        paragraphs = new ArrayList<>(Arrays.asList(p));

        paragraphs.removeIf(String::isEmpty);
    }

    public Integer parseParagraphCount() {
        paragraphCount = paragraphs.size();
        return paragraphCount;
    }

    public Integer parseParagraphMaxLength() {
        String longest = paragraphs.stream().max(Comparator.comparingInt(String::length)).get();
        paragraphMaxLength = longest.length();
        return paragraphMaxLength;
    }

    public Integer parseParagraphMinLength() {
        String longest = paragraphs.stream().min(Comparator.comparingInt(String::length)).get();
        paragraphMinLength = longest.length();
        return paragraphMinLength;
    }

    public Integer parseParagraphAvgLength() {
        paragraphAvgLength = paragraphs.stream().mapToInt(String::length).sum() / parseParagraphCount();
        return paragraphAvgLength;
    }

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