package com.example.aigent.tools;

import com.example.aigent.model.Tool;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Component;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

/**
 * 网页内容抓取工具
 * 
 * 使用Jsoup解析HTML，提取网页标题、正文和链接
 */
@Slf4j
@Component
public class FetchFromWebTool implements Tool {

    private final ObjectMapper objectMapper;
    private final HttpClient httpClient;

    public FetchFromWebTool(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
        this.httpClient = HttpClient.newHttpClient();
    }

    @Override
    public String getName() {
        return "fetch_webpage";
    }

    @Override
    public String getDescription() {
        return "获取网页内容，提取标题、正文和链接。参数：url（网页URL）";
    }

    @Override
    public JsonNode getParameters() {
        ObjectNode properties = objectMapper.createObjectNode();
        
        ObjectNode urlProp = objectMapper.createObjectNode();
        urlProp.put("type", "string");
        urlProp.put("description", "网页URL");
        
        ObjectNode params = objectMapper.createObjectNode();
        params.put("type", "object");
        params.set("properties", properties);
        properties.set("url", urlProp);
        
        return params;
    }

    @Override
    public String execute(JsonNode arguments) {
        String url = arguments.has("url") ? arguments.get("url").asText() : "";
        
        if (url.isEmpty()) {
            return "{\"error\": \"URL不能为空\"}";
        }
        
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .GET()
                    .build();
            
            HttpResponse<String> response = httpClient.send(request, 
                    HttpResponse.BodyHandlers.ofString());
            
            Document doc = Jsoup.parse(response.body(), url);
            
            String title = doc.title();
            String body = doc.body().text();
            if (body.length() > 2000) {
                body = body.substring(0, 2000) + "...";
            }
            
            Elements links = doc.select("a[href]");
            StringBuilder linksStr = new StringBuilder();
            links.stream().limit(10).forEach(link -> {
                String href = link.attr("abs:href");
                String text = link.text();
                linksStr.append("- [").append(text).append("](").append(href).append(")\n");
            });
            
            ObjectNode result = objectMapper.createObjectNode();
            result.put("url", url);
            result.put("title", title);
            result.put("content", body);
            result.put("links", linksStr.toString());
            result.put("status_code", response.statusCode());
            
            return objectMapper.writeValueAsString(result);
            
        } catch (Exception e) {
            log.error("获取网页失败: {}", e.getMessage());
            return "{\"error\": \"获取网页失败: " + e.getMessage() + "\"}";
        }
    }
}
