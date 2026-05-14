package com.example.aigent.tools;

import com.example.aigent.model.Tool;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * 目录列表工具
 * 
 * 使用Java NIO列出指定目录下的文件和文件夹
 */
@Slf4j
@Component
public class LSRepoTool implements Tool {

    private final ObjectMapper objectMapper;

    public LSRepoTool(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    public String getName() {
        return "list_directory";
    }

    @Override
    public String getDescription() {
        return "列出指定目录下的文件和文件夹。参数：path（目录路径，默认当前目录）";
    }

    @Override
    public JsonNode getParameters() {
        ObjectNode properties = objectMapper.createObjectNode();
        
        ObjectNode pathProp = objectMapper.createObjectNode();
        pathProp.put("type", "string");
        pathProp.put("description", "目录路径");
        
        ObjectNode params = objectMapper.createObjectNode();
        params.put("type", "object");
        params.set("properties", properties);
        properties.set("path", pathProp);
        
        return params;
    }

    @Override
    public String execute(JsonNode arguments) {
        String pathStr = arguments.has("path") ? arguments.get("path").asText() : ".";
        
        try {
            Path path = Paths.get(pathStr).toAbsolutePath().normalize();
            
            if (!Files.exists(path)) {
                return "{\"error\": \"路径不存在: " + pathStr + "\"}";
            }
            
            if (!Files.isDirectory(path)) {
                return "{\"error\": \"" + pathStr + "\" 不是目录}";
            }
            
            List<FileInfo> files = new ArrayList<>();
            
            Files.list(path)
                .sorted(Comparator.comparing(p -> !Files.isDirectory(p)))
                .forEach(p -> {
                    File file = p.toFile();
                    files.add(new FileInfo(
                        file.getName(),
                        Files.isDirectory(p),
                        file.length(),
                        file.lastModified()
                    ));
                });
            
            ObjectNode result = objectMapper.createObjectNode();
            result.put("directory", path.toString());
            result.put("total_items", files.size());
            
            ArrayNode itemsArray = objectMapper.createArrayNode();
            files.forEach(info -> {
                ObjectNode item = objectMapper.createObjectNode();
                item.put("name", info.name);
                item.put("is_directory", info.isDirectory);
                item.put("size", info.size);
                item.put("last_modified", info.lastModified);
                itemsArray.add(item);
            });
            result.set("items", itemsArray);
            
            return objectMapper.writeValueAsString(result);
            
        } catch (Exception e) {
            log.error("列出目录失败: {}", e.getMessage());
            return "{\"error\": \"列出目录失败: " + e.getMessage() + "\"}";
        }
    }

    private record FileInfo(String name, boolean isDirectory, long size, long lastModified) {}
}
