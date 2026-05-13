package com.informationretrieval.config;

import com.informationretrieval.Starter;
import com.informationretrieval.factory.AnalyzerFactory;
import com.informationretrieval.factory.SimilarityFactory;
import lombok.Getter;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.search.similarities.Similarity;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

@Getter
public class ConfigLoader {

    private static final Logger LOG = Logger.getLogger(ConfigLoader.class.getName());

    List<Analyzer> analyzers;

    List<Similarity> similarities;

    Path indexPath;

    Path cranDocumentsPath;

    Path cranQueryPath;

    Path cranQrelPath;

    Path outputDir;

    Path trecEvalPath;

    int topKDocuments;

    List<String> metrics;

    public ConfigLoader(Path filePath) {
        try {
            Properties properties = new Properties();
            try (FileInputStream fis = new FileInputStream(filePath.toFile())) {
                properties.load(fis);
            }

            String analyzers = properties.getProperty("analyzers", "standard");
            this.analyzers = Arrays.stream(analyzers.split("\\s*,\\s*")).map(AnalyzerFactory::getAnalyzer).toList();

            String sims = properties.getProperty("similarities", "bm25");
            this.similarities = Arrays.stream(sims.split("\\s*,\\s*")).map(SimilarityFactory::getSimilarity).toList();

            indexPath = Paths.get(properties.getProperty("indexPath", "./index")).toAbsolutePath().normalize();

            cranDocumentsPath = Paths.get(properties.getProperty("cranDocumentsPath", "./cran/cran.all.1400")).toAbsolutePath().normalize();

            cranQueryPath = Paths.get(properties.getProperty("cranQueryPath", "./cran/cran.qry")).toAbsolutePath().normalize();

            outputDir = Paths.get(properties.getProperty("outputDir", "./output")).toAbsolutePath().normalize();

            trecEvalPath = Paths.get(properties.getProperty("trecEvalPath", "./trec_eval-9.0.7")).toAbsolutePath().normalize();

            cranQrelPath = Paths.get(properties.getProperty("cranqrelFilePath", "./cran/cranqrel")).toAbsolutePath().normalize();

            topKDocuments = Integer.parseInt(properties.getProperty("topKDocuments", "50"));

            String metrics = properties.getProperty("trecEvalMetrics", "map,P.5,iprec_at_recall.00");

            List<String> tempList = new ArrayList<>(Arrays.stream(metrics.split("\\s*,\\s*")).filter(s -> !s.startsWith("P.")).toList());
            List<String> pMetrics = Arrays.stream(metrics.split("\\s*,\\s*")).filter((s)-> s.startsWith("P.")).toList();
            if(!pMetrics.isEmpty()) {
                String[] arr = pMetrics.stream().map((s) -> s.replaceAll("P.", "")).toArray(String[]::new);
                tempList.add("P." + String.join(",", arr));
            }
            this.metrics = tempList;
        }
        catch (IOException e) {
            LOG.log(Level.SEVERE, "Exception occurred while reading config file!", e);
            throw new RuntimeException(e);
        }
    }
}
