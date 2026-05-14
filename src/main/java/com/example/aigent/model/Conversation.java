package com.example.aigent.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * 对话会话模型
 * 
 * 管理单个对话的完整生命周期，包含消息历史和会话元数据
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Conversation {
    
    /**
     * 会话唯一ID
     */
    private String id;
    
    /**
     * 会话创建时间
     */
    @Builder.Default
    private LocalDateTime createdAt = LocalDateTime.now();
    
    /**
     * 最后更新时间
     */
    @Builder.Default
    private LocalDateTime updatedAt = LocalDateTime.now();
    
    /**
     * 消息历史列表
     */
    @Builder.Default
    private List<Message> messages = new ArrayList<>();
    
    /**
     * 会话标题（可选）
     */
    private String title;
    
    /**
     * 添加消息到会话
     * @param message 消息
     */
    public void addMessage(Message message) {
        this.messages.add(message);
        this.updatedAt = LocalDateTime.now();
    }
    
    /**
     * 获取消息历史（不含系统消息）
     * @return 用户和助手的消息列表
     */
    public List<Message> getHistoryWithoutSystem() {
        return messages.stream()
                .filter(m -> !"system".equals(m.getRole()))
                .toList();
    }
    
    /**
     * 获取消息数量
     * @return 消息数量
     */
    public int getMessageCount() {
        return messages.size();
    }
    
    /**
     * 清理旧消息，保留指定数量的最近消息
     * @param maxHistoryLength 最大历史消息数
     */
    public void trimHistory(int maxHistoryLength) {
        if (messages.size() > maxHistoryLength) {
            messages = new ArrayList<>(messages.subList(
                messages.size() - maxHistoryLength, 
                messages.size()
            ));
        }
    }
}
