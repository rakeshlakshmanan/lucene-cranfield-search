package com.informationretrieval.evaluate;

import com.informationretrieval.config.ConfigLoader;
import com.informationretrieval.searchengine.Searcher;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.search.similarities.Similarity;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Result {

    private static final Logger LOG = Logger.getLogger(Result.class.getName());

    public static void execute(ConfigLoader configLoader) throws InterruptedException, IOException {
        Path trecEvalPath = configLoader.getTrecEvalPath();
        Path qrelsFile = configLoader.getCranQrelPath();
        Path outputDir = configLoader.getOutputDir();

        List<String> metrics = configLoader.getMetrics();
        List<EvalResult> resultList = new ArrayList<>();
        StringBuilder stringBuilder = new StringBuilder();

        for (Analyzer analyzer : configLoader.getAnalyzers()) {
            for (Similarity similarity : configLoader.getSimilarities()) {
                String fileName = analyzer.getClass().getSimpleName() + "_" + similarity.getClass().getSimpleName();
                Path resultFile = outputDir.resolve( fileName +".txt");

                List<String> commandList = new ArrayList<>();
                commandList.add(trecEvalPath.resolve("trec_eval").toString());
                commandList.add("-m");
                commandList.add("runid");
                for (String metric : metrics) {
                    commandList.add("-m");
                    commandList.add(metric);
                }
                commandList.add(qrelsFile.toString());
                commandList.add(resultFile.toAbsolutePath().normalize().toString());

                LOG.log(Level.INFO, "Command to be executed: " + String.join(" ", commandList));

                ProcessBuilder pb = new ProcessBuilder(commandList);
                pb.redirectErrorStream(true);
                Process process = pb.start();

                EvalResult.Builder builder = EvalResult.builder()
                        .runID(fileName);

                try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
                    String line;
                    while ((line = reader.readLine()) != null) {
                        line = line.trim();
                        if (!line.isEmpty()) {

                            String[] parts = line.split("\\s+");
                            if (parts.length >= 3) {
                                if(!parts[0].equals("runid")){
                                    builder.setMetricValue(parts[0], parts[2]);
                                }
                                else{
                                    stringBuilder.append("--------------------------------------------------\n");
                                }
                            }
                            stringBuilder.append(line).append("\n");
                        }
                    }
                }
                int exitValue = process.waitFor();
                if(exitValue != 0) {
                    LOG.log(Level.SEVERE, "Process returned non-zero exit value: {0}", exitValue);
                    LOG.log(Level.SEVERE, "Output of the execution: {0}",stringBuilder);
                    throw new RuntimeException("Process exited with value " + exitValue);
                }
                resultList.add(builder.build());
            }
        }
        SwingUtilities.invokeLater(() -> showResultsTable(resultList));
    }

    private static void showResultsTable(List<EvalResult> results) {
        JFrame frame = new JFrame("TREC Evaluation Results");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        List<String> metrics = results.getFirst().getMetricsKeys();
        String[] columns = new String[metrics.size() + 1];
        columns[0] = "RunID";
        System.arraycopy(metrics.toArray(new String[0]), 0, columns, 1, metrics.size());

        DefaultTableModel model = new DefaultTableModel(columns, 0);

        for (EvalResult r : results) {
            String[] row = new String[metrics.size() + 1];
            row[0] = r.runID();
            for (int i = 0; i < metrics.size(); i++) {
                row[i + 1] = r.metrics().getOrDefault(metrics.get(i), "-");
            }
            model.addRow(row);
        }

        JTable table = new JTable(model);
        JScrollPane scrollPane = new JScrollPane(table);
        frame.add(scrollPane);

        frame.setSize(800, 400);
        frame.setVisible(true);
    }

}
