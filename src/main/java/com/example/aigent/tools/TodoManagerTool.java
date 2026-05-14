package com.example.aigent.tools;

import com.example.aigent.model.Tool;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 待办事项管理工具
 * 
 * 使用内存存储管理待办事项，支持添加、删除、列出和标记完成功能
 */
@Slf4j
@Component
public class TodoManagerTool implements Tool {

    private final ObjectMapper objectMapper;
    private final ConcurrentMap<Integer, TodoItem> todoStore = new ConcurrentHashMap<>();
    private final AtomicInteger idCounter = new AtomicInteger(1);

    public TodoManagerTool(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    public String getName() {
        return "manage_todo";
    }

    @Override
    public String getDescription() {
        return "管理待办事项。参数：action（操作类型：add/delete/list/complete），task（任务内容，add时必填），id（任务ID，delete/complete时必填）";
    }

    @Override
    public JsonNode getParameters() {
        ObjectNode properties = objectMapper.createObjectNode();
        
        ObjectNode actionProp = objectMapper.createObjectNode();
        actionProp.put("type", "string");
        actionProp.put("description", "操作类型：add/delete/list/complete");
        
        ObjectNode taskProp = objectMapper.createObjectNode();
        taskProp.put("type", "string");
        taskProp.put("description", "任务内容");
        
        ObjectNode idProp = objectMapper.createObjectNode();
        idProp.put("type", "integer");
        idProp.put("description", "任务ID");
        
        ObjectNode params = objectMapper.createObjectNode();
        params.put("type", "object");
        params.set("properties", properties);
        properties.set("action", actionProp);
        properties.set("task", taskProp);
        properties.set("id", idProp);
        
        return params;
    }

    @Override
    public String execute(JsonNode arguments) {
        String action = arguments.has("action") ? arguments.get("action").asText() : "";
        
        return switch (action.toLowerCase()) {
            case "add" -> addTodo(arguments);
            case "delete" -> deleteTodo(arguments);
            case "list" -> listTodos();
            case "complete" -> completeTodo(arguments);
            default -> "{\"error\": \"未知操作: " + action + "\"}";
        };
    }

    private String addTodo(JsonNode arguments) {
        String task = arguments.has("task") ? arguments.get("task").asText() : "";
        
        if (task.isEmpty()) {
            return "{\"error\": \"任务内容不能为空\"}";
        }
        
        int id = idCounter.getAndIncrement();
        todoStore.put(id, new TodoItem(id, task, false));
        
        return "{\"status\": \"success\", \"message\": \"添加成功\", \"id\": " + id + "}";
    }

    private String deleteTodo(JsonNode arguments) {
        int id = arguments.has("id") ? arguments.get("id").asInt() : -1;
        
        if (id <= 0) {
            return "{\"error\": \"无效的任务ID\"}";
        }
        
        if (todoStore.remove(id) != null) {
            return "{\"status\": \"success\", \"message\": \"删除成功\"}";
        } else {
            return "{\"error\": \"任务不存在\"}";
        }
    }

    private String listTodos() {
        List<TodoItem> items = new ArrayList<>(todoStore.values());
        items.sort((a, b) -> Integer.compare(a.id, b.id));
        
        ObjectNode result = objectMapper.createObjectNode();
        result.put("total", items.size());
        
        ArrayNode todosArray = objectMapper.createArrayNode();
        items.forEach(item -> {
            ObjectNode todo = objectMapper.createObjectNode();
            todo.put("id", item.id);
            todo.put("task", item.task);
            todo.put("completed", item.completed);
            todosArray.add(todo);
        });
        result.set("todos", todosArray);
        
        try {
            return objectMapper.writeValueAsString(result);
        } catch (Exception e) {
            log.error("序列化失败: {}", e.getMessage());
            return "{\"error\": \"序列化失败\"}";
        }
    }

    private String completeTodo(JsonNode arguments) {
        int id = arguments.has("id") ? arguments.get("id").asInt() : -1;
        
        if (id <= 0) {
            return "{\"error\": \"无效的任务ID\"}";
        }
        
        TodoItem item = todoStore.get(id);
        if (item == null) {
            return "{\"error\": \"任务不存在\"}";
        }
        
        item.completed = true;
        return "{\"status\": \"success\", \"message\": \"已标记完成\"}";
    }

    private static class TodoItem {
        int id;
        String task;
        boolean completed;
        
        TodoItem(int id, String task, boolean completed) {
            this.id = id;
            this.task = task;
            this.completed = completed;
        }
    }
}
