package com.example.aigent.agent;

import com.example.aigent.model.*;
import com.example.aigent.service.ConversationHistory;
import com.example.aigent.service.OpenAIService;
import com.example.aigent.service.ToolRegistry;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * ReAct Agent核心实现
 * 
 * 实现ReAct（Reasoning-Action）框架，包含：
 * 1. 推理阶段：分析用户请求，决定是否需要调用工具
 * 2. 行动阶段：执行工具调用
 * 3. 总结阶段：基于工具执行结果生成最终回答
 */
@Slf4j
@Component
public class ReActAgent {

    private final OpenAIService openAIService;
    private final ToolRegistry toolRegistry;
    private final ConversationHistory conversationHistory;
    private final ObjectMapper objectMapper;
    
    @Value("${app.agent.max-tool-calls:5}")
    private int maxToolCalls;

    public ReActAgent(OpenAIService openAIService, 
                      ToolRegistry toolRegistry,
                      ConversationHistory conversationHistory,
                      ObjectMapper objectMapper) {
        this.openAIService = openAIService;
        this.toolRegistry = toolRegistry;
        this.conversationHistory = conversationHistory;
        this.objectMapper = objectMapper;
    }

    /**
     * ReAct框架的系统提示词
     */
    private static final String REACT_SYSTEM_PROMPT = """
        你是一个AI Agent，具备使用工具的能力。请按照以下ReAct框架进行思考和行动：
        
        思考过程格式：
        1. 分析用户的问题或请求
        2. 决定是否需要调用工具
        3. 如果需要，选择合适的工具并执行
        4. 根据工具返回结果进行总结
        
        可用工具：
        {tool_descriptions}
        
        注意：
        - 如果问题可以直接回答，不需要调用工具
        - 如果需要实时信息或外部数据，使用工具
        - 工具执行后，根据结果进行总结回答
        """;

    /**
     * 执行Agent对话循环
     * @param conversationId 对话ID（可为null，会自动创建）
     * @param userInput 用户输入
     * @return AgentResponse
     */
    public AgentResponse execute(String conversationId, String userInput) {
        long startTime = System.currentTimeMillis();
        List<String> thoughts = new ArrayList<>();
        List<String> toolsUsed = new ArrayList<>();
        
        try {
            if (conversationId == null || conversationId.isEmpty()) {
                conversationId = conversationHistory.createConversation();
                log.info("创建新对话: {}", conversationId);
            }
            
            if (!conversationHistory.exists(conversationId)) {
                conversationHistory.createConversation();
            }
            
            conversationHistory.addMessage(conversationId, Message.user(userInput));
            thoughts.add("用户输入: " + userInput);
            
            List<Message> messages = buildMessages(conversationId);
            
            int toolCallCount = 0;
            boolean continueLoop = true;
            
            while (continueLoop && toolCallCount < maxToolCalls) {
                ToolCall toolCall = openAIService.chatCompletionWithTools(messages, 
                        toolRegistry.getToolsAsJsonSchema());
                
                if (toolCall.isValid()) {
                    thoughts.add("思考: 需要调用工具 " + toolCall.getToolName());
                    log.info("工具调用: {} ({})", toolCall.getToolName(), toolCall.getArguments());
                    
                    String toolResult = executeTool(toolCall);
                    toolsUsed.add(toolCall.getToolName());
                    thoughts.add("工具返回: " + truncate(toolResult, 200));
                    
                    messages.add(Message.assistant("调用工具: " + toolCall.getToolName()));
                    messages.add(Message.tool(toolCall.getToolName(), toolResult, toolCall.getId()));
                    conversationHistory.addMessage(conversationId, 
                            Message.assistant("调用工具: " + toolCall.getToolName()));
                    conversationHistory.addMessage(conversationId, 
                            Message.tool(toolCall.getToolName(), toolResult, toolCall.getId()));
                    
                    toolCallCount++;
                } else {
                    continueLoop = false;
                }
            }
            
            String finalAnswer = openAIService.chatCompletion(messages);
            thoughts.add("总结回答: " + truncate(finalAnswer, 200));
            
            conversationHistory.addMessage(conversationId, Message.assistant(finalAnswer));
            
            long responseTime = System.currentTimeMillis() - startTime;
            
            return AgentResponse.success(conversationId, finalAnswer, thoughts, toolsUsed, responseTime);
            
        } catch (Exception e) {
            log.error("Agent执行失败: {}", e.getMessage(), e);
            return AgentResponse.error(e.getMessage());
        }
    }

    /**
     * 构建消息列表（包含系统提示词）
     */
    private List<Message> buildMessages(String conversationId) {
        List<Message> messages = new ArrayList<>();
        
        String toolDescriptions = String.join("\n", toolRegistry.getToolDescriptions());
        String systemPrompt = REACT_SYSTEM_PROMPT.replace("{tool_descriptions}", toolDescriptions);
        
        messages.add(Message.system(systemPrompt));
        messages.addAll(conversationHistory.getMessages(conversationId));
        
        return messages;
    }

    /**
     * 执行工具调用
     */
    private String executeTool(ToolCall toolCall) {
        Tool tool = toolRegistry.getTool(toolCall.getToolName());
        
        if (tool == null) {
            return "{\"error\": \"未知工具: " + toolCall.getToolName() + "\"}";
        }
        
        try {
            JsonNode arguments = objectMapper.readTree(toolCall.getArguments());
            return tool.execute(arguments);
        } catch (Exception e) {
            log.error("工具执行失败: {}", e.getMessage());
            return "{\"error\": \"工具执行失败: " + e.getMessage() + "\"}";
        }
    }

    /**
     * 截断字符串
     */
    private String truncate(String text, int maxLength) {
        if (text == null) return "";
        return text.length() > maxLength ? text.substring(0, maxLength) + "..." : text;
    }
}
