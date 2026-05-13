package com.informationretrieval;

import com.informationretrieval.config.ConfigLoader;
import com.informationretrieval.document.CranDocumentParser;
import com.informationretrieval.evaluate.Result;
import com.informationretrieval.query.CranQuery;
import com.informationretrieval.query.QueryParser;
import com.informationretrieval.searchengine.*;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.search.similarities.Similarity;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Starter {

    private static final Logger LOG = Logger.getLogger(Starter.class.getName());

    public static void main(String[] args) {
        try {
            Path confPath = Paths.get("config.properties");
            if(!Files.exists(confPath)) {
                throw new RuntimeException("configuration file not found");
            }
            ConfigLoader configLoader = new ConfigLoader(confPath);
            for(Analyzer analyzer : configLoader.getAnalyzers()) {
                List<Document> documentList = CranDocumentParser.createDocument(configLoader.getCranDocumentsPath());
                Indexer.index(documentList, configLoader.getIndexPath(), analyzer );
                List<CranQuery> canQueries = QueryParser.parse(configLoader.getCranQueryPath());


                for(Similarity similarity : configLoader.getSimilarities()) {
                    Searcher.search(canQueries, analyzer, similarity, configLoader);
                }
            }
            Result.execute(configLoader);
        }
        catch (Exception e) {
            LOG.log(Level.SEVERE, "Exception occurred.", e);
            System.exit(1);
        }


    }
}
