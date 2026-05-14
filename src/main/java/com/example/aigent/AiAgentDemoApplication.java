package com.example.aigent;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * AI Agent Demo 启动类
 * 
 * 这是一个完整的AI Agent实践项目，整合了以下核心技术：
 * - RAG (Retrieval-Augmented Generation) - 检索增强生成
 * - MCP (Multi-Collaborative Processing) - 多智能体协作
 * - LLM (Large Language Model) - 大语言模型集成
 * - ReAct (Reasoning-Action) - 推理-行动框架
 * 
 * 使用方法：
 * 1. 设置环境变量 OPENAI_API_KEY
 * 2. 运行: mvn spring-boot:run
 * 3. 访问: http://localhost:8080/api/agent/chat
 */
@SpringBootApplication
public class AiAgentDemoApplication {

    public static void main(String[] args) {
        SpringApplication.run(AiAgentDemoApplication.class, args);
    }
}
