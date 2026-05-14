package com.example.aigent.service;

import com.example.aigent.model.Tool;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 工具注册器
 * 
 * 负责管理所有可用工具的注册、查找和转换为OpenAI格式
 */
@Slf4j
@Service
public class ToolRegistry {
    
    private final Map<String, Tool> tools = new ConcurrentHashMap<>();
    private final ObjectMapper objectMapper;

    /**
     * 通过Spring IoC自动注入所有Tool实现
     * @param toolList 所有实现Tool接口的Bean
     * @param objectMapper ObjectMapper实例
     */
    public ToolRegistry(List<Tool> toolList, ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
        
        for (Tool tool : toolList) {
            tools.put(tool.getName(), tool);
            log.info("工具已注册: {}", tool.getName());
        }
        
        log.info("共注册了 {} 个工具", tools.size());
    }
    
    /**
     * 根据名称获取工具
     * @param name 工具名称
     * @return Tool实例，如果不存在返回null
     */
    public Tool getTool(String name) {
        return tools.get(name);
    }
    
    /**
     * 获取所有已注册工具
     * @return 工具列表
     */
    public List<Tool> getAllTools() {
        return List.copyOf(tools.values());
    }
    
    /**
     * 检查工具是否已注册
     * @param name 工具名称
     * @return true表示已注册
     */
    public boolean hasTool(String name) {
        return tools.containsKey(name);
    }
    
    /**
     * 获取工具数量
     * @return 工具数量
     */
    public int getToolCount() {
        return tools.size();
    }
    
    /**
     * 将所有工具转换为OpenAI工具调用格式
     * @return ArrayNode包含所有工具的JSON Schema
     */
    public ArrayNode getToolsAsJsonSchema() {
        ArrayNode toolsArray = objectMapper.createArrayNode();
        
        for (Tool tool : tools.values()) {
            toolsArray.add(tool.toOpenAITool(objectMapper));
        }
        
        return toolsArray;
    }
    
    /**
     * 获取工具描述信息列表
     * @return 工具描述列表
     */
    public List<String> getToolDescriptions() {
        return tools.values().stream()
                .map(t -> t.getName() + ": " + t.getDescription())
                .toList();
    }
}
