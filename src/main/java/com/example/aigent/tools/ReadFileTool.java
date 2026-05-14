package com.example.aigent.tools;

import com.example.aigent.model.Tool;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.FileReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

/**
 * 文件读取工具
 * 
 * 支持大文件分块读取和内容搜索
 */
@Slf4j
@Component
public class ReadFileTool implements Tool {

    private final ObjectMapper objectMapper;
    
    private static final int CHUNK_SIZE = 4000;
    private static final int MAX_CHUNKS = 5;

    public ReadFileTool(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    public String getName() {
        return "read_file";
    }

    @Override
    public String getDescription() {
        return "读取文件内容。参数：file_path（文件路径），search_text（可选，搜索内容），chunk_size（可选，分块大小，默认4000字符）";
    }

    @Override
    public JsonNode getParameters() {
        ObjectNode properties = objectMapper.createObjectNode();
        
        ObjectNode filePathProp = objectMapper.createObjectNode();
        filePathProp.put("type", "string");
        filePathProp.put("description", "文件路径");
        
        ObjectNode searchTextProp = objectMapper.createObjectNode();
        searchTextProp.put("type", "string");
        searchTextProp.put("description", "搜索关键词（可选）");
        
        ObjectNode chunkSizeProp = objectMapper.createObjectNode();
        chunkSizeProp.put("type", "integer");
        chunkSizeProp.put("description", "分块大小（可选）");
        
        ObjectNode params = objectMapper.createObjectNode();
        params.put("type", "object");
        params.set("properties", properties);
        
        properties.set("file_path", filePathProp);
        properties.set("search_text", searchTextProp);
        properties.set("chunk_size", chunkSizeProp);
        
        ObjectNode required = objectMapper.createObjectNode();
        params.putArray("required").add("file_path");
        
        return params;
    }

    @Override
    public String execute(JsonNode arguments) {
        String filePath = arguments.has("file_path") ? arguments.get("file_path").asText() : "";
        String searchText = arguments.has("search_text") ? arguments.get("search_text").asText() : null;
        int chunkSize = arguments.has("chunk_size") ? arguments.get("chunk_size").asInt() : CHUNK_SIZE;
        
        if (filePath.isEmpty()) {
            return "{\"error\": \"文件路径不能为空\"}";
        }
        
        try {
            Path path = Paths.get(filePath);
            
            if (!Files.exists(path)) {
                return "{\"error\": \"文件不存在: " + filePath + "\"}";
            }
            
            if (searchText != null && !searchText.isEmpty()) {
                return searchInFile(path, searchText, chunkSize);
            }
            
            return readFileChunks(path, chunkSize);
            
        } catch (Exception e) {
            log.error("读取文件失败: {}", e.getMessage());
            return "{\"error\": \"读取文件失败: " + e.getMessage() + "\"}";
        }
    }

    private String readFileChunks(Path path, int chunkSize) throws Exception {
        List<String> chunks = new ArrayList<>();
        StringBuilder currentChunk = new StringBuilder();
        
        try (BufferedReader reader = new BufferedReader(new FileReader(path.toFile()))) {
            String line;
            while ((line = reader.readLine()) != null && chunks.size() < MAX_CHUNKS) {
                if (currentChunk.length() + line.length() > chunkSize) {
                    chunks.add(currentChunk.toString());
                    currentChunk = new StringBuilder();
                }
                currentChunk.append(line).append("\n");
            }
            
            if (currentChunk.length() > 0 && chunks.size() < MAX_CHUNKS) {
                chunks.add(currentChunk.toString());
            }
        }
        
        return objectMapper.writeValueAsString(java.util.Map.of(
            "file_path", path.toString(),
            "total_chunks", chunks.size(),
            "content", String.join("\n---CHUNK---\n", chunks)
        ));
    }

    private String searchInFile(Path path, String searchText, int chunkSize) throws Exception {
        List<String> matches = new ArrayList<>();
        StringBuilder currentChunk = new StringBuilder();
        int lineNum = 0;
        
        try (BufferedReader reader = new BufferedReader(new FileReader(path.toFile()))) {
            String line;
            while ((line = reader.readLine()) != null && matches.size() < 10) {
                lineNum++;
                if (line.toLowerCase().contains(searchText.toLowerCase())) {
                    matches.add("第" + lineNum + "行: " + line);
                }
                
                if (currentChunk.length() + line.length() > chunkSize) {
                    if (matches.isEmpty() && currentChunk.length() > 0) {
                        matches.add("上下文片段: " + currentChunk.toString().substring(0, Math.min(200, currentChunk.length())));
                    }
                    currentChunk = new StringBuilder();
                }
                currentChunk.append(line).append("\n");
            }
        }
        
        if (matches.isEmpty()) {
            return "{\"file_path\": \"" + path.toString() + "\", \"result\": \"未找到匹配内容\"}";
        }
        
        return objectMapper.writeValueAsString(java.util.Map.of(
            "file_path", path.toString(),
            "search_term", searchText,
            "matches", matches
        ));
    }
}
