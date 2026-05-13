package com.informationretrieval.searchengine;


import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.document.Document;

import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Indexer {
    private static final Logger LOG = Logger.getLogger(Indexer.class.getName());

    public static void index(List<Document> documentList, Path indexPath, Analyzer analyzer) throws IOException {
        IndexWriterConfig indexWriterConfig = new IndexWriterConfig(analyzer);
        indexWriterConfig.setOpenMode(IndexWriterConfig.OpenMode.CREATE);
        try(Directory dir = FSDirectory.open(indexPath);
            IndexWriter indexWriter = new IndexWriter(dir, indexWriterConfig)){

            documentList.forEach(document -> {
                try {
                    indexWriter.addDocument(document);
                } catch (IOException e) {
                    throw new RuntimeException("IOException occurred when adding document", e);
                }
            });
        }
        catch (IOException exception){
            LOG.log(Level.SEVERE, "IOException occurred when adding documents", exception);
            throw new RuntimeException("IOException occurred while indexing documents!",exception);
        }

    }
}
