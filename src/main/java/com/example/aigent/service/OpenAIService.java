package com.example.aigent.service;

import com.example.aigent.model.Message;
import com.example.aigent.model.ToolCall;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;

/**
 * OpenAI服务集成
 * 
 * 负责与OpenAI API通信，支持普通对话和工具调用两种模式
 */
@Slf4j
@Service
public class OpenAIService {

    private final ObjectMapper objectMapper;
    private final HttpClient httpClient;
    private final String apiKey;
    private final String baseUrl;
    private final String model;
    private final double temperature;

    public OpenAIService(ObjectMapper objectMapper,
                        @Value("${spring.ai.openai.api-key}") String apiKey,
                        @Value("${spring.ai.openai.base-url:https://api.openai.com}") String baseUrl,
                        @Value("${spring.ai.openai.chat.options.model:gpt-3.5-turbo-0613}") String model,
                        @Value("${spring.ai.openai.chat.options.temperature:0.7}") double temperature) {
        this.objectMapper = objectMapper;
        this.httpClient = HttpClient.newHttpClient();
        this.apiKey = apiKey;
        this.baseUrl = baseUrl;
        this.model = model;
        this.temperature = temperature;
    }

    /**
     * 发送普通对话请求
     * @param messages 消息列表
     * @return 响应内容
     */
    public String chatCompletion(List<Message> messages) throws Exception {
        ObjectNode requestBody = objectMapper.createObjectNode();
        requestBody.put("model", model);
        requestBody.put("temperature", temperature);
        
        ArrayNode messagesArray = objectMapper.createArrayNode();
        for (Message message : messages) {
            ObjectNode msgNode = objectMapper.createObjectNode();
            msgNode.put("role", message.getRole());
            msgNode.put("content", message.getContent());
            messagesArray.add(msgNode);
        }
        requestBody.set("messages", messagesArray);
        
        String response = sendRequest(requestBody);
        return extractContent(response);
    }

    /**
     * 发送工具调用请求
     * @param messages 消息列表
     * @param tools 工具列表（JSON Schema格式）
     * @return 工具调用信息
     */
    public ToolCall chatCompletionWithTools(List<Message> messages, ArrayNode tools) throws Exception {
        ObjectNode requestBody = objectMapper.createObjectNode();
        requestBody.put("model", model);
        requestBody.put("temperature", temperature);
        requestBody.put("tool_choice", "auto");
        
        ArrayNode messagesArray = objectMapper.createArrayNode();
        for (Message message : messages) {
            ObjectNode msgNode = objectMapper.createObjectNode();
            msgNode.put("role", message.getRole());
            msgNode.put("content", message.getContent());
            
            if ("tool".equals(message.getRole()) && message.getToolName() != null) {
                msgNode.put("name", message.getToolName());
                if (message.getToolCallId() != null) {
                    msgNode.put("tool_call_id", message.getToolCallId());
                }
            }
            
            messagesArray.add(msgNode);
        }
        requestBody.set("messages", messagesArray);
        requestBody.set("tools", tools);
        
        String response = sendRequest(requestBody);
        return extractToolCall(response);
    }

    /**
     * 发送HTTP请求到OpenAI API
     */
    private String sendRequest(ObjectNode requestBody) throws Exception {
        String jsonBody = objectMapper.writeValueAsString(requestBody);
        
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(baseUrl + "/v1/chat/completions"))
                .header("Content-Type", "application/json")
                .header("Authorization", "Bearer " + apiKey)
                .POST(HttpRequest.BodyPublishers.ofString(jsonBody))
                .build();
        
        log.debug("发送请求到OpenAI: {}", jsonBody);
        
        HttpResponse<String> response = httpClient.send(request, 
                HttpResponse.BodyHandlers.ofString());
        
        log.debug("OpenAI响应: {}", response.body());
        
        if (response.statusCode() != 200) {
            throw new RuntimeException("OpenAI API请求失败: " + response.statusCode() + " - " + response.body());
        }
        
        return response.body();
    }

    /**
     * 从响应中提取内容
     */
    private String extractContent(String response) throws Exception {
        JsonNode root = objectMapper.readTree(response);
        JsonNode choices = root.get("choices");
        
        if (choices != null && choices.isArray() && choices.size() > 0) {
            JsonNode message = choices.get(0).get("message");
            if (message != null && message.has("content")) {
                return message.get("content").asText();
            }
        }
        
        return "无法解析响应";
    }

    /**
     * 从响应中提取工具调用信息
     */
    private ToolCall extractToolCall(String response) throws Exception {
        JsonNode root = objectMapper.readTree(response);
        JsonNode choices = root.get("choices");
        
        if (choices != null && choices.isArray() && choices.size() > 0) {
            JsonNode message = choices.get(0).get("message");
            
            if (message != null && message.has("tool_call")) {
                JsonNode toolCall = message.get("tool_call");
                String id = toolCall.has("id") ? toolCall.get("id").asText() : null;
                String toolName = toolCall.has("function") ? toolCall.get("function").get("name").asText() : null;
                String arguments = toolCall.has("function") ? toolCall.get("function").get("arguments").asText() : "{}";
                
                return ToolCall.builder()
                        .id(id)
                        .toolName(toolName)
                        .arguments(arguments)
                        .build();
            }
        }
        
        return ToolCall.builder().build();
    }
}
