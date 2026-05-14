# AI Agent Demo 函数参考手册

## 目录

1. [工具接口 (Tool)](#1-工具接口-tool)
2. [工具注册器 (ToolRegistry)](#2-工具注册器-toolregistry)
3. [OpenAI服务 (OpenAIService)](#3-openai服务-openaIservice)
4. [ReAct Agent (ReActAgent)](#4-react-agent-reactagent)
5. [对话历史 (ConversationHistory)](#5-对话历史-conversationhistory)
6. [消息模型 (Message)](#6-消息模型-message)
7. [工具列表](#7-工具列表)

---

## 1. 工具接口 (Tool)

### 1.1 接口定义

```java
public interface Tool {
    String getName();
    String getDescription();
    JsonNode getParameters();
    String execute(JsonNode arguments);
}
```

### 1.2 方法说明

#### `getName()`

| 属性 | 说明 |
|------|------|
| **功能** | 返回工具的唯一名称 |
| **返回值** | `String` - 工具名称 |
| **命名规范** | 小写字母，使用下划线分隔 |
| **示例** | `"web_search"`, `"read_file"` |

#### `getDescription()`

| 属性 | 说明 |
|------|------|
| **功能** | 返回工具的描述信息 |
| **返回值** | `String` - 工具描述 |
| **用途** | 供LLM理解工具用途 |
| **示例** | `"用于搜索互联网获取最新信息"` |

#### `getParameters()`

| 属性 | 说明 |
|------|------|
| **功能** | 返回参数的JSON Schema定义 |
| **返回值** | `JsonNode` - JSON Schema对象 |
| **格式要求** | 符合OpenAI Function Calling规范 |

**Schema结构**:
```json
{
  "type": "object",
  "properties": {
    "param_name": {
      "type": "string",
      "description": "参数描述"
    }
  },
  "required": ["param_name"]
}
```

#### `execute(JsonNode arguments)`

| 属性 | 说明 |
|------|------|
| **功能** | 执行工具的核心逻辑 |
| **参数** | `JsonNode arguments` - 工具参数 |
| **返回值** | `String` - 执行结果（JSON格式） |
| **异常处理** | 必须处理异常并返回错误信息 |

---

## 2. 工具注册器 (ToolRegistry)

### 2.1 类定义

```java
@Service
public class ToolRegistry {
    private final Map<String, Tool> tools = new ConcurrentHashMap<>();
    
    public ToolRegistry(List<Tool> toolList, ObjectMapper objectMapper) { ... }
}
```

### 2.2 核心机制

**Spring自动装配**:
```java
public ToolRegistry(List<Tool> toolList, ObjectMapper objectMapper) {
    for (Tool tool : toolList) {
        tools.put(tool.getName(), tool);
        log.info("工具已注册: {}", tool.getName());
    }
}
```

**优势**:
- Spring自动收集所有`Tool`接口实现
- 无需手动注册，避免遗漏
- 启动时自动记录注册日志

### 2.3 方法说明

#### `getTool(String name)`

| 属性 | 说明 |
|------|------|
| **功能** | 根据名称获取工具实例 |
| **参数** | `name` - 工具名称 |
| **返回值** | `Tool` - 工具实例（不存在返回null） |
| **使用场景** | Agent执行工具调用时 |

**示例**:
```java
Tool tool = toolRegistry.getTool("web_search");
if (tool != null) {
    String result = tool.execute(arguments);
}
```

#### `getToolsAsJsonSchema()`

| 属性 | 说明 |
|------|------|
| **功能** | 将所有工具转换为OpenAI工具调用格式 |
| **返回值** | `ArrayNode` - 工具JSON Schema数组 |
| **格式** | 符合OpenAI Function Calling规范 |

**返回格式**:
```json
[
  {
    "type": "function",
    "function": {
      "name": "web_search",
      "description": "...",
      "parameters": {...}
    }
  }
]
```

#### `getAllTools()`

| 属性 | 说明 |
|------|------|
| **功能** | 获取所有已注册工具 |
| **返回值** | `List<Tool>` - 工具列表 |

#### `hasTool(String name)`

| 属性 | 说明 |
|------|------|
| **功能** | 检查工具是否已注册 |
| **参数** | `name` - 工具名称 |
| **返回值** | `boolean` - 是否存在 |

---

## 3. OpenAI服务 (OpenAIService)

### 3.1 类定义

```java
@Service
public class OpenAIService {
    private final String apiKey;
    private final String model;
    private final double temperature;
    
    public String chatCompletion(List<Message> messages) throws Exception { ... }
    public ToolCall chatCompletionWithTools(List<Message> messages, ArrayNode tools) throws Exception { ... }
}
```

### 3.2 配置参数

| 参数 | 类型 | 默认值 | 说明 |
|------|------|--------|------|
| apiKey | String | - | OpenAI API密钥 |
| model | String | gpt-3.5-turbo-0613 | 使用的模型 |
| temperature | double | 0.7 | 温度参数（0-2） |
| baseUrl | String | https://api.openai.com | API基础地址 |

### 3.3 方法说明

#### `chatCompletion(List<Message> messages)`

| 属性 | 说明 |
|------|------|
| **功能** | 发送普通对话请求 |
| **参数** | `messages` - 消息列表 |
| **返回值** | `String` - LLM回复内容 |
| **异常** | 网络错误、API错误抛出Exception |

**调用流程**:
1. 构建请求体
2. 发送HTTP POST请求
3. 解析响应提取内容

#### `chatCompletionWithTools(List<Message> messages, ArrayNode tools)`

| 属性 | 说明 |
|------|------|
| **功能** | 发送工具调用请求 |
| **参数** | `messages` - 消息列表；`tools` - 工具列表 |
| **返回值** | `ToolCall` - 工具调用信息 |

**使用场景**: 需要让LLM决定是否调用工具时

**ToolCall结构**:
```java
@Data
public class ToolCall {
    private String id;           // 调用ID
    private String toolName;     // 工具名称
    private String arguments;    // 参数（JSON字符串）
    
    public boolean isValid() {
        return toolName != null && !toolName.isEmpty();
    }
}
```

#### `sendRequest(ObjectNode requestBody)`

| 属性 | 说明 |
|------|------|
| **功能** | 发送HTTP请求到OpenAI API |
| **参数** | `requestBody` - 请求体JSON |
| **返回值** | `String` - 响应体 |
| **内部方法** | 不对外暴露 |

---

## 4. ReAct Agent (ReActAgent)

### 4.1 类定义

```java
@Component
public class ReActAgent {
    private final OpenAIService openAIService;
    private final ToolRegistry toolRegistry;
    private final ConversationHistory conversationHistory;
    
    public AgentResponse execute(String conversationId, String userInput) { ... }
}
```

### 4.2 核心常量

#### `REACT_SYSTEM_PROMPT`

```java
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
```

**作用**: 指导LLM按照ReAct模式进行思考和行动

### 4.3 方法说明

#### `execute(String conversationId, String userInput)`

| 属性 | 说明 |
|------|------|
| **功能** | 执行完整的Agent对话循环 |
| **参数** | `conversationId` - 对话ID（可选）；`userInput` - 用户输入 |
| **返回值** | `AgentResponse` - 包含回答、思考过程等 |

**核心流程**:

```
┌─────────────────────────────────────────────────────────────┐
│ 1. 创建/获取对话                                            │
│    conversationId为空 → 创建新对话                           │
│    conversationId存在 → 获取已有对话                          │
├─────────────────────────────────────────────────────────────┤
│ 2. 添加用户消息                                              │
│    conversationHistory.addMessage(conversationId, message)   │
├─────────────────────────────────────────────────────────────┤
│ 3. 构建消息列表                                              │
│    - 添加系统提示词（包含工具描述）                            │
│    - 添加对话历史                                            │
├─────────────────────────────────────────────────────────────┤
│ 4. ReAct循环                                                │
│    while (needToolCall && toolCallCount < maxToolCalls) {   │
│      - 调用LLM获取工具调用指令                                │
│      - 执行工具获取结果                                       │
│      - 将结果添加到消息列表                                   │
│    }                                                        │
├─────────────────────────────────────────────────────────────┤
│ 5. 生成最终回答                                              │
│    openAIService.chatCompletion(messages)                    │
├─────────────────────────────────────────────────────────────┤
│ 6. 返回响应                                                  │
│    AgentResponse.success(...)                                │
└─────────────────────────────────────────────────────────────┘
```

#### `buildMessages(String conversationId)`

| 属性 | 说明 |
|------|------|
| **功能** | 构建发送给LLM的消息列表 |
| **参数** | `conversationId` - 对话ID |
| **返回值** | `List<Message>` - 消息列表 |

**消息结构**:
1. 系统提示词（包含工具描述）
2. 对话历史消息

#### `executeTool(ToolCall toolCall)`

| 属性 | 说明 |
|------|------|
| **功能** | 执行工具调用 |
| **参数** | `toolCall` - 工具调用信息 |
| **返回值** | `String` - 工具执行结果 |

**执行流程**:
1. 根据工具名称获取工具实例
2. 解析工具参数
3. 调用工具的execute方法
4. 返回执行结果

---

## 5. 对话历史 (ConversationHistory)

### 5.1 类定义

```java
@Service
public class ConversationHistory {
    private final Map<String, Conversation> conversations = new ConcurrentHashMap<>();
}
```

### 5.2 方法说明

#### `createConversation()`

| 属性 | 说明 |
|------|------|
| **功能** | 创建新对话 |
| **返回值** | `String` - 新对话ID |
| **实现** | 使用UUID生成唯一ID |

**示例**:
```java
String conversationId = conversationHistory.createConversation();
```

#### `getConversation(String conversationId)`

| 属性 | 说明 |
|------|------|
| **功能** | 获取对话实例 |
| **参数** | `conversationId` - 对话ID |
| **返回值** | `Conversation` - 对话实例（不存在返回null） |

#### `addMessage(String conversationId, Message message)`

| 属性 | 说明 |
|------|------|
| **功能** | 向对话添加消息 |
| **参数** | `conversationId` - 对话ID；`message` - 消息对象 |
| **副作用** | 更新对话的updatedAt时间 |

#### `getMessages(String conversationId)`

| 属性 | 说明 |
|------|------|
| **功能** | 获取对话的消息列表 |
| **参数** | `conversationId` - 对话ID |
| **返回值** | `List<Message>` - 消息列表 |

#### `deleteConversation(String conversationId)`

| 属性 | 说明 |
|------|------|
| **功能** | 删除对话 |
| **参数** | `conversationId` - 对话ID |
| **返回值** | `boolean` - 是否删除成功 |

#### `exists(String conversationId)`

| 属性 | 说明 |
|------|------|
| **功能** | 检查对话是否存在 |
| **参数** | `conversationId` - 对话ID |
| **返回值** | `boolean` - 是否存在 |

---

## 6. 消息模型 (Message)

### 6.1 类定义

```java
@Data
@Builder
public class Message {
    private String role;
    private String content;
    private String toolName;
    private String toolCallId;
}
```

### 6.2 角色类型

| 角色 | 说明 |
|------|------|
| **system** | 系统提示词，指导AI行为 |
| **user** | 用户消息 |
| **assistant** | AI助手回复 |
| **tool** | 工具执行结果 |

### 6.3 静态工厂方法

#### `Message.user(String content)`

| 属性 | 说明 |
|------|------|
| **功能** | 创建用户消息 |
| **参数** | `content` - 用户输入 |
| **返回值** | `Message` - 用户消息实例 |

#### `Message.assistant(String content)`

| 属性 | 说明 |
|------|------|
| **功能** | 创建助手消息 |
| **参数** | `content` - 助手回复 |
| **返回值** | `Message` - 助手消息实例 |

#### `Message.tool(String toolName, String content, String toolCallId)`

| 属性 | 说明 |
|------|------|
| **功能** | 创建工具消息 |
| **参数** | `toolName` - 工具名称；`content` - 工具结果；`toolCallId` - 调用ID |
| **返回值** | `Message` - 工具消息实例 |

#### `Message.system(String content)`

| 属性 | 说明 |
|------|------|
| **功能** | 创建系统消息 |
| **参数** | `content` - 系统提示词 |
| **返回值** | `Message` - 系统消息实例 |

---

## 7. 工具列表

### 7.1 WebSearchTool

| 属性 | 说明 |
|------|------|
| **名称** | `web_search` |
| **描述** | 用于搜索互联网获取最新信息 |
| **参数** | `query` (String, 必填) - 搜索关键词 |
| **返回** | JSON格式的搜索结果 |

**Mock模式**: 当`tavily.mock-enabled=true`时返回模拟数据

### 7.2 FetchFromWebTool

| 属性 | 说明 |
|------|------|
| **名称** | `fetch_webpage` |
| **描述** | 获取网页内容，提取标题、正文和链接 |
| **参数** | `url` (String, 必填) - 网页URL |
| **返回** | 包含标题、内容、链接的JSON |

**使用Jsoup解析HTML**

### 7.3 ReadFileTool

| 属性 | 说明 |
|------|------|
| **名称** | `read_file` |
| **描述** | 读取文件内容（支持大文件分块） |
| **参数** | `file_path` (String, 必填), `search_text` (String, 可选), `chunk_size` (Integer, 可选) |
| **返回** | 文件内容或搜索结果 |

**特性**:
- 大文件分块读取（默认4000字符/块）
- 支持内容搜索
- 最多返回5个分块

### 7.4 LSRepoTool

| 属性 | 说明 |
|------|------|
| **名称** | `list_directory` |
| **描述** | 列出指定目录下的文件和文件夹 |
| **参数** | `path` (String, 可选) - 目录路径 |
| **返回** | 目录内容列表 |

**使用Java NIO实现**

### 7.5 TodoManagerTool

| 属性 | 说明 |
|------|------|
| **名称** | `manage_todo` |
| **描述** | 管理待办事项 |
| **参数** | `action` (String, 必填), `task` (String, 可选), `id` (Integer, 可选) |
| **返回** | 操作结果 |

**支持操作**:
- `add` - 添加任务
- `delete` - 删除任务
- `list` - 列出任务
- `complete` - 标记完成

---

## 附录：AgentResponse结构

```java
@Data
@Builder
public class AgentResponse {
    private String conversationId;  // 对话ID
    private String answer;          // 最终回答
    private List<String> thoughts;  // 思考过程
    private List<String> toolsUsed; // 使用的工具
    private boolean toolUsed;       // 是否使用工具
    private long responseTimeMs;    // 响应时间(ms)
    
    // 静态工厂方法
    public static AgentResponse success(...) {...}
    public static AgentResponse error(String errorMessage) {...}
}
```

---

**文档版本**: v1.0  
**最后更新**: 2024年
