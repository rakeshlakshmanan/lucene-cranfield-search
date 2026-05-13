package com.informationretrieval.factory;

import org.apache.lucene.search.similarities.*;

public class SimilarityFactory {

    public static Similarity getSimilarity(String name) {
        return switch (name.toLowerCase()) {
            case "classic" -> new ClassicSimilarity();
            case "boolean" -> new BooleanSimilarity();
            default -> new BM25Similarity();



        };
    }
}
