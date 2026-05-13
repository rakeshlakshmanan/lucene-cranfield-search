package com.informationretrieval.document;

import org.apache.lucene.document.*;

import java.io.BufferedReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class CranDocumentParser {

    private static final Logger LOG = Logger.getLogger(CranDocument.class.getName());

    public static List<CranDocument> parseCranDocument(Path cranDocPath) {
        List<CranDocument> cranDocuments = new ArrayList<>();

        try( BufferedReader bufferedReader = Files.newBufferedReader(cranDocPath)){
            String line = bufferedReader.readLine();

            while (line != null) {

                if (line.startsWith(".I")) {
                    StringBuilder stringBuilder;
                    CranDocument.Builder cranDocumentBuilder = CranDocument.builder();
                    cranDocumentBuilder.id(line.substring(3));
                    line = bufferedReader.readLine();

                    while (line != null && !line.startsWith(".I")) {
                        if (line.startsWith(".T")) {
                            stringBuilder = new StringBuilder();
                            line = bufferedReader.readLine();

                            while (line != null && !line.startsWith((".A"))) {
                                stringBuilder.append(line).append(" ");
                                line = bufferedReader.readLine();
                            }
                            cranDocumentBuilder.title(stringBuilder.toString());
                        }
                        else if (line.startsWith(".A")) {
                            stringBuilder = new StringBuilder();
                            line = bufferedReader.readLine();

                            while (line != null && !line.startsWith((".B"))) {
                                stringBuilder.append(line).append(" ");
                                line = bufferedReader.readLine();
                            }
                            cranDocumentBuilder.author(stringBuilder.toString());
                        }
                        else if(line.startsWith(".B")){
                            stringBuilder = new StringBuilder();
                            line = bufferedReader.readLine();
                            while(line != null && !line.startsWith((".W"))){
                                stringBuilder.append(line).append(" ");
                                line = bufferedReader.readLine();
                            }
                            cranDocumentBuilder.bibliography(stringBuilder.toString());
                        }
                        else if (line.startsWith(".W")) {
                            stringBuilder = new StringBuilder();
                            line = bufferedReader.readLine();

                            while (line != null && !line.startsWith(".I")) {
                                stringBuilder.append(line).append(" ");
                                line = bufferedReader.readLine();
                            }
                            cranDocumentBuilder.words(stringBuilder.toString());
                        }
                    }
                    cranDocuments.add(cranDocumentBuilder.build());
                }
            }
        } catch (Exception e) {
            LOG.log(Level.SEVERE, "Exception occurred while parsing the cran document.", e);
            throw new RuntimeException(e);
        }
        return cranDocuments;
    }


    public static List<Document> createDocument(Path cranDocPath) {
        List<CranDocument> cranDocumentList = parseCranDocument(cranDocPath);
        List<Document> documentList = new ArrayList<>();
        for(CranDocument cranDocument : cranDocumentList){
            Document document = new Document();
            document.add(new StringField("Id", cranDocument.id(), Field.Store.YES));
            document.add(new TextField("Title", cranDocument.title(), Field.Store.YES));
            document.add(new TextField("Author", cranDocument.author(), Field.Store.YES));
            document.add(new TextField("Bibliography", cranDocument.bibliography(), Field.Store.YES));
            document.add(new TextField("Words", cranDocument.words(), Field.Store.YES));
            documentList.add(document);
        }
        return documentList;
    }




}
