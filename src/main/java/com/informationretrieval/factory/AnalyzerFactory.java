package com.informationretrieval.factory;

import com.informationretrieval.searchengine.CustomAnalyzer;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.core.SimpleAnalyzer;
import org.apache.lucene.analysis.core.StopAnalyzer;
import org.apache.lucene.analysis.core.WhitespaceAnalyzer;
import org.apache.lucene.analysis.en.EnglishAnalyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;

public class AnalyzerFactory {

    public static Analyzer getAnalyzer(String name) {
        return switch (name.toLowerCase()) {
            case "english" -> new EnglishAnalyzer();
            case "simple" -> new SimpleAnalyzer();
            case "whitespace" -> new WhitespaceAnalyzer();
            case "custom" -> new CustomAnalyzer();
            case "stop" -> new StopAnalyzer(EnglishAnalyzer.ENGLISH_STOP_WORDS_SET);
            default -> new StandardAnalyzer();
        };
    }
}
