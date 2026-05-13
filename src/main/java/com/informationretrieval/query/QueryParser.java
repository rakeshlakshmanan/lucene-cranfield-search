package com.informationretrieval.query;

import java.io.BufferedReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class QueryParser {

    private static final Logger LOG = Logger.getLogger(QueryParser.class.getName());

    public static List<CranQuery> parse(Path path){
        List<CranQuery> queries = new ArrayList<>();
        try(BufferedReader bufferedReader = Files.newBufferedReader(path)){

            String queryLine = bufferedReader.readLine();
            int i = 1;

            while(queryLine != null){
                if(queryLine.matches("(\\.I)( )(\\d)*")){
                    StringBuilder stringBuilder;
                    CranQuery.Builder cranQueryBuilder = CranQuery.builder();
                    cranQueryBuilder.id(String.valueOf(i));
                    queryLine = bufferedReader.readLine();
                    while(queryLine != null && !queryLine.matches("(\\.I)( )(\\d)*")){
                        if(queryLine.matches("(\\.W)")){
                            stringBuilder = new StringBuilder();
                            queryLine = bufferedReader.readLine();
                            while(queryLine != null && !queryLine.matches("(\\.I)( )(\\d)*")){
                                stringBuilder.append(queryLine).append(" ");
                                queryLine = bufferedReader.readLine();
                            }
                            cranQueryBuilder.queryContent(stringBuilder.toString());
                        }
                    }
                    i++;
                    queries.add(cranQueryBuilder.build());
                }
            }
        }
        catch (Exception exception) {
            LOG.log(Level.SEVERE, "Exception occurred while parsing the query file.",exception);
            throw new RuntimeException(exception);
        }
        return queries;
    }
}
