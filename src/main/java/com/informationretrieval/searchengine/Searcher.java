package com.informationretrieval.searchengine;

import com.informationretrieval.config.ConfigLoader;
import com.informationretrieval.query.CranQuery;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.queryparser.classic.MultiFieldQueryParser;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.similarities.Similarity;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

public class Searcher {

    private static final Logger LOG = Logger.getLogger(Searcher.class.getName());

    public static void search(List<CranQuery> cranQueryList, Analyzer analyzer, Similarity similarity, ConfigLoader configLoader) throws IOException {
        Directory directory = FSDirectory.open(Paths.get("index"));
        DirectoryReader indexReader = DirectoryReader.open(directory);
        IndexSearcher indexSearcher = new IndexSearcher(indexReader);
        indexSearcher.setSimilarity(similarity);
        List<String> FileContent = new ArrayList<>();


        Map<String, Float> boosts = new HashMap<>();
        boosts.put("Title", 3.0f);
        boosts.put("Words", 1.5f);
        boosts.put("Bibliography", 0.1f);
        boosts.put("Author", 0.1f);

        MultiFieldQueryParser queryParser = new MultiFieldQueryParser(
                new String[] {"Title", "Author", "Bibliography", "Words"}, analyzer, boosts
        );
        String fileName = analyzer.getClass().getSimpleName()+"_"+similarity.getClass().getSimpleName();

        for(CranQuery cranQuery : cranQueryList) {
            try {

                String queryText = cranQuery.queryContent()
                        .toLowerCase()
                        .replaceAll("[^a-z0-9\\s]", " ")
                        .replaceAll("\\s+", " ")
                        .trim();
                Query query = queryParser.parse(queryText);
                ScoreDoc[] hits = indexSearcher.search(query, 50).scoreDocs;
                int rank = 1;
                for (ScoreDoc hit : hits) {
                    Document hitDoc = indexSearcher.doc(hit.doc);
                    String path = hitDoc.get("Id");
                    if (path != null) {
                        FileContent.add(cranQuery.id() + " 0 " + hitDoc.get("Id") +" "+  rank +" "+ hit.score + " STANDARD");
                        rank++;
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        if(!Files.exists(configLoader.getOutputDir())){
            Files.createDirectories(configLoader.getOutputDir());
        }
        Path outputFilePath = configLoader.getOutputDir().resolve(fileName+".txt");
        Files.deleteIfExists(outputFilePath);
        Files.write(outputFilePath, FileContent, StandardCharsets.UTF_8);
        indexReader.close();
        directory.close();

    }
}
