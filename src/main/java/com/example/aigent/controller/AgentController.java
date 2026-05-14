package com.example.aigent.controller;

import com.example.aigent.agent.ReActAgent;
import com.example.aigent.model.AgentResponse;
import com.example.aigent.service.ConversationHistory;
import com.example.aigent.service.ToolRegistry;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Agent REST API控制器
 * 
 * 提供与AI Agent交互的REST端点
 */
@Slf4j
@RestController
@RequestMapping("/api/agent")
public class AgentController {

    private final ReActAgent reActAgent;
    private final ToolRegistry toolRegistry;
    private final ConversationHistory conversationHistory;

    public AgentController(ReActAgent reActAgent, 
                          ToolRegistry toolRegistry,
                          ConversationHistory conversationHistory) {
        this.reActAgent = reActAgent;
        this.toolRegistry = toolRegistry;
        this.conversationHistory = conversationHistory;
    }

    /**
     * 聊天端点
     * POST /api/agent/chat
     * 
     * 与Agent进行对话，支持多轮对话
     * @param request ChatRequest包含对话ID和用户输入
     * @return AgentResponse
     */
    @PostMapping("/chat")
    public ResponseEntity<AgentResponse> chat(@RequestBody ChatRequest request) {
        log.info("收到聊天请求: conversationId={}, message={}", 
                request.getConversationId(), request.getMessage());
        
        AgentResponse response = reActAgent.execute(
                request.getConversationId(), 
                request.getMessage()
        );
        
        return ResponseEntity.ok(response);
    }

    /**
     * 获取可用工具列表
     * GET /api/agent/tools
     * @return 工具列表
     */
    @GetMapping("/tools")
    public ResponseEntity<Map<String, Object>> getTools() {
        Map<String, Object> result = new HashMap<>();
        result.put("count", toolRegistry.getToolCount());
        result.put("tools", toolRegistry.getToolDescriptions());
        return ResponseEntity.ok(result);
    }

    /**
     * 获取所有对话列表
     * GET /api/agent/conversations
     * @return 对话列表
     */
    @GetMapping("/conversations")
    public ResponseEntity<Map<String, Object>> getConversations() {
        Map<String, Object> result = new HashMap<>();
        result.put("count", conversationHistory.getConversationCount());
        result.put("conversation_ids", conversationHistory.getAllConversationIds());
        return ResponseEntity.ok(result);
    }

    /**
     * 删除对话
     * DELETE /api/agent/conversations/{id}
     * @param id 对话ID
     * @return 删除结果
     */
    @DeleteMapping("/conversations/{id}")
    public ResponseEntity<Map<String, Object>> deleteConversation(@PathVariable String id) {
        boolean deleted = conversationHistory.deleteConversation(id);
        
        Map<String, Object> result = new HashMap<>();
        result.put("success", deleted);
        result.put("message", deleted ? "对话已删除" : "对话不存在");
        
        return ResponseEntity.ok(result);
    }

    /**
     * 健康检查端点
     * GET /api/agent/health
     * @return 健康状态
     */
    @GetMapping("/health")
    public ResponseEntity<Map<String, Object>> health() {
        Map<String, Object> result = new HashMap<>();
        result.put("status", "UP");
        result.put("service", "AI Agent Demo");
        result.put("tool_count", toolRegistry.getToolCount());
        result.put("conversation_count", conversationHistory.getConversationCount());
        return ResponseEntity.ok(result);
    }

    /**
     * 聊天请求模型
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ChatRequest {
        /**
         * 对话ID（可选，不传则创建新对话）
         */
        private String conversationId;
        
        /**
         * 用户消息
         */
        private String message;
    }
}
