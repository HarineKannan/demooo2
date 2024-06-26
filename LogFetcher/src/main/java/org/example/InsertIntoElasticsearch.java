package org.example;

import org.apache.http.HttpHost;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class InsertIntoElasticsearch {

    public static String latestTimestamp(String neededLog) {
        String latestTimestamp = "0";
        Test test = new Test();
        Object[] result = test.getArray(neededLog);
        for (Object obj : result) {
            if (obj instanceof HashMap) {
                HashMap<String, String> event = (HashMap<String, String>) obj;
                for (String key : event.keySet()) {
                    if (key.equals("Time Generated")) {
                        String timestamp = event.get(key);
                        if (timestamp.compareTo(latestTimestamp) > 0) {
                            latestTimestamp = timestamp;
                        }
                    }
                }
            }
        }
        return latestTimestamp;
    }

    public static void insertion(String neededLog) throws IOException {
        RestHighLevelClient client = new RestHighLevelClient(
                RestClient.builder(new HttpHost("localhost", 9200, "http")));

       
        String indexName = neededLog.toLowerCase();
        Test test = new Test();
        Object[] result = test.getArray(neededLog);
       
        int c=0;
        try {
            BulkRequest bulkRequest = new BulkRequest();

            for (Object obj : result) {
                Map<String, String> event = new HashMap<>();

                if (obj instanceof HashMap) {
                    HashMap<String, String> eventData = (HashMap<String, String>) obj;
                    event.put("sourcename", eventData.get("Source Name"));
                    event.put("message", eventData.get("Message"));
                    event.put("eventcode", eventData.get("Event Code"));
                    event.put("timegenerated", eventData.get("Time Generated"));
                    event.put("timewritten", eventData.get("Time Written"));
                    event.put("category", eventData.get("Category"));
                    event.put("recordnumber", eventData.get("Record Number"));
                    event.put("user", eventData.get("User"));
                    event.put("eventidentifier", eventData.get("Event Identifier"));
                    event.put("computername", eventData.get("Computer Name"));

                   
                    IndexRequest request = new IndexRequest(indexName).source(event);
                    bulkRequest.add(request);
                    c++;
                } else {
                    System.out.println("Error");
                }

               
            }

            BulkResponse bulkResponse = client.bulk(bulkRequest, RequestOptions.DEFAULT);

            if (bulkResponse.hasFailures()) {
                System.err.println("Error indexing documents: " + bulkResponse.buildFailureMessage());
            } else {
                System.out.println("Documents indexed successfully.");
            }
        } catch (IOException e) {
            System.err.println("Error indexing documents "+c );
        } finally {
            client.close();
     
        }
    }

    public static void insertionFrom(String neededLog, String latestTimestamp) throws IOException {
        RestHighLevelClient client = new RestHighLevelClient(
                RestClient.builder(new HttpHost("localhost", 9200, "http")));

        String indexName = neededLog.toLowerCase();

        Test test = new Test();
        Object[] result = test.getArray(neededLog);

        int c=0;
        try {
            BulkRequest bulkRequest = new BulkRequest();

            for (Object obj : result) {
                Map<String, String> event = new HashMap<>();

                if (obj instanceof HashMap) {
                    HashMap<String, String> eventData = (HashMap<String, String>) obj;
                    String eventTimestamp = eventData.get("Time Generated");
                    if (eventTimestamp != null && eventTimestamp.compareTo(latestTimestamp) > 0) {
                    	event.put("sourcename", eventData.get("Source Name"));
                        event.put("message", eventData.get("Message"));
                        event.put("eventcode", eventData.get("Event Code"));
                        event.put("timegenerated", eventData.get("Time Generated"));
                        event.put("timewritten", eventData.get("Time Written"));
                        event.put("category", eventData.get("Category"));
                        event.put("recordnumber", eventData.get("Record Number"));
                        event.put("user", eventData.get("User"));
                        event.put("eventidentifier", eventData.get("Event Identifier"));
                        event.put("computername", eventData.get("Computer Name"));
                        IndexRequest request = new IndexRequest(indexName).source(event);
                        bulkRequest.add(request);
                        c++;
                    }

                } else {
                    System.out.println("Error");
                }

           
            }

            BulkResponse bulkResponse = client.bulk(bulkRequest, RequestOptions.DEFAULT);

            if (bulkResponse.hasFailures()) {
                System.err.println("Error indexing documents: " + bulkResponse.buildFailureMessage());
            } else {
                System.out.println("Documents indexed successfully.");
            }
        } catch (IOException e) {
            System.err.println("Error indexing documents "+c );
        } finally {
            client.close();
     
        }
    }

}