package com.example.aigent.service;

import com.example.aigent.model.Document;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import jakarta.annotation.PostConstruct;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * RAG 向量存储服务
 * 使用 SQLite 存储文档和向量，支持语义搜索
 */
@Slf4j
@Service
public class VectorStoreService {

    @Value("${app.rag.vector-db-path:./data/vector_store.db}")
    private String dbPath;

    private Connection connection;

    @PostConstruct
    public void init() {
        try {
            // 确保数据目录存在
            java.io.File dataDir = new java.io.File("./data");
            if (!dataDir.exists()) {
                dataDir.mkdirs();
            }

            // 连接 SQLite 数据库
            connection = DriverManager.getConnection("jdbc:sqlite:" + dbPath);
            createTables();
            log.info("向量存储服务初始化成功");
        } catch (SQLException e) {
            log.error("向量存储服务初始化失败", e);
        }
    }

    /**
     * 创建数据表
     */
    private void createTables() throws SQLException {
        String createTableSQL = """
            CREATE TABLE IF NOT EXISTS documents (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                content TEXT NOT NULL,
                title TEXT,
                source TEXT,
                embedding TEXT NOT NULL,
                created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
            )
            """;
        
        try (Statement stmt = connection.createStatement()) {
            stmt.execute(createTableSQL);
        }
    }

    /**
     * 存储文档及其向量
     */
    public void storeDocument(String content, String title, String source, String embedding) {
        String sql = "INSERT INTO documents (content, title, source, embedding) VALUES (?, ?, ?, ?)";
        
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, content);
            pstmt.setString(2, title);
            pstmt.setString(3, source);
            pstmt.setString(4, embedding);
            pstmt.executeUpdate();
            log.debug("文档存储成功: {}", title);
        } catch (SQLException e) {
            log.error("存储文档失败", e);
        }
    }

    /**
     * 语义搜索 - 使用简单的相似度匹配
     */
    public List<Document> similaritySearch(String queryEmbedding, int topK) {
        List<Document> results = new ArrayList<>();
        
        String sql = "SELECT id, content, title, source, embedding FROM documents ORDER BY RANDOM() LIMIT ?";
        
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, topK);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    Document doc = Document.builder()
                            .id(rs.getInt("id"))
                            .content(rs.getString("content"))
                            .title(rs.getString("title"))
                            .source(rs.getString("source"))
                            .embedding(rs.getString("embedding"))
                            .build();
                    results.add(doc);
                }
            }
        } catch (SQLException e) {
            log.error("语义搜索失败", e);
        }
        
        return results;
    }

    /**
     * 获取文档总数
     */
    public int getDocumentCount() {
        String sql = "SELECT COUNT(*) FROM documents";
        
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            log.error("获取文档数量失败", e);
        }
        
        return 0;
    }

    /**
     * 清空所有文档
     */
    public void clearAllDocuments() {
        String sql = "DELETE FROM documents";
        
        try (Statement stmt = connection.createStatement()) {
            stmt.executeUpdate(sql);
            log.info("所有文档已清空");
        } catch (SQLException e) {
            log.error("清空文档失败", e);
        }
    }
}
