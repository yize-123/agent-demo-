package com.example.aigent.service;

import com.example.aigent.model.Conversation;
import com.example.aigent.model.Message;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 对话历史管理服务
 * 
 * 负责管理多个对话会话的生命周期
 */
@Slf4j
@Service
public class ConversationHistory {
    
    private final Map<String, Conversation> conversations = new ConcurrentHashMap<>();
    
    @Value("${app.agent.max-history-length:20}")
    private int maxHistoryLength;

    /**
     * 创建新对话
     * @return 新对话ID
     */
    public String createConversation() {
        String id = UUID.randomUUID().toString();
        Conversation conversation = Conversation.builder()
                .id(id)
                .build();
        conversations.put(id, conversation);
        
        log.info("创建新对话: {}", id);
        return id;
    }

    /**
     * 获取对话
     * @param conversationId 对话ID
     * @return Conversation实例，如果不存在返回null
     */
    public Conversation getConversation(String conversationId) {
        return conversations.get(conversationId);
    }

    /**
     * 添加消息到对话
     * @param conversationId 对话ID
     * @param message 消息
     */
    public void addMessage(String conversationId, Message message) {
        Conversation conversation = conversations.get(conversationId);
        if (conversation != null) {
            conversation.addMessage(message);
            conversation.trimHistory(maxHistoryLength);
            log.debug("消息已添加到对话 {}: {}", conversationId, message.getRole());
        }
    }

    /**
     * 获取对话消息列表
     * @param conversationId 对话ID
     * @return 消息列表
     */
    public List<Message> getMessages(String conversationId) {
        Conversation conversation = conversations.get(conversationId);
        return conversation != null ? conversation.getMessages() : List.of();
    }

    /**
     * 删除对话
     * @param conversationId 对话ID
     * @return true表示删除成功
     */
    public boolean deleteConversation(String conversationId) {
        return conversations.remove(conversationId) != null;
    }

    /**
     * 检查对话是否存在
     * @param conversationId 对话ID
     * @return true表示存在
     */
    public boolean exists(String conversationId) {
        return conversations.containsKey(conversationId);
    }

    /**
     * 获取所有对话ID
     * @return 对话ID列表
     */
    public List<String> getAllConversationIds() {
        return List.copyOf(conversations.keySet());
    }

    /**
     * 获取对话数量
     * @return 对话数量
     */
    public int getConversationCount() {
        return conversations.size();
    }
}
