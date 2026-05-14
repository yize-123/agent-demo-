# AI Agent 5天系统化学习计划

## 学习目标

通过5天的系统化学习，从零基础掌握AI Agent的核心技术，包括ReAct框架、工具系统、LLM集成、对话管理等关键知识点，并能够独立开发和部署AI Agent应用。

## 前置要求

- Java 17+ 基础知识
- Spring Boot 基础了解
- REST API 基本概念
- 对AI和LLM有兴趣

---

## Day 1: 基础入门 - 理解AI Agent概念

### 学习目标

- 理解AI Agent的基本概念和工作原理
- 掌握项目结构和技术栈
- 成功运行项目并完成第一个对话
- 了解ReAct框架的基本思想

### 时间分配

| 时间段 | 内容 | 时长 |
|--------|------|------|
| 上午 | 理论学习 | 2小时 |
| 下午 | 环境搭建 | 1.5小时 |
| 下午 | 项目运行 | 0.5小时 |

### 核心知识点

#### 1. AI Agent概念
- **定义**: AI Agent是能够感知环境、推理决策并执行行动的智能系统
- **核心能力**: 感知、推理、行动、学习
- **应用场景**: 智能客服、任务自动化、知识问答、代码助手

#### 2. ReAct框架
- **全称**: Reasoning + Acting（推理+行动）
- **工作流程**:
  1. 分析用户问题
  2. 决定是否需要调用工具
  3. 执行工具获取信息
  4. 基于结果生成回答
- **优势**: 结合推理和行动，提高问题解决能力

#### 3. 技术栈介绍
- **后端**: Java 17 + Spring Boot 3.2
- **AI服务**: OpenAI API (GPT-3.5/4)
- **前端**: HTML5 + CSS3 + JavaScript
- **工具库**: Jsoup（网页解析）、Jackson（JSON处理）

### 实践任务

#### 任务1: 环境配置（30分钟）
```bash
# 1. 安装Java 17
java -version

# 2. 安装Maven 3.8+
mvn -version

# 3. 克隆或下载项目
cd demo-springAi

# 4. 设置OpenAI API Key
export OPENAI_API_KEY=your-api-key-here
```

#### 任务2: 编译运行项目（30分钟）
```bash
# 1. 编译项目
mvn clean package -DskipTests

# 2. 启动应用
mvn spring-boot:run

# 3. 访问前端页面
# 浏览器打开: http://localhost:8080/index.html
```

#### 任务3: 测试API接口（30分钟）
```bash
# 1. 健康检查
curl http://localhost:8080/api/agent/health

# 2. 获取工具列表
curl http://localhost:8080/api/agent/tools

# 3. 发送聊天消息
curl -X POST http://localhost:8080/api/agent/chat \
  -H "Content-Type: application/json" \
  -d '{"message": "你好，请介绍一下你自己"}'
```

#### 任务4: 前端体验（30分钟）
- 访问前端页面
- 尝试与AI Agent对话
- 测试快捷按钮功能
- 查看思考过程展示

### 评估标准

- [ ] 成功配置开发环境
- [ ] 项目能够正常启动
- [ ] 能够通过API发送消息并收到回复
- [ ] 前端页面正常显示
- [ ] 理解AI Agent和ReAct框架的基本概念

### 学习资源

