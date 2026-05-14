# 🤖 AI Agent Demo

一个完整的 AI Agent 实践项目，基于 Spring Boot + ReAct 框架实现，支持工具调用、对话管理、Web 搜索等功能。

## ✨ 核心特性

| 特性 | 说明 |
|------|------|
| 🔄 **ReAct 框架** | 推理-行动循环，智能判断何时调用工具 |
| 🔧 **5 种工具** | 网页搜索、网页抓取、文件读取、目录浏览、待办管理 |
| 💬 **对话管理** | 支持多轮对话，会话历史自动保存 |
| 🎨 **Web 界面** | 响应式前端，即开即用 |
| 📦 **容器化** | 支持 Docker 一键部署 |

## 🏗️ 项目架构

```
┌─────────────────────────────────────────────────────┐
│                      前端页面                         │
│                   (index.html)                        │
└─────────────────────┬───────────────────────────────┘
                      │ HTTP
┌─────────────────────▼───────────────────────────────┐
│                 AgentController                       │
│                   REST API                            │
└─────────────────────┬───────────────────────────────┘
                      │
┌─────────────────────▼───────────────────────────────┐
│                   ReActAgent                           │
│              ReAct 核心循环逻辑                        │
└───────┬─────────────────┬─────────────────┬──────────┘
        │                 │                 │
        ▼                 ▼                 ▼
┌───────────────┐ ┌───────────────┐ ┌───────────────┐
│ OpenAIService │ │ ToolRegistry  │ │Conversation   │
│   调用 LLM     │ │   工具管理     │ │   History     │
└───────────────┘ └───────┬───────┘ └───────────────┘
                          │
                          ▼
            ┌─────────────────────────┐
            │        Tools            │
            │ ┌─────┬─────┬─────┬────┐ │
            │ │搜索 │抓取 │读取 │目录│ │
            │ └─────┴─────┴─────┴────┘ │
            └─────────────────────────┘
```

## 🚀 快速开始

### 环境要求

- Java 17+
- Maven 3.8+
- OpenAI API Key

### 1. 克隆项目

```bash
git clone git@github.com:yize-123/agent-demo-.git
cd agent-demo-
```

### 2. 设置 API Key

```bash
# Linux/Mac
export OPENAI_API_KEY=sk-your-key-here

# Windows PowerShell
$env:OPENAI_API_KEY="sk-your-key-here"
```

### 3. 编译运行

```bash
mvn clean compile
mvn spring-boot:run
```

### 4. 打开浏览器

访问 http://localhost:8080/index.html

## 📡 API 接口

### 聊天接口

```bash
POST /api/agent/chat
Content-Type: application/json

{
  "message": "你的问题",
  "conversationId": "可选，不传则创建新对话"
}
```

### 响应示例

```json
{
  "conversationId": "conv-uuid-123",
  "answer": "根据搜索结果，今天天气晴朗...",
  "thoughts": [
    "用户询问天气",
    "需要调用搜索工具获取实时信息",
    "搜索结果：天气晴朗，温度25度"
  ],
  "toolsUsed": ["web_search"],
  "toolUsed": true,
  "responseTimeMs": 1500
}
```

### 其他接口

| 接口 | 方法 | 说明 |
|------|------|------|
| `/api/agent/health` | GET | 健康检查 |
| `/api/agent/tools` | GET | 获取工具列表 |
| `/api/agent/conversations` | GET | 获取对话列表 |
| `/api/agent/conversations/{id}` | DELETE | 删除对话 |

## 🛠️ 可用工具

| 工具 | 功能 | 示例问题 |
|------|------|----------|
| `web_search` | 搜索网络信息 | "今天有什么新闻？" |
| `fetch_webpage` | 抓取网页内容 | "帮我看看这个网页的内容" |
| `read_file` | 读取本地文件 | "读取项目下的 README.md" |
| `list_directory` | 浏览目录结构 | "列出当前目录的文件" |
| `manage_todo` | 管理待办事项 | "帮我创建一个待办" |

