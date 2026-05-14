package com.example.aigent.tools;

import com.example.aigent.model.Tool;
import com.example.aigent.service.VectorStoreService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

/**
 * 文档加载工具
 * 用于将文档加载到向量存储中，支持文本文件和 Markdown 文件
 */
@Slf4j
@Component
public class DocumentLoaderTool implements Tool {

    private final VectorStoreService vectorStoreService;
    private final ObjectMapper objectMapper;

    public DocumentLoaderTool(VectorStoreService vectorStoreService, ObjectMapper objectMapper) {
        this.vectorStoreService = vectorStoreService;
        this.objectMapper = objectMapper;
    }

    @Override
    public String getName() {
        return "load_document";
    }

    @Override
    public String getDescription() {
        return "将文档加载到知识库中。支持 .txt、.md、.json 格式的文件。参数：file_path（文件路径）、title（文档标题，可选）";
    }

    @Override
    public JsonNode getParameters() {
        ObjectNode params = objectMapper.createObjectNode();
        params.put("type", "object");
        
        ObjectNode properties = objectMapper.createObjectNode();
        
        ObjectNode pathParam = objectMapper.createObjectNode();
        pathParam.put("type", "string");
        pathParam.put("description", "要加载的文件路径");
        properties.set("file_path", pathParam);
        
        ObjectNode titleParam = objectMapper.createObjectNode();
        titleParam.put("type", "string");
        titleParam.put("description", "文档标题（可选，默认使用文件名）");
        properties.set("title", titleParam);
        
        params.set("properties", properties);
        params.putArray("required").add("file_path");
        
        return params;
    }

    @Override
    public String execute(JsonNode arguments) {
        try {
            // 获取参数
            String filePath = arguments.get("file_path").asText();
            String title = arguments.has("title") ? arguments.get("title").asText() : null;
            
            log.info("加载文档: {}", filePath);
            
            // 读取文件内容
            Path path = Paths.get(filePath);
            if (!Files.exists(path)) {
                return "{\"error\": \"文件不存在: " + filePath + "\"}";
            }
            
            String content = Files.readString(path);
            
            // 如果没有指定标题，使用文件名
            if (title == null || title.isEmpty()) {
                title = path.getFileName().toString();
            }
            
            // 生成模拟向量（实际应用中应调用 Embedding API）
            String embedding = generateMockEmbedding(content);
            
            // 存储到向量数据库
            vectorStoreService.storeDocument(content, title, filePath, embedding);
            
            int docCount = vectorStoreService.getDocumentCount();
            
            return "{\"status\": \"success\", \"message\": \"文档加载成功\", " +
                   "\"title\": \"" + title + "\", " +
                   "\"content_length\": " + content.length() + ", " +
                   "\"total_documents\": " + docCount + "}";
            
        } catch (IOException e) {
            log.error("加载文档失败", e);
            return "{\"error\": \"加载文档失败: " + e.getMessage() + "\"}";
        }
    }

    /**
     * 生成模拟向量（实际应用中应调用 OpenAI Embedding API）
     */
    private String generateMockEmbedding(String content) {
        // 生成一个简单的模拟向量
        int hash = content.hashCode();
        StringBuilder sb = new StringBuilder("[");
        for (int i = 0; i < 10; i++) {
            if (i > 0) sb.append(", ");
            sb.append((hash + i * 1000) % 1000 / 1000.0);
        }
        sb.append("]");
        return sb.toString();
    }
}
