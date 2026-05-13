package com.informationretrieval.searchengine;
import org.apache.lucene.analysis.*;
import org.apache.lucene.analysis.en.EnglishAnalyzer;
import org.apache.lucene.analysis.en.EnglishPossessiveFilter;
import org.apache.lucene.analysis.en.PorterStemFilter;
import org.apache.lucene.analysis.miscellaneous.ASCIIFoldingFilter;
import org.apache.lucene.analysis.miscellaneous.LengthFilter;
import org.apache.lucene.analysis.standard.StandardTokenizer;

public class CustomAnalyzer extends Analyzer {


    public CustomAnalyzer() {
        super(Analyzer.PER_FIELD_REUSE_STRATEGY);
    }

    @Override
    protected TokenStreamComponents createComponents(String fieldName) {

        Tokenizer tokenizer = new StandardTokenizer();

        TokenStream tokenStream = new LowerCaseFilter(tokenizer);
        tokenStream = new EnglishPossessiveFilter(tokenStream);
        tokenStream = new StopFilter(tokenStream, EnglishAnalyzer.getDefaultStopSet());
        tokenStream = new PorterStemFilter(tokenStream);
        tokenStream = new ASCIIFoldingFilter(tokenStream);
        tokenStream = new LengthFilter(tokenStream, 3, 20);

        return new TokenStreamComponents(tokenizer, tokenStream);

    }
}