package com.example.aigent.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 对话消息模型
 * 
 * 表示对话中的一条消息，包含角色和内容
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Message {
    
    /**
     * 消息角色：system, user, assistant, tool
     */
    private String role;
    
    /**
     * 消息内容
     */
    private String content;
    
    /**
     * 工具调用相关的名称（当role为tool时使用）
     */
    @JsonProperty("name")
    private String toolName;
    
    /**
     * 工具调用的ID（当role为assistant且包含tool_call时使用）
     */
    @JsonProperty("tool_call_id")
    private String toolCallId;
    
    /**
     * 创建用户消息
     * @param content 用户输入
     * @return Message实例
     */
    public static Message user(String content) {
        return Message.builder()
                .role("user")
                .content(content)
                .build();
    }
    
    /**
     * 创建助手消息
     * @param content 助手回复
     * @return Message实例
     */
    public static Message assistant(String content) {
        return Message.builder()
                .role("assistant")
                .content(content)
                .build();
    }
    
    /**
     * 创建工具消息（工具执行结果）
     * @param toolName 工具名称
     * @param content 工具执行结果
     * @param toolCallId 工具调用ID
     * @return Message实例
     */
    public static Message tool(String toolName, String content, String toolCallId) {
        return Message.builder()
                .role("tool")
                .toolName(toolName)
                .content(content)
                .toolCallId(toolCallId)
                .build();
    }
    
    /**
     * 创建系统消息
     * @param content 系统提示词
     * @return Message实例
     */
    public static Message system(String content) {
        return Message.builder()
                .role("system")
                .content(content)
                .build();
    }
}
