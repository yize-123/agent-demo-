# AI Agent Demo 项目学习指南

---

## 目录

1. [项目概述](#1-项目概述)
   - 1.1 项目简介
   - 1.2 技术栈
   - 1.3 项目结构

2. [环境搭建](#2-环境搭建)
   - 2.1 前置要求
   - 2.2 环境配置
   - 2.3 项目构建

3. [核心模块详解](#3-核心模块详解)
   - 3.1 模型层 (Model)
   - 3.2 工具层 (Tools)
   - 3.3 服务层 (Service)
   - 3.4 Agent层 (Agent)
   - 3.5 控制器层 (Controller)

4. [关键函数详解](#4-关键函数详解)
   - 4.1 Tool接口
   - 4.2 ToolRegistry
   - 4.3 OpenAIService
   - 4.4 ReActAgent
   - 4.5 ConversationHistory

5. [项目流程分析](#5-项目流程分析)
   - 5.1 完整对话流程
   - 5.2 工具调用流程
   - 5.3 数据流向图

6. [实践练习](#6-实践练习)
   - 6.1 基础练习
   - 6.2 进阶练习
   - 6.3 综合练习

7. [常见问题](#7-常见问题)

---

## 1. 项目概述

### 1.1 项目简介

本项目是一个完整的AI Agent实践项目，整合了以下核心技术：

| 技术 | 说明 |
|------|------|
| **ReAct框架** | 推理-行动循环，让AI能够思考并调用工具 |
| **RAG** | 检索增强生成，结合外部知识增强回答能力 |
| **MCP** | 多智能体协作，支持多个工具协同工作 |
| **LLM集成** | OpenAI API集成，支持GPT-3.5/4模型 |

### 1.2 技术栈

```
├── 后端框架: Spring Boot 3.2.5
├── 语言: Java 17
├── AI服务: OpenAI API
├── JSON处理: Jackson
├── 网页解析: Jsoup
├── 前端: HTML5 + CSS3 + JavaScript
└── 容器化: Docker
```

### 1.3 项目结构

```
src/main/java/com/example/aigent/
├── AiAgentDemoApplication.java    # 启动类
├── agent/
│   └── ReActAgent.java            # ReAct框架核心实现
├── controller/
│   └── AgentController.java       # REST API控制器
├── model/
│   ├── AgentResponse.java         # 响应模型
│   ├── Conversation.java          # 对话会话模型
│   ├── Message.java               # 消息模型
│   ├── Tool.java                  # 工具接口
│   └── ToolCall.java              # 工具调用模型
├── service/
│   ├── ConversationHistory.java   # 对话历史管理
│   ├── OpenAIService.java         # OpenAI服务集成
│   └── ToolRegistry.java          # 工具注册器
└── tools/
    ├── FetchFromWebTool.java      # 网页抓取工具
    ├── LSRepoTool.java            # 目录列表工具
    ├── ReadFileTool.java          # 文件读取工具
    ├── TodoManagerTool.java       # 待办管理工具
    └── WebSearchTool.java         # 网页搜索工具
```

---

## 2. 环境搭建

### 2.1 前置要求

| 软件 | 版本 | 说明 |
|------|------|------|
| Java | 17+ | 项目开发语言 |
| Maven | 3.8+ | 项目构建工具 |
| OpenAI API Key | - | AI服务认证 |

### 2.2 环境配置

#### 步骤1: 安装Java 17

```bash
# Windows检查
java -version
# 应显示: java version "17.x.x"

# 如果未安装，从Oracle官网下载安装
# https://www.oracle.com/java/technologies/downloads/#java17
```

#### 步骤2: 安装Maven

```bash
# Windows检查
mvn -version
# 应显示: Apache Maven 3.8.x

# 如果未安装
# 下载地址: https://maven.apache.org/download.cgi
# 设置环境变量 MAVEN_HOME
```

#### 步骤3: 配置OpenAI API Key

```bash
# Windows PowerShell
$env:OPENAI_API_KEY="your-api-key-here"

# 验证
echo $env:OPENAI_API_KEY
```

### 2.3 项目构建

```bash
# 1. 进入项目目录
cd demo-springAi

# 2. 编译项目
mvn clean compile

# 3. 打包项目（跳过测试）
mvn package -DskipTests

# 4. 启动应用
mvn spring-boot:run

# 5. 验证启动
# 访问: http://localhost:8080/api/agent/health
# 应返回: {"status":"UP","service":"AI Agent Demo",...}
```

---

## 3. 核心模块详解

### 3.1 模型层 (Model)

#### 3.1.1 Tool接口

**功能**: 定义工具的标准接口，所有工具必须实现此接口

**位置**: `model/Tool.java`

```java
public interface Tool {
    // 获取工具名称（唯一标识）
    String getName();
    
    // 获取工具描述（供LLM理解）
    String getDescription();
    
    // 获取参数JSON Schema（定义参数结构）
    JsonNode getParameters();
    
    // 执行工具
    String execute(JsonNode arguments);
}
```

#### 3.1.2 Message类

**功能**: 表示对话中的一条消息

**位置**: `model/Message.java`

```java
@Data
@Builder
public class Message {
    private String role;      // 角色: system, user, assistant, tool
    private String content;   // 消息内容
    private String toolName;  // 工具名称（tool角色时使用）
    private String toolCallId;// 工具调用ID
    
    // 静态工厂方法
    public static Message user(String content) {...}
    public static Message assistant(String content) {...}
    public static Message tool(String toolName, String content, String toolCallId) {...}
    public static Message system(String content) {...}
}
```

#### 3.1.3 Conversation类

**功能**: 管理单个对话的完整生命周期

**位置**: `model/Conversation.java`

```java
@Data
@Builder
public class Conversation {
    private String id;              // 会话唯一ID
    private LocalDateTime createdAt;// 创建时间
    private LocalDateTime updatedAt;// 更新时间
    private List<Message> messages; // 消息列表
    private String title;           // 会话标题
    
    // 添加消息
    public void addMessage(Message message) {...}
    
    // 获取非系统消息
    public List<Message> getHistoryWithoutSystem() {...}
    
    // 截断历史消息
    public void trimHistory(int maxHistoryLength) {...}
}
```

### 3.2 工具层 (Tools)

#### 3.2.1 WebSearchTool

**功能**: 执行网络搜索，获取实时信息

**位置**: `tools/WebSearchTool.java`

```java
@Component
public class WebSearchTool implements Tool {
    
    @Value("${app.tools.tavily.mock-enabled:true}")
    private boolean mockEnabled;  // Mock模式开关
    
    @Override
    public String execute(JsonNode arguments) {
        String query = arguments.get("query").asText();
        
        if (mockEnabled) {
            return executeMock(query);  // 模拟搜索结果
        }
        return executeRealSearch(query); // 真实搜索
    }
}
```

**使用场景**: 当用户需要实时信息时调用，如"今天天气怎么样？"

#### 3.2.2 ReadFileTool

**功能**: 读取文件内容，支持大文件分块

**位置**: `tools/ReadFileTool.java`

```java
@Component
public class ReadFileTool implements Tool {
    
    private static final int CHUNK_SIZE = 4000;  // 分块大小
    private static final int MAX_CHUNKS = 5;     // 最大分块数
    
    @Override
    public String execute(JsonNode arguments) {
        String filePath = arguments.get("file_path").asText();
        String searchText = arguments.has("search_text") ? arguments.get("search_text").asText() : null;
        
        if (searchText != null) {
            return searchInFile(path, searchText, chunkSize);  // 搜索模式
        }
        return readFileChunks(path, chunkSize);  // 读取模式
    }
}
```

**使用场景**: 读取项目文档、配置文件等

#### 3.2.3 TodoManagerTool

**功能**: 内存待办事项管理

**位置**: `tools/TodoManagerTool.java`

```java
@Component
public class TodoManagerTool implements Tool {
    
    private final ConcurrentMap<Integer, TodoItem> todoStore = new ConcurrentHashMap<>();
    private final AtomicInteger idCounter = new AtomicInteger(1);
    
    @Override
    public String execute(JsonNode arguments) {
        String action = arguments.get("action").asText();
        
        return switch (action.toLowerCase()) {
            case "add" -> addTodo(arguments);
            case "delete" -> deleteTodo(arguments);
            case "list" -> listTodos();
            case "complete" -> completeTodo(arguments);
            default -> "{\"error\": \"未知操作\"}";
        };
    }
}
```

**使用场景**: 管理个人任务列表

### 3.3 服务层 (Service)

#### 3.3.1 ToolRegistry

**功能**: 工具注册中心，管理所有可用工具

**位置**: `service/ToolRegistry.java`

```java
@Service
public class ToolRegistry {
    
    private final Map<String, Tool> tools = new ConcurrentHashMap<>();
    
    // Spring自动注入所有Tool实现
    public ToolRegistry(List<Tool> toolList, ObjectMapper objectMapper) {
        for (Tool tool : toolList) {
            tools.put(tool.getName(), tool);
            log.info("工具已注册: {}", tool.getName());
        }
    }
    
    // 根据名称获取工具
    public Tool getTool(String name) {...}
    
    // 获取所有工具的JSON Schema格式
    public ArrayNode getToolsAsJsonSchema() {...}
}
```

**核心机制**: 使用Spring IoC的自动装配功能，无需手动注册工具

#### 3.3.2 OpenAIService

**功能**: OpenAI API集成服务

**位置**: `service/OpenAIService.java`

```java
@Service
public class OpenAIService {
    
    // 普通对话
    public String chatCompletion(List<Message> messages) throws Exception {...}
    
    // 工具调用对话
    public ToolCall chatCompletionWithTools(List<Message> messages, ArrayNode tools) throws Exception {...}
    
    // 发送HTTP请求
    private String sendRequest(ObjectNode requestBody) throws Exception {...}
    
    // 解析工具调用响应
    private ToolCall extractToolCall(String response) throws Exception {...}
}
```

**关键配置**:
- `api-key`: OpenAI API密钥
- `model`: 使用的模型（gpt-3.5-turbo-0613）
- `temperature`: 温度参数（0.7）

#### 3.3.3 ConversationHistory

**功能**: 对话历史管理服务

**位置**: `service/ConversationHistory.java`

```java
@Service
public class ConversationHistory {
    
    private final Map<String, Conversation> conversations = new ConcurrentHashMap<>();
    
    // 创建新对话
    public String createConversation() {...}
    
    // 获取对话
    public Conversation getConversation(String conversationId) {...}
    
    // 添加消息
    public void addMessage(String conversationId, Message message) {...}
    
    // 删除对话
    public boolean deleteConversation(String conversationId) {...}
}
```

### 3.4 Agent层 (Agent)

#### 3.4.1 ReActAgent

**功能**: ReAct框架核心实现

**位置**: `agent/ReActAgent.java`

```java
@Component
public class ReActAgent {
    
    private static final String REACT_SYSTEM_PROMPT = """
        你是一个AI Agent，具备使用工具的能力...
        """;
    
    public AgentResponse execute(String conversationId, String userInput) {
        // 1. 创建/获取对话
        // 2. 添加用户消息
        // 3. 构建消息列表（包含系统提示词）
        // 4. ReAct循环：分析→工具调用→结果处理
        // 5. 生成最终回答
    }
}
```

**ReAct循环流程**:
```
用户输入 → 构建消息 → 调用LLM → 分析是否需要工具 → 执行工具 → 获取结果 → 总结回答
                                    ↓                    ↓
                               需要工具调用           工具执行完成
                                    ↓                    ↓
                               返回工具调用指令        返回工具结果给LLM
```

### 3.5 控制器层 (Controller)

#### 3.5.1 AgentController

**功能**: REST API控制器

**位置**: `controller/AgentController.java`

```java
@RestController
@RequestMapping("/api/agent")
public class AgentController {
    
    // 聊天接口
    @PostMapping("/chat")
    public ResponseEntity<AgentResponse> chat(@RequestBody ChatRequest request) {...}
    
    // 获取工具列表
    @GetMapping("/tools")
    public ResponseEntity<Map<String, Object>> getTools() {...}
    
    // 获取对话列表
    @GetMapping("/conversations")
    public ResponseEntity<Map<String, Object>> getConversations() {...}
    
    // 删除对话
    @DeleteMapping("/conversations/{id}")
    public ResponseEntity<Map<String, Object>> deleteConversation(@PathVariable String id) {...}
    
    // 健康检查
    @GetMapping("/health")
    public ResponseEntity<Map<String, Object>> health() {...}
}
```

---

## 4. 关键函数详解

### 4.1 Tool接口方法

#### 4.1.1 `getName()`

| 项目 | 说明 |
|------|------|
| **功能** | 返回工具的唯一名称 |
| **返回值** | `String` - 工具名称 |
| **要求** | 名称必须唯一，建议使用下划线命名 |
| **示例** | `"web_search"`, `"read_file"` |

#### 4.1.2 `getDescription()`

| 项目 | 说明 |
|------|------|
| **功能** | 返回工具的描述信息 |
| **返回值** | `String` - 工具描述 |
| **用途** | 供LLM理解工具用途，决定是否调用 |
| **示例** | `"用于搜索互联网获取最新信息"` |

#### 4.1.3 `getParameters()`

| 项目 | 说明 |
|------|------|
| **功能** | 返回参数的JSON Schema定义 |
| **返回值** | `JsonNode` - JSON Schema对象 |
| **结构** | `{type: "object", properties: {...}, required: [...]}` |
| **示例** | `{"type":"object","properties":{"query":{"type":"string"}},"required":["query"]}` |

#### 4.1.4 `execute(JsonNode arguments)`

| 项目 | 说明 |
|------|------|
| **功能** | 执行工具的核心逻辑 |
| **参数** | `JsonNode arguments` - 工具参数 |
| **返回值** | `String` - 执行结果（JSON格式） |
| **异常处理** | 需要处理可能的异常并返回错误信息 |

### 4.2 ToolRegistry方法

#### 4.2.1 `getTool(String name)`

| 项目 | 说明 |
|------|------|
| **功能** | 根据名称获取工具实例 |
| **参数** | `name` - 工具名称 |
| **返回值** | `Tool` - 工具实例（不存在返回null） |
| **使用场景** | Agent执行工具调用时 |

#### 4.2.2 `getToolsAsJsonSchema()`

| 项目 | 说明 |
|------|------|
| **功能** | 将所有工具转换为OpenAI工具调用格式 |
| **返回值** | `ArrayNode` - 工具JSON Schema数组 |
| **格式** | 符合OpenAI Function Calling规范 |
| **使用场景** | 调用OpenAI API时传入工具列表 |

### 4.3 OpenAIService方法

#### 4.3.1 `chatCompletion(List<Message> messages)`

| 项目 | 说明 |
|------|------|
| **功能** | 发送普通对话请求 |
| **参数** | `messages` - 消息列表 |
| **返回值** | `String` - LLM回复内容 |
| **异常** | 网络错误、API错误抛出Exception |

#### 4.3.2 `chatCompletionWithTools(List<Message> messages, ArrayNode tools)`

| 项目 | 说明 |
|------|------|
| **功能** | 发送工具调用请求 |
| **参数** | `messages` - 消息列表；`tools` - 工具列表 |
| **返回值** | `ToolCall` - 工具调用信息 |
| **调用时机** | 需要让LLM决定是否调用工具时 |

### 4.4 ReActAgent方法

#### 4.4.1 `execute(String conversationId, String userInput)`

| 项目 | 说明 |
|------|------|
| **功能** | 执行完整的Agent对话循环 |
| **参数** | `conversationId` - 对话ID（可选）；`userInput` - 用户输入 |
| **返回值** | `AgentResponse` - 包含回答、思考过程、工具使用等 |
| **核心流程** | 创建对话→构建消息→ReAct循环→生成回答 |

**ReAct循环核心代码**:

```java
int toolCallCount = 0;
boolean continueLoop = true;

while (continueLoop && toolCallCount < maxToolCalls) {
    // 调用LLM，获取工具调用指令
    ToolCall toolCall = openAIService.chatCompletionWithTools(messages, tools);
    
    if (toolCall.isValid()) {
        // 执行工具
        String toolResult = executeTool(toolCall);
        
        // 添加工具调用消息和结果
        messages.add(Message.assistant("调用工具: " + toolCall.getToolName()));
        messages.add(Message.tool(toolCall.getToolName(), toolResult, toolCall.getId()));
        
        toolCallCount++;
    } else {
        continueLoop = false;
    }
}

// 生成最终回答
String finalAnswer = openAIService.chatCompletion(messages);
```

### 4.5 ConversationHistory方法

#### 4.5.1 `createConversation()`

| 项目 | 说明 |
|------|------|
| **功能** | 创建新对话 |
| **返回值** | `String` - 新对话ID |
| **实现** | 使用UUID生成唯一ID |

#### 4.5.2 `addMessage(String conversationId, Message message)`

| 项目 | 说明 |
|------|------|
| **功能** | 向对话添加消息 |
| **参数** | `conversationId` - 对话ID；`message` - 消息对象 |
| **副作用** | 更新对话的updatedAt时间 |

---

## 5. 项目流程分析

### 5.1 完整对话流程

```
用户请求 → API端点 → Agent → LLM → 工具调用 → 结果处理 → 返回响应
   ↓          ↓        ↓      ↓        ↓          ↓           ↓
HTTP请求  Controller  ReAct  OpenAI   Tool       ReAct       JSON响应
```

### 5.2 工具调用流程

```
用户提问 → ReActAgent → OpenAIService → 分析是否调用工具
                                        ↓
                                   需要调用工具
                                        ↓
                              获取工具调用指令 (ToolCall)
                                        ↓
                              ToolRegistry获取工具
                                        ↓
                              执行工具 (Tool.execute)
                                        ↓
                              返回结果给LLM
                                        ↓
                              LLM总结回答
```

### 5.3 数据流向图

```
┌─────────────┐      ┌────────────────┐      ┌─────────────┐
│   用户请求   │ ──→ │  AgentController│ ──→ │  ReActAgent │
└─────────────┘      └────────────────┘      └──────┬──────┘
                                                    │
         ┌──────────────────────────────────────────┼──────────────────────────────────────────┐
         │                                          │                                          │
         ▼                                          ▼                                          ▼
┌────────────────┐                      ┌──────────────────┐                      ┌──────────────────┐
│ Conversation   │ ←─ 更新对话历史 ─── │   ToolRegistry   │ ←─ 获取工具列表 ─── │   OpenAIService  │
│    History     │                      │                  │                      │                  │
└────────────────┘                      └────────┬─────────┘                      └────────┬─────────┘
                                                 │                                          │
                                                 ▼                                          ▼
                                      ┌──────────────────┐                      ┌──────────────────┐
                                      │     Tools        │                      │    OpenAI API    │
                                      │ (WebSearch,      │                      │                  │
                                      │  ReadFile, etc)  │                      │                  │
                                      └──────────────────┘                      └──────────────────┘
```

---

## 6. 实践练习

### 6.1 基础练习

#### 练习1: 运行项目并测试基础功能

**目标**: 熟悉项目启动和基本API调用

**步骤**:
1. 启动项目：`mvn spring-boot:run`
2. 测试健康检查：`curl http://localhost:8080/api/agent/health`
3. 获取工具列表：`curl http://localhost:8080/api/agent/tools`
4. 发送简单消息：
   ```bash
   curl -X POST http://localhost:8080/api/agent/chat \
     -H "Content-Type: application/json" \
     -d '{"message": "你好"}'
   ```

**预期结果**:
- 健康检查返回状态UP
- 工具列表包含5个工具
- 收到AI的友好回复

#### 练习2: 测试工具调用

**目标**: 验证工具调用功能

**步骤**:
1. 测试待办管理工具：
   ```bash
   curl -X POST http://localhost:8080/api/agent/chat \
     -H "Content-Type: application/json" \
     -d '{"message": "添加待办事项：学习AI Agent"}'
   ```
2. 测试目录列表工具：
   ```bash
   curl -X POST http://localhost:8080/api/agent/chat \
     -H "Content-Type: application/json" \
     -d '{"message": "列出当前目录"}'
   ```

**预期结果**:
- 待办事项添加成功
- 目录列表正确显示

### 6.2 进阶练习

#### 练习3: 创建自定义工具

**目标**: 掌握工具开发流程

**步骤**:
1. 创建新工具类 `CalculatorTool.java`
2. 实现 `Tool` 接口
3. 支持加减乘除四则运算
4. 编译并测试

**参考代码**:
```java
@Component
public class CalculatorTool implements Tool {
    
    private final ObjectMapper objectMapper;
    
    public CalculatorTool(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }
    
    @Override
    public String getName() {
        return "calculator";
    }
    
    @Override
    public String getDescription() {
        return "执行数学计算。参数：expression（数学表达式）";
    }
    
    @Override
    public JsonNode getParameters() {
        ObjectNode properties = objectMapper.createObjectNode();
        ObjectNode exprProp = objectMapper.createObjectNode();
        exprProp.put("type", "string");
        exprProp.put("description", "数学表达式");
        properties.set("expression", exprProp);
        
        ObjectNode params = objectMapper.createObjectNode();
        params.put("type", "object");
        params.set("properties", properties);
        params.putArray("required").add("expression");
        
        return params;
    }
    
    @Override
    public String execute(JsonNode arguments) {
        String expression = arguments.get("expression").asText();
        try {
            // 简单实现：使用JavaScript引擎
            javax.script.ScriptEngine engine = new javax.script.ScriptEngineManager().getEngineByName("JavaScript");
            Object result = engine.eval(expression);
            return "{\"result\": " + result + "}";
        } catch (Exception e) {
            return "{\"error\": \"" + e.getMessage() + "\"}";
        }
    }
}
```

#### 练习4: 修改系统提示词

**目标**: 理解提示工程的重要性

**步骤**:
1. 修改 `ReActAgent.java` 中的系统提示词
2. 添加更详细的工具使用指导
3. 测试不同提示词对结果的影响

**提示词优化方向**:
- 添加工具使用示例
- 明确工具选择策略
- 定义输出格式要求

### 6.3 综合练习

#### 练习5: 实现多轮对话

**目标**: 理解对话历史管理

**步骤**:
1. 发送多条消息保持同一个对话ID
2. 验证对话历史正确保存
3. 测试对话切换功能

**测试命令**:
```bash
# 第一次请求（获取conversationId）
curl -X POST http://localhost:8080/api/agent/chat \
  -H "Content-Type: application/json" \
  -d '{"message": "你好，我叫小明"}'

# 第二次请求（使用返回的conversationId）
curl -X POST http://localhost:8080/api/agent/chat \
  -H "Content-Type: application/json" \
  -d '{"conversationId": "your-conversation-id", "message": "我刚才说了什么？"}'
```

**预期结果**: AI能够记住之前的对话内容

---

## 7. 常见问题

### Q1: 端口8080被占用

**问题描述**: 启动时报错 "Port 8080 was already in use"

**解决方案**:
```bash
# 查找占用进程
netstat -ano | findstr :8080

# 终止进程（替换PID为实际值）
taskkill /F /PID 1234
```

### Q2: OpenAI API调用失败

**问题描述**: 发送消息时报API错误

**解决方案**:
1. 检查API Key是否正确设置
2. 确认网络连接正常
3. 查看日志中的错误信息
4. 检查API余额是否充足

### Q3: 工具调用没有响应

**问题描述**: AI没有调用预期的工具

**解决方案**:
1. 检查工具是否正确注册（查看启动日志）
2. 确认系统提示词包含工具描述
3. 检查工具参数定义是否正确
4. 测试工具是否能独立工作

### Q4: 响应时间过长

**问题描述**: 对话响应很慢

**解决方案**:
1. 检查网络延迟
2. 考虑使用更快的模型（gpt-3.5-turbo）
3. 减少历史消息数量
4. 优化工具执行逻辑

### Q5: 如何切换到Mock模式

**问题描述**: 没有OpenAI API Key，想测试功能

**解决方案**:
修改 `application.yml`:
```yaml
app:
  tools:
    tavily:
      mock-enabled: true
```

Mock模式下，搜索工具会返回模拟数据。

---

## 附录：API接口列表

| 接口 | 方法 | 路径 | 说明 |
|------|------|------|------|
| 聊天 | POST | `/api/agent/chat` | 发送消息给AI Agent |
| 获取工具 | GET | `/api/agent/tools` | 获取可用工具列表 |
| 获取对话 | GET | `/api/agent/conversations` | 获取所有对话列表 |
| 删除对话 | DELETE | `/api/agent/conversations/{id}` | 删除指定对话 |
| 健康检查 | GET | `/api/agent/health` | 检查服务状态 |

---

**文档版本**: v1.0  
**最后更新**: 2024年  
**适用项目**: AI Agent Demo

---

*祝您学习愉快！🚀*
