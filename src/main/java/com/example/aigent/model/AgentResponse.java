package com.example.aigent.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Agent响应模型
 * 
 * 封装Agent的完整响应，包含回答内容和相关元数据
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AgentResponse {
    
    /**
     * 会话ID
     */
    private String conversationId;
    
    /**
     * Agent的最终回答
     */
    private String answer;
    
    /**
     * 思考过程列表（ReAct框架的推理步骤）
     */
    private List<String> thoughts;
    
    /**
     * 调用的工具列表
     */
    private List<String> toolsUsed;
    
    /**
     * 是否调用了工具
     */
    private boolean toolUsed;
    
    /**
     * 响应耗时（毫秒）
     */
    private long responseTimeMs;
    
    /**
     * 创建成功响应
     * @param conversationId 会话ID
     * @param answer 回答内容
     * @param thoughts 思考过程
     * @param toolsUsed 使用的工具
     * @param responseTimeMs 响应时间
     * @return AgentResponse实例
     */
    public static AgentResponse success(String conversationId, String answer, 
                                       List<String> thoughts, List<String> toolsUsed, 
                                       long responseTimeMs) {
        return AgentResponse.builder()
                .conversationId(conversationId)
                .answer(answer)
                .thoughts(thoughts)
                .toolsUsed(toolsUsed)
                .toolUsed(!toolsUsed.isEmpty())
                .responseTimeMs(responseTimeMs)
                .build();
    }
    
    /**
     * 创建错误响应
     * @param errorMessage 错误信息
     * @return AgentResponse实例
     */
    public static AgentResponse error(String errorMessage) {
        return AgentResponse.builder()
                .answer("Error: " + errorMessage)
                .toolUsed(false)
                .responseTimeMs(0)
                .build();
    }
}
