package com.example.aigent.tools;

import com.example.aigent.model.Tool;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

/**
 * 网页搜索工具
 * 
 * 提供网络搜索和网页内容提取功能，支持Mock模式用于测试
 */
@Slf4j
@Component
public class WebSearchTool implements Tool {

    private final ObjectMapper objectMapper;
    private final HttpClient httpClient;
    
    @Value("${app.tools.tavily.api-key:}")
    private String tavilyApiKey;
    
    @Value("${app.tools.tavily.mock-enabled:true}")
    private boolean mockEnabled;

    public WebSearchTool(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
        this.httpClient = HttpClient.newHttpClient();
    }

    @Override
    public String getName() {
        return "web_search";
    }

    @Override
    public String getDescription() {
        return "用于搜索互联网获取最新信息，回答需要实时数据的问题。参数：query（搜索关键词）";
    }

    @Override
    public JsonNode getParameters() {
        ObjectNode properties = objectMapper.createObjectNode();
        properties.put("type", "object");
        
        ObjectNode queryProp = objectMapper.createObjectNode();
        queryProp.put("type", "string");
        queryProp.put("description", "搜索关键词");
        
        ObjectNode params = objectMapper.createObjectNode();
        params.set("properties", queryProp);
        
        return params;
    }

    @Override
    public String execute(JsonNode arguments) {
        String query = arguments.has("query") ? arguments.get("query").asText() : "";
        
        if (mockEnabled || tavilyApiKey == null || tavilyApiKey.isEmpty()) {
            log.info("使用Mock模式进行搜索: {}", query);
            return executeMock(query);
        }
        
        return executeRealSearch(query);
    }

    private String executeMock(String query) {
        return """
            {
                "results": [
                    {
                        "title": "关于%s的搜索结果",
                        "url": "https://example.com/search",
                        "content": "这是关于'%s'的模拟搜索结果。在实际应用中，这里会显示真实的搜索内容摘要。"
                    },
                    {
                        "title": "%s - 最新资讯",
                        "url": "https://example.com/news",
                        "content": "最新相关信息和新闻报道。"
                    }
                ]
            }
            """.formatted(query, query, query);
    }

    private String executeRealSearch(String query) {
        try {
            String jsonBody = "{\"query\": \"" + query + "\", \"search_depth\": \"basic\"}";
            
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create("https://api.tavily.com/search"))
                    .header("Content-Type", "application/json")
                    .header("Authorization", "Bearer " + tavilyApiKey)
                    .POST(HttpRequest.BodyPublishers.ofString(jsonBody))
                    .build();
            
            HttpResponse<String> response = httpClient.send(request, 
                    HttpResponse.BodyHandlers.ofString());
            
            return response.body();
        } catch (Exception e) {
            log.error("搜索失败: {}", e.getMessage());
            return "{\"error\": \"搜索失败: " + e.getMessage() + "\"}";
        }
    }
}
