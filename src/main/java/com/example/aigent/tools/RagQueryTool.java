package com.example.aigent.tools;

import com.example.aigent.model.Document;
import com.example.aigent.model.Tool;
import com.example.aigent.service.VectorStoreService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * RAG 查询工具
 * 用于从向量存储中检索相关文档
 */
@Slf4j
@Component
public class RagQueryTool implements Tool {

    private final VectorStoreService vectorStoreService;
    private final ObjectMapper objectMapper;

    public RagQueryTool(VectorStoreService vectorStoreService, ObjectMapper objectMapper) {
        this.vectorStoreService = vectorStoreService;
        this.objectMapper = objectMapper;
    }

    @Override
    public String getName() {
        return "rag_query";
    }

    @Override
    public String getDescription() {
        return "从知识库中检索相关文档。当用户的问题需要基于已有知识回答时使用，例如：'项目的核心功能是什么？'、'如何使用这个工具？'";
    }

    @Override
    public JsonNode getParameters() {
        ObjectNode params = objectMapper.createObjectNode();
        params.put("type", "object");
        
        ObjectNode properties = objectMapper.createObjectNode();
        
        ObjectNode queryParam = objectMapper.createObjectNode();
        queryParam.put("type", "string");
        queryParam.put("description", "查询问题，用于检索相关文档");
        properties.set("query", queryParam);
        
        ObjectNode topKParam = objectMapper.createObjectNode();
        topKParam.put("type", "integer");
        topKParam.put("description", "返回的文档数量，默认3");
        topKParam.put("default", 3);
        properties.set("top_k", topKParam);
        
        params.set("properties", properties);
        params.putArray("required").add("query");
        
        return params;
    }

    @Override
    public String execute(JsonNode arguments) {
        try {
            // 获取参数
            String query = arguments.get("query").asText();
            int topK = arguments.has("top_k") ? arguments.get("top_k").asInt() : 3;
            
            log.info("执行 RAG 查询: {}, 返回数量: {}", query, topK);
            
            // 执行语义搜索（这里使用模拟的向量搜索）
            List<Document> results = vectorStoreService.similaritySearch(query, topK);
            
            // 构建返回结果
            StringBuilder sb = new StringBuilder();
            sb.append("{\"query\": \"").append(query).append("\", ");
            sb.append("\"results\": [");
            
            for (int i = 0; i < results.size(); i++) {
                Document doc = results.get(i);
                if (i > 0) sb.append(", ");
                sb.append("{");
                sb.append("\"title\": \"").append(doc.getTitle() != null ? doc.getTitle() : "未知标题").append("\", ");
                sb.append("\"source\": \"").append(doc.getSource() != null ? doc.getSource() : "未知来源").append("\", ");
                sb.append("\"content\": \"").append(truncateContent(doc.getContent())).append("\"");
                sb.append("}");
            }
            
            sb.append("]}");
            
            return sb.toString();
            
        } catch (Exception e) {
            log.error("RAG 查询失败", e);
            return "{\"error\": \"" + e.getMessage() + "\"}";
        }
    }

    /**
     * 截断过长的内容
     */
    private String truncateContent(String content) {
        if (content == null) return "";
        // 转义双引号和换行符
        String escaped = content.replace("\"", "\\\"").replace("\n", "\\n").replace("\r", "\\r");
        // 限制长度
        return escaped.length() > 500 ? escaped.substring(0, 500) + "..." : escaped;
    }
}
