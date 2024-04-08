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
    public static void insertion(Object[] result, boolean includeEventCode,boolean includeSourcename , boolean includeTimestamp, boolean includeMessage) throws IOException {
        RestHighLevelClient client = new RestHighLevelClient(
                RestClient.builder(new HttpHost("localhost", 9200, "http")));

        String indexName = "collectedlogs";

        try {
            BulkRequest bulkRequest = new BulkRequest();

            for (Object obj : result) {
                Object[] array = (Object[]) obj;

//                Map<String, Object> document = new HashMap<>();
//                document.put("eventcode", array[0]);
//                document.put("sourcename", array[1]);
                Map<String, Object> document = new HashMap<>();

                if (includeTimestamp) {
                    document.put("timestamp", array[2]);
                }
                if (includeEventCode) {
                    document.put("eventcode", array[0]);
                }
                if (includeSourcename) {
                    document.put("sourcename", array[1]);
                }
                if (includeMessage) {
                    document.put("message", array[3]);
                }

                IndexRequest request = new IndexRequest(indexName).source(document);
                bulkRequest.add(request);
            }

            BulkResponse bulkResponse = client.bulk(bulkRequest, RequestOptions.DEFAULT);

            if (bulkResponse.hasFailures()) {
                // Handle failures
                System.err.println("Error indexing documents: " + bulkResponse.buildFailureMessage());
            } else {
                System.out.println("Documents indexed successfully.");
            }
        }
        catch (IOException e) {
            System.err.println("Error indexing documents " );
        } finally {
            client.close();

        }
    }
}
