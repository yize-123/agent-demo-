package com.example.aigent.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * RAG 文档模型
 * 用于存储和检索文档内容
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Document {
    
    /**
     * 文档ID
     */
    private Integer id;
    
    /**
     * 文档内容
     */
    private String content;
    
    /**
     * 文档标题
     */
    private String title;
    
    /**
     * 文档来源（文件路径、URL等）
     */
    private String source;
    
    /**
     * 文档向量（JSON格式存储）
     */
    private String embedding;
    
    /**
     * 相似度分数（用于搜索结果排序）
     */
    private Double similarityScore;
}
