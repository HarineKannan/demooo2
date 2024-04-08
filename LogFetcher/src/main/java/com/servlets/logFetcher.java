package com.servlets;

import org.example.InsertIntoElasticsearch;

import java.io.*;
import java.sql.*;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONObject;

@WebServlet("/logFetcher")
public class logFetcher extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private static final String DB_URL = "jdbc:postgresql://localhost:5432/LogCollection";
    private static final String DB_USERNAME = "postgres";
    private static final String DB_PASSWORD = "Kala@1001";

    protected void doOptions(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setHeader("Access-Control-Allow-Origin", "http://localhost:4200");
        response.setHeader("Access-Control-Allow-Methods", "POST, OPTIONS");
        response.setHeader("Access-Control-Allow-Headers", "Content-Type");
        response.setHeader("Access-Control-Allow-Credentials", "true");
        response.setStatus(HttpServletResponse.SC_OK);
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html");

        PrintWriter out = response.getWriter();
        out.println("<html><body>");

        try {
            Class.forName("org.postgresql.Driver");
            Connection connection = DriverManager.getConnection(DB_URL, DB_USERNAME, DB_PASSWORD);
            out.println("<h3>Connected to PostgreSQL</h3>");
            Statement st = connection.createStatement();
            String sql = "select * from logfetch";
            ResultSet rs = st.executeQuery(sql);
            while (rs.next()) {
                out.println(rs.getInt("id"));
                out.println(rs.getString("logtype"));
                out.println(rs.getString("updatedtime"));
            }
            connection.close();
        } catch (SQLException e) {
            out.println("<h3>Error in connecting to PostgreSQL</h3>");
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }

        out.println("</body></html>");
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("application/json");
        response.setHeader("Access-Control-Allow-Origin", "http://localhost:4200");
        response.setHeader("Access-Control-Allow-Methods", "POST, OPTIONS");
        response.setHeader("Access-Control-Allow-Headers", "Content-Type");
        response.setHeader("Access-Control-Allow-Credentials", "true");

        PrintWriter out = response.getWriter();

        try {
            BufferedReader reader = request.getReader();
            StringBuilder jsonPayload = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                jsonPayload.append(line);
            }

            JSONObject jsonObject = new JSONObject(jsonPayload.toString());
            String logType = jsonObject.getString("logtype");


            Class.forName("org.postgresql.Driver");
            try (Connection connection = DriverManager.getConnection(DB_URL, DB_USERNAME, DB_PASSWORD)) {

                if (logType != null && !logType.isEmpty()) {
                    String sql = "SELECT * FROM logfetch WHERE logtype = ?";
                    try (PreparedStatement statement = connection.prepareStatement(sql)) {
                        statement.setString(1, logType);
                        ResultSet rs = statement.executeQuery();
                        if (rs.next()) {
                            String ut = rs.getString("updatedtime");
                            out.print(ut);
                           InsertIntoElasticsearch.insertionFrom(logType, ut);
                           String lt = InsertIntoElasticsearch.latestTimestamp(logType);
                           out.print(lt);
                           String updateSql = "UPDATE logfetch SET updatedtime = ? WHERE logtype = ?";
                           try (PreparedStatement updateStatement = connection.prepareStatement(updateSql)) {
                               updateStatement.setString(1, lt);
                               updateStatement.setString(2, logType);
                               int rowsAffected = updateStatement.executeUpdate();
                               if (rowsAffected > 0) {
                                   out.print("done");
                               } else {
                                   out.print("Update failed");
                               }
                           }
                        } else {
                        InsertIntoElasticsearch.insertion(logType);
                            String lt = InsertIntoElasticsearch.latestTimestamp(logType);
                            String insertSql = "INSERT INTO logfetch (logtype, updatedtime) VALUES (?, ?)";
                            try (PreparedStatement insertStatement = connection.prepareStatement(insertSql)) {
                                insertStatement.setString(1, logType);
                                insertStatement.setString(2, lt);
                                int rowsAffected = insertStatement.executeUpdate();
                                if (rowsAffected > 0) {
                                    out.print("Inserted new log type");
                                } else {
                                    out.print("Insert failed");
                                }
                            }
                           
                        }
                   
                 
                    }
                } else {
                    out.println("<p>No log type specified.</p>");
                }

            }
        } catch (Exception e) {
            JSONObject jsonResponse = new JSONObject();
            jsonResponse.put("status", "error");
            jsonResponse.put("All records are inserted up to time.", e.getMessage());
            out.println(jsonResponse.toString());
            e.printStackTrace();
        }
    }
   
}
//protected void doGet(HttpServletRequest request, HttpServletResponse response)
//throws ServletException, IOException {
//response.setContentType("application/json");
//response.setCharacterEncoding("UTF-8");
//
//PrintWriter out = response.getWriter();
//
//try {
//String searchQuery = request.getParameter("q");
////String searchQuery = "16384";
//
//if (searchQuery != null && !searchQuery.isEmpty()) {
//  SearchRequest searchRequest = new SearchRequest("application");
//  SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
//  searchSourceBuilder.query(QueryBuilders.matchQuery("eventcode", searchQuery));
//  searchRequest.source(searchSourceBuilder);
//
//  SearchHit[] hits = esClient.search(searchRequest, RequestOptions.DEFAULT).getHits().getHits();
//
//  JSONArray jsonArray = new JSONArray();
//  for (SearchHit hit : hits) {
//      JSONObject jsonObject = new JSONObject(hit.getSourceAsString());
//      jsonArray.put(jsonObject);
//  }
//  out.print(jsonArray.toString());
//} else {
//  out.println("<p>No search query specified.</p>");
//}
//} catch (Exception e) {
//JSONObject jsonResponse = new JSONObject();
//jsonResponse.put("status", "error");
//jsonResponse.put("message", e.getMessage());
//out.println(jsonResponse.toString());
//e.printStackTrace();
//}
//}