- [ReAct论文](https://arxiv.org/abs/2210.03629)
- [Spring Boot官方文档](https://docs.spring.io/spring-boot/docs/current/reference/html/)
- [OpenAI API文档](https://platform.openai.com/docs/introduction)

---

## Day 2: 工具系统 - 掌握工具注册与开发

### 学习目标

- 理解工具系统的架构设计
- 掌握工具接口的实现方法
- 学会使用Spring IoC自动注册工具
- 开发一个自定义工具

### 时间分配

| 时间段 | 内容 | 时长 |
|--------|------|------|
| 上午 | 理论学习 | 2小时 |
| 下午 | 代码分析 | 2小时 |
| 下午 | 实践开发 | 1小时 |

### 核心知识点

#### 1. 工具接口设计
```java
public interface Tool {
    String getName();              // 工具名称
    String getDescription();        // 工具描述
    JsonNode getParameters();      // 参数JSON Schema
    String execute(JsonNode args); // 执行逻辑
}
```

#### 2. Spring IoC自动注册
- **原理**: Spring自动收集所有实现Tool接口的Bean
- **优势**: 无需手动注册，避免遗漏
- **实现**: 构造器注入List<Tool>

#### 3. JSON Schema
- **作用**: 描述工具参数结构
- **格式**: 符合OpenAI Function Calling规范
- **示例**:
```json
{
  "type": "object",
  "properties": {
    "query": {
      "type": "string",
      "description": "搜索关键词"
    }
  },
  "required": ["query"]
}
```

### 实践任务

#### 任务1: 分析现有工具（1小时）
阅读以下工具的实现代码：
- `WebSearchTool.java` - 网络搜索
- `ReadFileTool.java` - 文件读取
- `TodoManagerTool.java` - 待办管理

重点关注：
- 参数定义方式
- 执行逻辑实现
- 错误处理机制

#### 任务2: 理解工具注册机制（30分钟）
分析`ToolRegistry.java`：
```java
public ToolRegistry(List<Tool> toolList, ObjectMapper mapper) {
    for (Tool tool : toolList) {
        tools.put(tool.getName(), tool);
    }
}
```

理解：
- Spring如何自动注入工具列表
- 工具名称作为唯一标识
- 转换为OpenAI工具格式

#### 任务3: 开发自定义工具（1.5小时）

**任务**: 创建一个计算器工具

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
        return "执行基本数学计算。参数：expression（数学表达式，如：2+3*4）";
    }
    
    @Override
    public JsonNode getParameters() {
        // 实现参数JSON Schema
    }
    
    @Override
    public String execute(JsonNode arguments) {
        // 实现计算逻辑
    }
}
```

**要求**:
- 支持加减乘除四则运算
- 处理异常情况（除零、非法表达式）
- 返回JSON格式结果

#### 任务4: 测试新工具（30分钟）
```bash
# 1. 重新编译项目
mvn clean package -DskipTests

# 2. 重启应用
mvn spring-boot:run

# 3. 测试工具调用
curl -X POST http://localhost:8080/api/agent/chat \
  -H "Content-Type: application/json" \
  -d '{"message": "帮我计算 15 * 8 + 32"}'
```

### 评估标准

- [ ] 理解工具接口的设计原理
- [ ] 掌握Spring IoC自动注册机制
- [ ] 成功开发并测试自定义工具
- [ ] 工具能够正确处理参数和异常
- [ ] 理解JSON Schema的作用和格式

### 学习资源

- [Spring IoC容器](https://docs.spring.io/spring-framework/reference/core/beans/introduction.html)
- [JSON Schema规范](https://json-schema.org/)
- [OpenAI Function Calling](https://platform.openai.com/docs/guides/function-calling)

---

## Day 3: ReAct框架 - 深入理解推理-行动循环

### 学习目标

- 深入理解ReAct框架的工作原理
- 掌握系统提示词的设计方法
- 优化Agent的决策逻辑
- 测试复杂场景下的表现

### 时间分配

| 时间段 | 内容 | 时长 |
|--------|------|------|
| 上午 | 理论学习 | 2.5小时 |
| 下午 | 代码分析 | 2小时 |
| 下午 | 优化实践 | 0.5小时 |

### 核心知识点

#### 1. ReAct循环机制
```
用户输入 → 推理分析 → 决策 → 工具调用 → 结果处理 → 总结回答
    ↑                                              ↓
    └────────────────── 循环直到完成 ─────────────────┘
```

#### 2. 系统提示词设计
- **结构**: 角色定义 + 工具说明 + 思考流程 + 注意事项
- **关键要素**:
  - 明确角色定位
  - 列出可用工具
  - 指导思考步骤
  - 提供示例

#### 3. 工具调用策略
- **何时调用**: 需要外部信息或无法直接回答时
- **选择标准**: 根据问题类型匹配最合适的工具
- **调用限制**: 最大调用次数防止无限循环

### 实践任务

#### 任务1: 分析ReActAgent代码（1.5小时）
重点分析`ReActAgent.java`的execute方法：

```java
public AgentResponse execute(String conversationId, String userInput) {
    // 1. 创建或获取对话
    // 2. 构建消息列表
    // 3. ReAct循环
    // 4. 生成最终回答
    // 5. 返回响应
}
```

理解每个步骤的作用和实现。

#### 任务2: 优化系统提示词（1小时）

**当前提示词**:
```java
private static final String REACT_SYSTEM_PROMPT = """
    你是一个AI Agent，具备使用工具的能力...
    """;
```

**优化方向**:
1. 添加更多思考示例
2. 明确工具使用场景
3. 添加错误处理指导
4. 优化输出格式

#### 任务3: 测试复杂场景（1.5小时）

**测试场景**:

1. **多工具协作**
```bash
curl -X POST http://localhost:8080/api/agent/chat \
  -H "Content-Type: application/json" \
  -d '{"message": "搜索AI Agent最新技术，然后帮我总结要点"}'
```

2. **错误处理**
```bash
curl -X POST http://localhost:8080/api/agent/chat \
  -H "Content-Type: application/json" \
  -d '{"message": "读取一个不存在的文件"}'
```

3. **连续推理**
```bash
curl -X POST http://localhost:8080/api/agent/chat \
  -H "Content-Type: application/json" \
  -d '{"message": "列出当前目录，然后读取README.md文件"}'
```

#### 任务4: 性能优化（1小时）

**优化方向**:
- 减少不必要的工具调用
- 优化消息历史管理
- 添加缓存机制
- 改进错误重试逻辑

### 评估标准

- [ ] 深入理解ReAct循环的工作原理
- [ ] 能够设计有效的系统提示词
- [ ] 成功测试复杂场景
- [ ] 优化后的Agent表现更稳定
- [ ] 理解工具调用的决策逻辑

### 学习资源

- [ReAct论文详细解读](https://arxiv.org/abs/2210.03629)
- [提示工程最佳实践](https://platform.openai.com/docs/guides/prompt-engineering)
- [Chain of Thought推理](https://arxiv.org/abs/2201.11903)

---

## Day 4: LLM集成 - 掌握OpenAI API调用

### 学习目标

- 掌握OpenAI API的集成方法
- 理解Function Calling机制
- 优化API调用性能
- 测试不同模型的效果

### 时间分配

| 时间段 | 内容 | 时长 |
|--------|------|------|
| 上午 | 理论学习 | 2小时 |
| 下午 | 代码分析 | 2小时 |
| 下午 | 性能优化 | 1小时 |

### 核心知识点

#### 1. OpenAI API基础
- **端点**: `https://api.openai.com/v1/chat/completions`
- **认证**: Bearer Token (API Key)
- **请求格式**: JSON
- **响应格式**: JSON

#### 2. Function Calling
- **作用**: 让LLM能够调用外部工具
- **流程**:
  1. 发送工具定义
  2. LLM返回工具调用请求
  3. 执行工具获取结果
  4. 将结果反馈给LLM
  5. LLM生成最终回答

#### 3. Token管理
- **输入Token**: 消息内容
- **输出Token**: 生成内容
- **成本控制**: 限制最大Token数
- **优化策略**: 压缩历史消息

### 实践任务

#### 任务1: 分析OpenAIService代码（1.5小时）
重点分析：
- `chatCompletion()` - 普通对话
- `chatCompletionWithTools()` - 工具调用
- HTTP请求构建
- 响应解析

#### 任务2: 测试不同模型（1小时）

**支持的模型**:
- `gpt-3.5-turbo-0613` - 快速、经济
- `gpt-4-0613` - 强大、昂贵
- `gpt-4-turbo-preview` - 最新、平衡

**测试方法**:
修改`application.yml`中的模型配置，对比效果。

#### 任务3: 性能优化（1.5小时）

**优化方向**:

1. **连接池配置**
```java
HttpClient client = HttpClient.newBuilder()
    .version(HttpClient.Version.HTTP_2)
    .connectTimeout(Duration.ofSeconds(10))
    .executor(Executors.newFixedThreadPool(10))
    .build();
```

2. **请求重试机制**
```java
int maxRetries = 3;
for (int i = 0; i < maxRetries; i++) {
    try {
        return sendRequest(request);
    } catch (Exception e) {
        if (i == maxRetries - 1) throw e;
        Thread.sleep(1000 * (i + 1));
    }
}
```

3. **响应缓存**
```java
@Cacheable(value = "openaiResponses", key = "#messages.hashCode()")
public String chatCompletion(List<Message> messages) {
    // ...
}
```

#### 任务4: 成本分析（1小时）

**Token计算**:
```bash
# 安装tiktoken
pip install tiktoken

# 计算Token数
python -c "import tiktoken; enc = tiktoken.encoding_for_model('gpt-3.5-turbo'); print(len(enc.encode('你的文本')))"
```

**成本估算**:
- GPT-3.5: $0.002/1K tokens
- GPT-4: $0.03/1K tokens

### 评估标准

- [ ] 掌握OpenAI API的调用方法
- [ ] 理解Function Calling的工作原理
- [ ] 成功优化API调用性能
- [ ] 了解不同模型的差异和适用场景
- [ ] 能够进行成本分析和优化

### 学习资源

- [OpenAI API文档](https://platform.openai.com/docs/api-reference)
- [Function Calling指南](https://platform.openai.com/docs/guides/function-calling)
- [Token计算器](https://platform.openai.com/tokenizer)

---

## Day 5: 综合实践 - 完整项目开发与部署

### 学习目标

- 完成综合功能开发
- 实现Docker容器化部署
- 进行性能测试和优化
- 编写学习总结报告

### 时间分配

| 时间段 | 内容 | 时长 |
|--------|------|------|
| 上午 | 功能开发 | 2.5小时 |
| 下午 | 部署测试 | 2小时 |
| 下午 | 总结报告 | 0.5小时 |

### 实践任务

#### 任务1: 完善功能（2小时）

**待完善功能**:

1. **用户认证**
```java
@RestController
@RequestMapping("/api/auth")
public class AuthController {
    
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request) {
        // 实现JWT认证
    }
}
```

2. **数据持久化**
```java
@Entity
public class Conversation {
    @Id
    private String id;
    @OneToMany
    private List<Message> messages;
    // ...
}
```

3. **日志记录**
```java
@Slf4j
@Component
public class LoggingAspect {
    @Around("execution(* com.example.aigent..*(..))")
    public Object logExecution(ProceedingJoinPoint joinPoint) {
        // 实现AOP日志
    }
}
```

#### 任务2: Docker部署（1.5小时）

**1. 构建Docker镜像**
```bash
docker build -t ai-agent-demo:latest .
```

**2. 运行容器**
```bash
docker run -d \
  --name ai-agent \
  -p 8080:8080 \
  -e OPENAI_API_KEY=your-key \
  ai-agent-demo:latest
```

**3. Docker Compose**
```yaml
version: '3.8'
services:
  ai-agent:
    build: .
    ports:
      - "8080:8080"
    environment:
      - OPENAI_API_KEY=${OPENAI_API_KEY}
    volumes:
      - ./data:/app/data
```

#### 任务3: 性能测试（1小时）

**测试工具**: Apache Bench (ab)

```bash
# 安装ab
# Windows: 下载Apache HTTP Server
# Mac: brew install httpd
# Linux: sudo apt-get install apache2-utils

# 测试API性能
ab -n 100 -c 10 http://localhost:8080/api/agent/health

# 测试聊天接口
ab -n 50 -c 5 -p chat.json -T application/json \
  http://localhost:8080/api/agent/chat
```

**性能指标**:
- 响应时间
- 吞吐量
- 错误率
- 资源占用

#### 任务4: 学习总结（1.5小时）

**总结报告内容**:

1. **学习成果**
   - 掌握的核心技术
   - 完成的功能模块
   - 解决的关键问题

2. **技术亮点**
   - ReAct框架实现
   - 工具系统设计
   - 性能优化方案

3. **遇到的问题**
   - 问题描述
   - 解决方案
   - 经验教训

4. **未来展望**
   - 功能扩展方向
   - 技术改进计划
   - 应用场景探索

### 评估标准

- [ ] 完成所有计划功能
- [ ] 成功部署到Docker
- [ ] 性能测试通过
- [ ] 完成学习总结报告
- [ ] 能够独立开发和部署AI Agent应用

### 学习资源

- [Docker官方文档](https://docs.docker.com/)
- [Spring Boot部署指南](https://docs.spring.io/spring-boot/docs/current/reference/html/deployment.html)
- [性能测试最佳实践](https://www.baeldung.com/java-performance-testing)

---

## 学习建议

### 每日学习节奏

1. **上午**: 理论学习 + 代码分析
2. **下午**: 实践开发 + 测试验证
3. **晚上**: 复习总结 + 问题记录

### 学习方法

1. **理论结合实践**: 先理解概念，再动手实现
2. **循序渐进**: 从简单到复杂，逐步深入
3. **问题驱动**: 遇到问题主动查找解决方案
4. **记录总结**: 每天记录学习笔记和心得

### 常见问题

**Q1: API调用失败怎么办？**
- 检查API Key是否正确
- 确认网络连接正常
- 查看错误日志定位问题

**Q2: 工具调用没有响应？**
- 检查工具是否正确注册
- 确认参数格式是否正确
- 查看工具执行日志

**Q3: 如何提高响应速度？**
- 优化系统提示词
- 减少不必要的工具调用
- 使用更快的模型
- 添加缓存机制

## 总结

通过这5天的系统化学习，您将：

1. **掌握AI Agent核心技术**: ReAct框架、工具系统、LLM集成
2. **具备独立开发能力**: 能够设计和实现AI Agent应用
3. **理解最佳实践**: 掌握性能优化和部署方法
4. **建立知识体系**: 形成完整的技术认知

祝您学习顺利！
