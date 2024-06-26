package org.example;
//
//import java.io.IOException;
//import java.util.HashMap;
//
//import org.elasticsearch.action.search.SearchRequest;
//import org.elasticsearch.action.search.SearchResponse;
//import org.elasticsearch.client.RestClient;
//import org.elasticsearch.client.RestHighLevelClient;
//import org.elasticsearch.index.query.QueryBuilders;
//import org.elasticsearch.search.builder.SearchSourceBuilder;
//import org.apache.http.HttpHost;
//import org.elasticsearch.action.bulk.BulkRequest;
//import org.elasticsearch.action.bulk.BulkResponse;
//import org.elasticsearch.action.index.IndexRequest;
//import org.elasticsearch.client.RequestOptions;
//import org.elasticsearch.client.RestClient;
//import org.elasticsearch.client.RestHighLevelClient;
//
//import java.io.IOException;
//import java.util.HashMap;
//import java.util.Map;
//
//
//public class Search{
//public static String performElasticsearchSearch(String neededField, String searchQuery) throws IOException {
//        StringBuilder searchResults = new StringBuilder();
//
//        RestHighLevelClient client = new RestHighLevelClient(
//                RestClient.builder(new HttpHost("localhost", 9200, "http")));
//
//        try {
//            SearchRequest searchRequest = new SearchRequest("logs");
//            SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
//            sourceBuilder.size(10);
//            sourceBuilder.query(QueryBuilders.wildcardQuery(neededField, "*" + searchQuery + "*"));
//            sourceBuilder.fetchSource(new String[]{"sourcename","eventcode","recordnumber","eventidentifier","timegenerated"}, null);
//            searchRequest.source(sourceBuilder);
//
//            SearchResponse searchResponse = client.search(searchRequest, RequestOptions.DEFAULT);
//
//            searchResponse.getHits().forEach(hit -> {
//                searchResults.append(hit.getSourceAsString());
//            });
//        } finally {
//            client.close();
//        }
//
//        return searchResults.toString();
//    }
//}
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.apache.http.HttpHost;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Search {
    public static List<Map<String, Object>> performElasticsearchSearch(String neededField, String searchQuery) throws IOException {
        List<Map<String, Object>> searchResults = new ArrayList<>();

        RestHighLevelClient client = new RestHighLevelClient(
                RestClient.builder(new HttpHost("localhost", 9200, "http")));

        try {
            SearchRequest searchRequest = new SearchRequest("logs");
            SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
            sourceBuilder.size(10);
            sourceBuilder.query(QueryBuilders.wildcardQuery(neededField, "*" + searchQuery + "*"));
            sourceBuilder.fetchSource(new String[]{"sourcename", "eventcode", "recordnumber", "eventidentifier", "timegenerated"}, null);
            searchRequest.source(sourceBuilder);

          SearchResponse searchResponse = client.search(searchRequest, RequestOptions.DEFAULT);

            for (SearchHit hit : searchResponse.getHits().getHits()) {
                Map<String, Object> hitMap = hit.getSourceAsMap();
                searchResults.add(hitMap);
            }
        } finally {
            client.close();
        }

        return searchResults;
    }
}
