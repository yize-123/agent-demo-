package com.example.aigent.service;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import jakarta.annotation.PostConstruct;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * MCP（多智能体协作）协调服务
 * 负责管理多个智能体之间的协作和任务分发
 */
@Slf4j
@Service
public class MultiAgentCoordinator {

    /**
     * 可用的智能体列表
     */
    private final Map<String, AgentInfo> agents = new ConcurrentHashMap<>();

    /**
     * 正在进行的协作任务
     */
    private final Map<String, CollaborationTask> tasks = new ConcurrentHashMap<>();

    @PostConstruct
    public void init() {
        // 注册内置智能体
        registerAgent(AgentInfo.builder()
                .id("search_agent")
                .name("搜索专家")
                .description("擅长网络搜索和信息收集")
                .capabilities(List.of("web_search", "fetch_webpage"))
                .build());

        registerAgent(AgentInfo.builder()
                .id("document_agent")
                .name("文档专家")
                .description("擅长文档处理和知识检索")
                .capabilities(List.of("read_file", "list_directory", "rag_query"))
                .build());

        registerAgent(AgentInfo.builder()
                .id("todo_agent")
                .name("任务专家")
                .description("擅长任务管理和待办事项")
                .capabilities(List.of("manage_todo"))
                .build());

        registerAgent(AgentInfo.builder()
                .id("math_agent")
                .name("数学专家")
                .description("擅长数学计算")
                .capabilities(List.of("calculator"))
                .build());

        log.info("MCP 协调服务初始化完成，注册了 {} 个智能体", agents.size());
    }

    /**
     * 注册智能体
     */
    public void registerAgent(AgentInfo agent) {
        agents.put(agent.getId(), agent);
        log.info("智能体已注册: {}", agent.getName());
    }

    /**
     * 获取所有智能体信息
     */
    public List<AgentInfo> getAgents() {
        return new ArrayList<>(agents.values());
    }

    /**
     * 根据任务类型选择最合适的智能体
     */
    public AgentInfo selectAgent(String taskDescription) {
        // 简单的匹配逻辑：根据关键词选择智能体
        String lowerDesc = taskDescription.toLowerCase();
        
        if (lowerDesc.contains("搜索") || lowerDesc.contains("网络") || lowerDesc.contains("新闻")) {
            return agents.get("search_agent");
        } else if (lowerDesc.contains("文档") || lowerDesc.contains("文件") || lowerDesc.contains("知识")) {
            return agents.get("document_agent");
        } else if (lowerDesc.contains("待办") || lowerDesc.contains("任务") || lowerDesc.contains("计划")) {
            return agents.get("todo_agent");
        } else if (lowerDesc.contains("计算") || lowerDesc.contains("数学") || lowerDesc.contains("公式")) {
            return agents.get("math_agent");
        }
        
        // 默认返回文档专家
        return agents.get("document_agent");
    }

    /**
     * 创建协作任务
     */
    public String createTask(String description, String requester) {
        String taskId = "task-" + UUID.randomUUID().toString().substring(0, 8);
        
        CollaborationTask task = CollaborationTask.builder()
                .id(taskId)
                .description(description)
                .requester(requester)
                .status("created")
                .createdAt(new Date())
                .build();
        
        tasks.put(taskId, task);
        
        // 选择合适的智能体
        AgentInfo agent = selectAgent(description);
        if (agent != null) {
            task.setAssignedAgent(agent.getId());
            task.setStatus("assigned");
            log.info("任务已分配给智能体 {}: {}", agent.getName(), taskId);
        }
        
        return taskId;
    }

    /**
     * 获取任务状态
     */
    public CollaborationTask getTask(String taskId) {
        return tasks.get(taskId);
    }

    /**
     * 更新任务状态
     */
    public void updateTaskStatus(String taskId, String status, String result) {
        CollaborationTask task = tasks.get(taskId);
        if (task != null) {
            task.setStatus(status);
            task.setResult(result);
            task.setUpdatedAt(new Date());
            log.info("任务状态更新: {} -> {}", taskId, status);
        }
    }

    /**
     * 获取所有任务列表
     */
    public List<CollaborationTask> getAllTasks() {
        return new ArrayList<>(tasks.values());
    }

    /**
     * 智能体信息
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class AgentInfo {
        private String id;
        private String name;
        private String description;
        private List<String> capabilities;
    }

    /**
     * 协作任务
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CollaborationTask {
        private String id;
        private String description;
        private String requester;
        private String assignedAgent;
        private String status;
        private String result;
        private Date createdAt;
        private Date updatedAt;
    }
}