## 🔌 添加自定义工具

```java
@Component
public class CalculatorTool implements Tool {

    @Override
    public String getName() {
        return "calculator";
    }

    @Override
    public String getDescription() {
        return "执行数学计算，支持加减乘除";
    }

    @Override
    public JsonNode getParameters() {
        // 返回 JSON Schema 格式的参数定义
    }

    @Override
    public String execute(JsonNode arguments) {
        // 实现计算逻辑
        return "{\"result\": 42}";
    }
}
```

添加 `@Component` 注解后，工具会自动注册！

## 🐳 Docker 部署

```bash
# 构建镜像
docker build -t ai-agent-demo .

# 运行容器
docker run -d -p 8080:8080 \
  -e OPENAI_API_KEY=sk-your-key-here \
  --name ai-agent \
  ai-agent-demo
```

## 📁 项目结构

```
src/main/java/com/example/aigent/
├── AiAgentDemoApplication.java    # 🚀 启动入口
├── agent/
│   └── ReActAgent.java            # 🧠 ReAct 核心实现
├── controller/
│   └── AgentController.java       # 🌐 REST API
├── model/
│   ├── Message.java               # 💬 消息模型
│   ├── Tool.java                  # 🔧 工具接口
│   ├── ToolCall.java              # 📞 工具调用
│   ├── AgentResponse.java         # 📦 响应模型
│   └── Conversation.java          # 💭 对话模型
├── service/
│   ├── OpenAIService.java         # 🤖 LLM 调用
│   ├── ToolRegistry.java          # 📋 工具注册
│   └── ConversationHistory.java   # 📜 对话历史
└── tools/
    ├── WebSearchTool.java         # 🔍 网页搜索
    ├── FetchFromWebTool.java       # 🌐 网页抓取
    ├── ReadFileTool.java           # 📄 文件读取
    ├── LSRepoTool.java            # 📁 目录浏览
    └── TodoManagerTool.java       # ✅ 待办管理
```

## 📚 学习资源

项目配套详细学习文档：

| 文档 | 说明 |
|------|------|
| `learn/视频讲解式开发教程.md` | 📖 像看视频一样学开发 |
| `learn/五天从零写项目计划.md` | 📅 5 天从零构建项目 |
| `learn/五天每日技能练习.md` | 🛠️ 每天添加新功能 |
| `learn/Demo 项目学习指南.md` | 📘 完整学习指南 |

## 🔬 ReAct 工作流程

```
用户: "北京今天天气如何？"
  │
  ▼
┌─────────────────────────────────────┐
│ Step 1: 思考 (Thought)              │
│ "用户问天气，需要搜索获取实时信息"    │
└─────────────────┬───────────────────┘
                  │ 需要工具
┌─────────────────▼───────────────────┐
│ Step 2: 行动 (Action)               │
│ 调用 web_search，参数: query=北京天气 │
└─────────────────┬───────────────────┘
                  │
┌─────────────────▼───────────────────┐
│ Step 3: 观察 (Observation)          │
│ 工具返回: "北京今天晴，25度"         │
└─────────────────┬───────────────────┘
                  │ 再次思考
┌─────────────────▼───────────────────┐
│ Step 4: 回答                        │
│ "北京今天天气晴朗，温度约25度..."     │
└─────────────────────────────────────┘
```

## ⚙️ 配置说明

```yaml
# application.yml
server:
  port: 8080

openai:
  api-key: ${OPENAI_API_KEY}      # 从环境变量读取
  model: gpt-3.5-turbo-0613       # 使用的模型
  temperature: 0.7                # 随机性 (0-1)

app:
  agent:
    max-tool-calls: 5            # 最大工具调用次数
```

## 🤝 贡献

欢迎提交 Issue 和 Pull Request！

## 📄 License

MIT License
