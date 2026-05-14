package com.example.aigent.model;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

/**
 * 工具接口定义
 * 
 * AI Agent的核心组件之一，定义了Agent可以调用的外部工具
 * 每个工具必须实现execute方法来执行具体功能
 */
public interface Tool {
    
    /**
     * 获取工具名称
     * @return 工具名称
     */
    String getName();
    
    /**
     * 获取工具描述
     * @return 工具描述，用于LLM理解工具用途
     */
    String getDescription();
    
    /**
     * 获取工具参数描述（JSON Schema格式）
     * @return 参数的JSON Schema
     */
    JsonNode getParameters();
    
    /**
     * 执行工具
     * @param arguments 工具参数
     * @return 执行结果
     */
    String execute(JsonNode arguments);
    
    /**
     * 将工具转换为OpenAI工具调用格式
     * @param mapper ObjectMapper实例
     * @return OpenAI工具格式的ObjectNode
     */
    default ObjectNode toOpenAITool(ObjectMapper mapper) {
        ObjectNode toolObject = mapper.createObjectNode();
        toolObject.put("type", "function");
        
        ObjectNode functionObject = mapper.createObjectNode();
        functionObject.put("name", getName());
        functionObject.put("description", getDescription());
        functionObject.set("parameters", getParameters());
        
        toolObject.set("function", functionObject);
        return toolObject;
    }
    
    /**
     * 创建参数JSON Schema的辅助方法
     * @param mapper ObjectMapper实例
     * @param properties 属性定义
     * @param required 必填字段列表
     * @return 参数的JSON Schema
     */
    static ObjectNode createParameters(ObjectMapper mapper, ObjectNode properties, String... required) {
        ObjectNode params = mapper.createObjectNode();
        params.put("type", "object");
        params.set("properties", properties);
        
        ArrayNode requiredArray = mapper.createArrayNode();
        for (String field : required) {
            requiredArray.add(field);
        }
        params.set("required", requiredArray);
        
        return params;
    }
}
