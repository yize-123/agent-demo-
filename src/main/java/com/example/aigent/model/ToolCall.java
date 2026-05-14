package com.example.aigent.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 工具调用模型
 * 
 * 封装LLM返回的工具调用信息
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ToolCall {
    
    /**
     * 工具调用ID
     */
    private String id;
    
    /**
     * 工具名称
     */
    private String toolName;
    
    /**
     * 工具参数（JSON字符串）
     */
    private String arguments;
    
    /**
     * 判断是否为有效工具调用
     * @return true表示有效
     */
    public boolean isValid() {
        return toolName != null && !toolName.isEmpty();
    }
}
