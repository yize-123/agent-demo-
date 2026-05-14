# AI Agent 五日学习计划

## 📋 计划概述

本学习计划面向AI Agent领域初学者，通过5天的系统学习，您将掌握AI Agent的核心概念、技术栈及项目实战能力。学习内容从基础概念逐步深入到复杂实现，配合实践项目帮助您建立完整的AI Agent知识体系。

| 天数 | 主题 | 难度 | 预计时长 |
|------|------|------|----------|
| Day 1 | AI Agent基础概念 | ⭐ | 4-6小时 |
| Day 2 | LLM与Prompt工程 | ⭐⭐ | 4-6小时 |
| Day 3 | ReAct框架原理 | ⭐⭐⭐ | 5-7小时 |
| Day 4 | 工具系统与函数调用 | ⭐⭐⭐ | 5-7小时 |
| Day 5 | 项目实战与综合应用 | ⭐⭐⭐⭐ | 6-8小时 |

---

## Day 1: AI Agent基础概念

### 🎯 学习目标

- 理解AI Agent的定义与核心特征
- 掌握AI Agent的四大核心组件
- 了解AI Agent的发展历程与应用场景
- 能够区分Agent与其他AI系统

### 📚 核心内容模块

#### 1.1 AI Agent定义

**什么是AI Agent？**

AI Agent（人工智能代理）是一种能够感知环境、进行自主决策、执行动作的智能系统。与传统的被动响应式AI不同，Agent具备主动推理和持续行动的能力。

```
┌─────────────────────────────────────────────────────────────┐
│                      AI Agent                                │
├─────────────────────────────────────────────────────────────┤
│  感知 (Perceive)  →  推理 (Reason)  →  行动 (Act)  →  学习   │
│       ↑                                              │       │
│       └──────────────────────────────────────────────┘       │
└─────────────────────────────────────────────────────────────┘
```

**AI Agent的核心特征**:

| 特征 | 说明 | 示例 |
|------|------|------|
| 自主性 | 无需人类干预即可完成任务 | 自动规划路线并执行 |
| 反应性 | 感知环境变化并响应 | 检测到障碍物后绕行 |
| 目标导向 | 围绕明确目标行动 | 帮你完成订票任务 |
| 持续性 | 能够进行多步骤推理 | 复杂问题分解求解 |
| 社交能力 | 与其他Agent协作 | 多Agent任务分配 |

#### 1.2 AI Agent发展历程

```
1940s-1950s: 理论萌芽
    ↓ Warren McCulloch & Walter Pitts 神经网络模型
1960s-1970s: 专家系统兴起
    ↓ MYCIN、DENDRAL等专家系统
1980s-1990s: 知识表示与推理
    ↓ 框架、语义网络、本体论
2000s-2010s: 机器学习崛起
    ↓ 统计学习方法、深度学习
2020s至今: 大语言模型时代
    ↓ GPT、BERT等LLM + Agent框架
```

#### 1.3 AI Agent四大核心组件

```
┌─────────────────────────────────────────────────────────────┐
│                    AI Agent 架构                            │
├──────────────────┬──────────────────┬───────────────────────┤
│     感知层        │     决策层        │      执行层           │
│   (Perception)   │   (Decision)     │      (Action)         │
├──────────────────┼──────────────────┼───────────────────────┤
│ • 文本输入       │ • LLM推理        │ • API调用             │
│ • 工具结果       │ • 任务分解       │ • 代码执行            │
│ • 外部数据       │ • 策略选择       │ • 外部系统交互        │
│ • 状态更新       │ • 自我反思       │ • 响应生成            │
└──────────────────┴──────────────────┴───────────────────────┘
         ↑                                       ↓
         └───────────── 记忆层 (Memory) ───────────┘
```

**组件详解**:

| 组件 | 职责 | 关键技术 |
|------|------|----------|
| 感知层 | 接收和处理输入 | NLP、工具调用解析 |
| 决策层 | 分析和规划 | LLM、ReAct、CoT |
| 执行层 | 执行动作和输出 | Function Calling、API |
| 记忆层 | 存储和检索信息 | 向量数据库、RAG |

#### 1.4 AI Agent vs 传统AI

| 对比维度 | 传统AI | AI Agent |
|----------|--------|----------|
| 交互模式 | 单一请求-响应 | 多轮对话循环 |
| 决策方式 | 预设规则/训练模型 | 实时推理生成 |
| 工具使用 | 无 | 支持工具调用 |
| 任务范围 | 单一任务 | 多步骤复杂任务 |
| 自主程度 | 低 | 高 |
| 适应性 | 固定输出 | 动态调整 |

### 🛠️ 实践任务

#### 任务1.1: 分析AI Assistant的Agent特性

打开ChatGPT或其他AI Assistant，分析其是否具备Agent特性：

- [ ] 能否进行多轮对话？（感知+记忆）
- [ ] 能否调用外部工具？（执行能力）
- [ ] 能否分解复杂任务？（推理能力）
- [ ] 能否根据反馈调整？（学习能力）

#### 任务1.2: 识别生活中的Agent应用

列举3个你认为属于AI Agent的应用，并说明其核心组件：

1. **应用名称**: ____________
   - 感知层: ____________
   - 决策层: ____________
   - 执行层: ____________

2. **应用名称**: ____________
   - 感知层: ____________
   - 决策层: ____________
   - 执行层: ____________

3. **应用名称**: ____________
   - 感知层: ____________
   - 决策层: ____________
   - 执行层: ____________

### 📖 推荐学习资源

| 资源类型 | 名称 | 链接/来源 |
|----------|------|----------|
| 论文 | "Artificial Intelligence: A Modern Approach" | Stuart Russell, Peter Norvig |
| 博客 | LLM Agent综述 | OpenAI Research Blog |
| 视频 | AI Agent入门系列 | YouTube: Yannic Kilcher |
| 文档 | LangChain Agent文档 | langchain.ai/docs |

### ✅ 检验方法

- [ ] 能够准确描述AI Agent的四大核心组件
- [ ] 能够区分AI Agent与传统AI系统的差异
- [ ] 能够列举至少3个AI Agent的实际应用
- [ ] 完成实践任务并提交分析报告

---

## Day 2: LLM与Prompt工程

### 🎯 学习目标

- 理解大语言模型(LLM)的工作原理
- 掌握Prompt工程的核心技巧
- 学会设计有效的系统提示词
- 理解上下文学习和思维链提示

### 📚 核心内容模块

#### 2.1 大语言模型基础

**LLM是什么？**

大语言模型是基于深度学习的自然语言处理模型，通过在大规模文本数据上进行预训练，学习语言的模式和结构。

```
输入: "今天天气"
    ↓
┌─────────────────────────────────────┐
│         Transformer 架构            │
│  ┌─────┐    ┌─────┐    ┌─────┐    │
│  │ Att │ →  │ Att │ →  │ Att │    │
│  │ention│   │ention│   │ention│    │
│  └─────┘    └─────┘    └─────┘    │
│       ↑         ↑         ↑        │
│  ┌────────────────────────────────┐ │
│  │   位置编码 + 词嵌入 + 前馈网络  │ │
│  └────────────────────────────────┘ │
└─────────────────────────────────────┘
    ↓
输出: "今天天气晴朗，适合外出"
```

**LLM的关键能力**:

| 能力 | 说明 | 示例 |
|------|------|------|
| 文本生成 | 根据输入续写内容 | 写作、摘要、翻译 |
| 意图识别 | 理解用户真实需求 | 分类、情感分析 |
| 知识推理 | 基于知识进行逻辑推理 | 问答、推理题 |
| 上下文学习 | 从示例中学习新任务 | Few-shot Learning |
| 代码生成 | 理解和生成代码 | Copilot |

#### 2.2 Prompt工程基础

**什么是Prompt？**

Prompt是与LLM交互的输入文本，它决定了模型的输出。

```
┌─────────────────────────────────────────────────────────────┐
│                      Prompt 结构                             │
├─────────────────────────────────────────────────────────────┤
│  [系统提示词]                                                │
│  "你是一个专业的AI助手，擅长解答技术问题"                       │
│                                                              │
│  [上下文信息]                                                │
│  "用户所在城市：北京，当前时间：周一上午"                       │
│                                                              │
│  [用户输入]                                                  │
│  "推荐适合周一早上做的运动"                                    │
│                                                              │
│  [输出格式]                                                  │
│  "请以列表形式给出建议"                                        │
└─────────────────────────────────────────────────────────────┘
```

**核心Prompt技巧**:

| 技巧 | 描述 | 示例 |
|------|------|------|
| 清晰具体 | 明确说明要什么 | ✅ "解释量子力学" vs ❌ "讲物理" |
| 结构化 | 使用格式符 | 使用编号、 bullet points |
| 角色设定 | 赋予AI特定角色 | "你是一位10年经验的架构师" |
| 示例驱动 | 提供输入输出示例 | Few-shot Learning |
| 分解任务 | 分步骤引导 | "首先...然后...最后..." |

#### 2.3 上下文学习 (In-Context Learning)

**定义**: 无需更新模型参数，通过在Prompt中提供示例来引导模型学习新任务。

```
┌─────────────────────────────────────────────────────────────┐
│                 上下文学习示例                                │
├─────────────────────────────────────────────────────────────┤
│  示例1:                                                     │
│  输入: "great的复数是" → 输出: "greats"                      │
│                                                              │
│  示例2:                                                     │
│  输入: "happy的复数是" → 输出: "happier"  ❌ 错误！           │
│                                                              │
│  示例3:                                                     │
│  输入: "careful的复数是" → 输出: "carefuls"                  │
│                                                              │
│  测试:                                                      │
│  输入: "beautiful的复数是" → 输出: "beautifuls"               │
│                                                              │
│  模型从示例中学习到复数规则，而不需要参数更新                  │
└─────────────────────────────────────────────────────────────┘
```

**Few-shot vs Zero-shot**:

| 模式 | 说明 | 适用场景 |
|------|------|----------|
| Zero-shot | 无示例，直接指令 | 简单任务、通用场景 |
| One-shot | 1个示例 | 任务较明确但需引导 |
| Few-shot | 2-5个示例 | 需要特定格式/风格 |
| Many-shot | 更多示例 | 复杂模式学习 |

#### 2.4 思维链提示 (Chain-of-Thought)

**定义**: 通过在Prompt中展示推理过程，引导模型进行逐步推理。

```
┌─────────────────────────────────────────────────────────────┐
│                 思维链提示示例                                │
├─────────────────────────────────────────────────────────────┤
│  普通Prompt:                                                 │
│  "小明有5个苹果，小红给了他3个，小明吃了2个，还剩几个？"           │
│  模型直接输出: 6个                                           │
│                                                              │
│  ─────────────────────────────────────────────────────────   │
│                                                              │
│  思维链Prompt:                                               │
│  "小明有5个苹果，小红给了他3个 → 5+3=8个                      │
│   小明吃了2个 → 8-2=6个                                       │
│   所以还剩6个苹果"                                            │
│                                                              │
│  模型输出包含推理过程，结果更准确                             │
└─────────────────────────────────────────────────────────────┘
```

**思维链的优势**:

- ✅ 提高复杂推理任务的准确性
- ✅ 增强模型的可解释性
- ✅ 便于调试和错误定位
- ✅ 帮助模型处理多步骤问题

### 🛠️ 实践任务

#### 任务2.1: 设计系统提示词

为AI Agent设计一个系统提示词，要求：

1. 明确Agent的角色定位
2. 定义Agent的能力范围
3. 设定输出格式规范
4. 包含安全边界说明

**参考模板**:
```
你是一位[角色]，专门[任务描述]。

你的核心能力：
1. [能力1]
2. [能力2]
3. [能力3]

你必须遵循以下规则：
- [规则1]
- [规则2]

输出格式：
[格式要求]
```

#### 任务2.2: 对比实验

使用不同的Prompt策略完成同一任务，对比效果：

**任务**: "解释什么是机器学习"

| Prompt策略 | Prompt内容 | 输出质量(1-10) |
|------------|-----------|---------------|
| 基础Zero-shot | "解释什么是机器学习" | |
| 角色设定 | "你是一位计算机科学教授，请解释什么是机器学习" | |
| 思维链 | "请先介绍机器学习的定义，然后说明其主要类型，最后给出应用场景" | |
| 格式要求 | "用简洁的语言解释机器学习，并分别用一句话说明监督学习、无监督学习和强化学习的区别" | |

### 📖 推荐学习资源

| 资源类型 | 名称 | 链接 |
|----------|------|------|
| 官方文档 | OpenAI Prompt最佳实践 | platform.openai.com/docs |
| 论文 | "Chain-of-Thought Prompting" | Google Research |
| 课程 | ChatGPT Prompt工程课程 | DeepLearning.AI |
| 工具 | Prompt Engineering Guide | promptengineering.org |

### ✅ 检验方法

- [ ] 能够设计清晰有效的系统提示词
- [ ] 掌握Few-shot和思维链提示技巧
- [ ] 能够针对不同任务选择合适的Prompt策略
- [ ] 完成对比实验并撰写分析报告

---

## Day 3: ReAct框架原理

### 🎯 学习目标

- 理解ReAct框架的核心思想
- 掌握ReAct循环的工作原理
- 理解Reasoning与Acting的结合方式
- 能够在项目中实现ReAct Agent

### 📚 核心内容模块

#### 3.1 ReAct框架概述

**ReAct = Reasoning + Acting**

ReAct是一种让LLM能够交错进行推理和行动的框架，使模型能够动态控制推理过程，并依据推理结果决定下一步行动。

```
┌─────────────────────────────────────────────────────────────┐
│                    ReAct 循环流程                           │
├─────────────────────────────────────────────────────────────┤
│                                                              │
│    ┌──────────┐                                              │
│    │  用户    │                                              │
│    │  输入    │                                              │
│    └────┬─────┘                                              │
│         ↓                                                   │
│    ┌──────────┐                                              │
│    │  推理    │  Thought: 分析问题，决定是否需要工具           │
│    │ Reason   │                                              │
│    └────┬─────┘                                              │
│         ↓                                                   │
│    ┌──────────┐                                              │
│    │  行动    │  Action: 调用web_search工具                   │
│    │  Act     │                                              │
│    └────┬─────┘                                              │
│         ↓                                                   │
│    ┌──────────┐                                              │
│    │  观察    │  Observation: 获取搜索结果                     │
│    │   Obs    │                                              │
│    └────┬─────┘                                              │
│         ↓                                                   │
│    ┌──────────┐                                              │
│    │ 总结回答 │  Response: 基于工具结果生成最终回答             │
│    └──────────┘                                              │
│                                                              │
└─────────────────────────────────────────────────────────────┘
```

#### 3.2 ReAct vs 其他框架对比

| 框架 | 特点 | 优势 | 劣势 |
|------|------|------|------|
| **ReAct** | 推理与行动交错 | 可解释性强、灵活性高 | 需要多轮交互 |
| **CoT** | 纯推理链 | 简单直接 | 无法行动 |
| **ToT** | 树形探索 | 全局优化 | 计算成本高 |
| **Reflexion** | 自我反思 | 错误纠正 | 实现复杂 |

#### 3.3 ReAct Prompt设计

**标准ReAct提示词模板**:

```
你是一个AI Agent，使用ReAct框架进行推理和行动。

在每一步中，你需要：
1. Thought: 分析当前情况，决定下一步行动
2. Action: 执行一个动作（调用工具）
3. Observation: 观察动作结果

可用工具：
- web_search: 搜索互联网信息
- calculator: 执行数学计算
- read_file: 读取文件内容

开始：
Thought: 用户询问的是[问题]，我需要先[动作]
Action: [工具名称]([参数])
Observation: [工具返回结果]
...
```

#### 3.4 ReAct循环终止条件

```
┌─────────────────────────────────────────────────────────────┐
│                   循环终止条件                                │
├─────────────────────────────────────────────────────────────┤
│  条件1: 生成最终回答                                         │
│       → LLM判断无需更多工具调用，直接生成回答                  │
│                                                              │
│  条件2: 达到最大迭代次数                                     │
│       → 防止无限循环（通常设置为5-10次）                      │
│                                                              │
│  条件3: 工具执行失败                                         │
│       → 工具报错或返回无效结果                                │
│                                                              │
│  条件4: 明确要求终止                                         │
│       → 用户主动中断或发送终止指令                            │
└─────────────────────────────────────────────────────────────┘
```

#### 3.5 ReAct在项目中的实现

**项目代码结构**（参考ReActAgent.java）:

```java
@Component
public class ReActAgent {
    
    public AgentResponse execute(String userInput) {
        // 1. 构建初始消息列表
        List<Message> messages = buildMessages(userInput);
        
        // 2. ReAct循环
        int toolCallCount = 0;
        int maxToolCalls = 10;
        
        while (toolCallCount < maxToolCalls) {
            // 2.1 调用LLM获取工具调用指令
            ToolCall toolCall = llmService.chatCompletionWithTools(
                messages, 
                toolRegistry.getToolsAsJsonSchema()
            );
            
            // 2.2 检查是否需要调用工具
            if (!toolCall.isValid()) {
                // 无需工具调用，生成最终回答
                break;
            }
            
            // 2.3 执行工具
            String toolResult = executeTool(toolCall);
            
            // 2.4 添加工具调用消息到历史
            messages.add(Message.tool(toolCall, toolResult));
            
            toolCallCount++;
        }
        
        // 3. 生成最终回答
        String finalAnswer = llmService.chatCompletion(messages);
        
        return AgentResponse.builder()
            .answer(finalAnswer)
            .toolCallCount(toolCallCount)
            .build();
    }
}
```

### 🛠️ 实践任务

#### 任务3.1: 手动模拟ReAct循环

使用ReAct框架解决以下问题，并记录每一步的Thought、Action、Observation：

**问题**: "帮我查询今天北京的天气，并判断是否适合户外运动"

| 步骤 | Thought | Action | Observation |
|------|---------|--------|-------------|
| 1 | 需要查询北京天气 | web_search("北京今天天气") | 晴，气温25℃ |
| 2 | ... | ... | ... |

#### 任务3.2: 实现简单ReAct Agent

在项目中找到ReActAgent.java，理解其实现逻辑，并回答以下问题：

1. ReAct循环的核心变量有哪些？
2. 如何判断是否需要继续循环？
3. 工具调用结果如何传递给LLM？

### 📖 推荐学习资源

| 资源类型 | 名称 | 链接 |
|----------|------|------|
| 论文 | "ReAct: Synergizing Reasoning and Acting" | arXiv:2210.03629 |
| 代码 | LangChain ReAct实现 | github.com/langchain-ai |
| 博客 | AI Agent的推理框架对比 | Towards Data Science |
| 视频 | ReAct实战教程 | YouTube: Samuel |

### ✅ 检验方法

- [ ] 能够完整描述ReAct循环的工作流程
- [ ] 能够设计ReAct风格的提示词
- [ ] 理解Reasoning与Acting的结合方式
- [ ] 能够阅读并理解项目中的ReActAgent实现

---

## Day 4: 工具系统与函数调用

### 🎯 学习目标

- 理解Function Calling的原理
- 掌握Tool接口的设计规范
- 学会创建自定义工具
- 理解工具注册与调用机制

### 📚 核心内容模块

#### 4.1 Function Calling概述

**什么是Function Calling？**

Function Calling是一种让LLM能够调用外部函数/工具的技术，使模型能够执行实际操作而不仅仅是生成文本。

```
┌─────────────────────────────────────────────────────────────┐
│                  Function Calling 流程                       │
├─────────────────────────────────────────────────────────────┤
│                                                              │
│  用户: "北京现在几度？"                                       │
│         ↓                                                    │
│  ┌─────────────────┐                                        │
│  │   LLM 分析      │                                        │
│  │  需要调用工具   │                                        │
│  └────────┬────────┘                                        │
│           ↓                                                  │
│  LLM输出: {                                                  │
│    "tool_calls": [{                                          │
│      "name": "get_weather",                                  │
│      "arguments": {"location": "北京"}                       │
│    }]                                                        │
│  }                                                          │
│           ↓                                                  │
│  ┌─────────────────┐                                        │
│  │   执行工具      │                                        │
│  │ get_weather(    │                                        │
│  │   location="北京"                                       │
│  │ ) → "25°C"     │                                        │
│  └────────┬────────┘                                        │
│           ↓                                                  │
│  ┌─────────────────┐                                        │
│  │   LLM 总结      │                                        │
│  │ "北京现在25度"  │                                        │
│  └────────┬────────┘                                        │
│           ↓                                                  │
│  用户收到回答                                                 │
│                                                              │
└─────────────────────────────────────────────────────────────┘
```

#### 4.2 工具接口设计

**Tool接口标准定义**:

```java
public interface Tool {
    String getName();          // 工具唯一标识
    String getDescription();  // 工具描述（供LLM理解）
    JsonNode getParameters(); // 参数Schema定义
    String execute(JsonNode arguments); // 执行逻辑
}
```

**参数Schema示例**:

```json
{
  "type": "object",
  "properties": {
    "query": {
      "type": "string",
      "description": "搜索关键词"
    },
    "limit": {
      "type": "integer",
      "description": "返回结果数量限制",
      "default": 5
    }
  },
  "required": ["query"]
}
```

#### 4.3 工具注册机制

**Spring IoC自动注册**:

```java
@Service
public class ToolRegistry {
    private final Map<String, Tool> tools = new ConcurrentHashMap<>();
    
    // Spring自动注入所有Tool实现
    public ToolRegistry(List<Tool> toolList) {
        for (Tool tool : toolList) {
            tools.put(tool.getName(), tool);
            log.info("工具已注册: {}", tool.getName());
        }
    }
}
```

**注册流程**:

```
┌─────────────────────────────────────────────────────────────┐
│                    工具注册流程                              │
├─────────────────────────────────────────────────────────────┤
│                                                              │
│  Spring容器启动                                              │
│       ↓                                                      │
│  扫描@Component注解的类                                      │
│       ↓                                                      │
│  发现WebSearchTool、ReadFileTool等实现Tool接口               │
│       ↓                                                      │
│  自动注入到ToolRegistry构造函数                              │
│       ↓                                                      │
│  ToolRegistry.register(tool)                                 │
│       ↓                                                      │
│  工具可用于Agent调用                                          │
│                                                              │
└─────────────────────────────────────────────────────────────┘
```

#### 4.4 OpenAI Function Calling格式

**请求格式**:

```json
{
  "model": "gpt-3.5-turbo-0613",
  "messages": [{"role": "user", "content": "天气怎么样？"}],
  "tools": [
    {
      "type": "function",
      "function": {
        "name": "get_weather",
        "description": "获取指定位置的天气信息",
        "parameters": {
          "type": "object",
          "properties": {
            "location": {
              "type": "string",
              "description": "城市名称"
            }
          },
          "required": ["location"]
        }
      }
    }
  ]
}
```

**响应格式**:

```json
{
  "id": "chatcmpl-xxx",
  "choices": [{
    "message": {
      "role": "assistant",
      "content": null,
      "tool_calls": [{
        "id": "call_abc123",
        "type": "function",
        "function": {
          "name": "get_weather",
          "arguments": "{\"location\": \"北京\"}"
        }
      }]
    }
  }]
}
```

#### 4.5 项目中的5个工具

| 工具名称 | 功能 | 参数 | 返回值 |
|----------|------|------|--------|
| web_search | 网络搜索 | query: string | 搜索结果列表 |
| fetch_webpage | 网页抓取 | url: string | 标题、内容、链接 |
| read_file | 文件读取 | file_path: string, search_text?: string | 文件内容 |
| list_directory | 目录列表 | path?: string | 文件列表 |
| manage_todo | 待办管理 | action: string, task?: string, id?: number | 操作结果 |

### 🛠️ 实践任务

#### 任务4.1: 创建计算器工具

参考项目结构，创建CalculatorTool：

1. 实现Tool接口
2. 支持加减乘除运算
3. 处理异常输入
4. 测试工具功能

**参考代码框架**:

```java
@Component
public class CalculatorTool implements Tool {
    
    @Override
    public String getName() {
        return "calculator";
    }
    
    @Override
    public String getDescription() {
        return "执行数学计算，支持加减乘除运算";
    }
    
    @Override
    public JsonNode getParameters() {
        // 定义参数schema
    }
    
    @Override
    public String execute(JsonNode arguments) {
        // 实现计算逻辑
    }
}
```

#### 任务4.2: 工具调用追踪

使用项目API发送请求，记录完整的工具调用过程：

```bash
curl -X POST http://localhost:8080/api/agent/chat \
  -H "Content-Type: application/json" \
  -d '{"message": "计算 25 + 37 等于多少？"}'
```

观察返回结果中的toolsUsed字段，验证工具是否被正确调用。

### 📖 推荐学习资源

| 资源类型 | 名称 | 链接 |
|----------|------|------|
| 官方文档 | OpenAI Function Calling | platform.openai.com/docs |
| 官方文档 | Azure OpenAI Function Calling | learn.microsoft.com |
| 博客 | LangChain Tools | python.langchain.com/docs |
| 博客 | 函数调用设计模式 | Towards AI |

### ✅ 检验方法

- [ ] 理解Function Calling的工作原理
- [ ] 能够设计符合规范的Tool接口
- [ ] 成功创建并注册自定义工具
- [ ] 能够追踪和调试工具调用过程

---

## Day 5: 项目实战与综合应用

### 🎯 学习目标

- 完成项目的完整构建流程
- 整合所有核心知识点
- 能够进行二次开发和扩展
- 具备AI Agent项目的工程能力

### 📚 核心内容模块

#### 5.1 项目整体架构

```
┌─────────────────────────────────────────────────────────────┐
│                    AI Agent Demo 架构                       │
├─────────────────────────────────────────────────────────────┤
│                                                              │
│  ┌─────────────┐    ┌─────────────┐    ┌─────────────┐      │
│  │   前端      │ →  │  Controller │ →  │   Agent     │      │
│  │  (HTML/CSS) │    │   (REST)    │    │  (ReAct)    │      │
│  └─────────────┘    └─────────────┘    └──────┬──────┘      │
│                                                │              │
│                        ┌──────────────────────┼──────────┐   │
│                        │                      ▼          │   │
│                        │  ┌─────────────────────────┐   │   │
│                        │  │     Tool Registry       │   │   │
│                        │  └────────────┬────────────┘   │   │
│                        │               │                │   │
│                        │   ┌───────────┼───────────┐    │   │
│                        │   ▼           ▼           ▼    │   │
│                        │ [Search]  [ReadFile]  [Todo]   │   │
│                        │  Tools      Tools       Tools  │   │
│                        └───────────────────────────────────┘   │
│                                           │                    │
│                        ┌──────────────────┘                    │
│                        ▼                                      │
│                 ┌─────────────┐                                │
│                 │  OpenAI API │                                │
│                 │   Service   │                                │
│                 └──────┬──────┘                                │
│                        │                                        │
│                        ▼                                        │
│                 ┌─────────────┐                                │
│                 │   OpenAI   │                                │
│                 │    LLM     │                                │
│                 └─────────────┘                                │
│                                                              │
└─────────────────────────────────────────────────────────────┘
```

#### 5.2 模块间交互流程

**完整对话流程**:

```
1. 用户发送消息
   ↓
2. Controller接收请求
   ↓
3. 创建/获取Conversation
   ↓
4. 调用ReActAgent.execute()
   ↓
5. ReAct循环:
   5.1 构建消息列表
   5.2 调用OpenAI Service
   5.3 检查是否需要工具调用
   5.4 执行工具 → 返回结果
   5.5 重复直到无需工具
   ↓
6. 生成最终回答
   ↓
7. 保存到ConversationHistory
   ↓
8. 返回AgentResponse
   ↓
9. Controller返回JSON响应
```

#### 5.3 数据流详解

| 阶段 | 输入 | 处理 | 输出 |
|------|------|------|------|
| 接收请求 | HTTP POST | Controller解析 | ChatRequest对象 |
| 创建对话 | 用户消息 | 创建UUID | conversationId |
| Agent推理 | 用户输入 | ReAct循环 | ToolCall列表 |
| 工具执行 | ToolCall | ToolRegistry | 执行结果 |
| 生成回答 | 工具结果 | LLM总结 | 回答文本 |
| 返回响应 | AgentResponse | JSON序列化 | HTTP响应 |

#### 5.4 关键配置参数

| 配置项 | 默认值 | 说明 |
|--------|--------|------|
| server.port | 8080 | 服务端口 |
| openai.api-key | - | API密钥（必填） |
| openai.model | gpt-3.5-turbo-0613 | 使用的模型 |
| openai.temperature | 0.7 | 温度参数 |
| app.agent.max-tool-calls | 10 | 最大工具调用次数 |
| app.agent.max-history-length | 20 | 最大历史消息数 |
| app.tools.tavily.mock-enabled | true | 是否启用Mock模式 |

#### 5.5 扩展与优化方向

| 方向 | 当前实现 | 扩展建议 |
|------|----------|----------|
| 记忆持久化 | 内存存储 | 引入Redis/数据库 |
| 多Agent协作 | 单Agent | 引入Agent通信协议 |
| RAG增强 | 无 | 集成向量数据库 |
| 安全防护 | 无 | 添加输入验证、限流 |
| 监控日志 | 基础日志 | 引入APM工具 |

### 🛠️ 实践任务

#### 任务5.1: 完整项目复现

按照以下步骤完整复现项目：

- [ ] 克隆/下载项目代码
- [ ] 配置OpenAI API Key
- [ ] 执行mvn clean compile
- [ ] 启动服务mvn spring-boot:run
- [ ] 访问前端页面 http://localhost:8080/index.html
- [ ] 测试至少3个不同的对话场景

#### 任务5.2: 添加新工具

在项目中添加一个货币转换工具：

```java
@Component
public class CurrencyConverterTool implements Tool {
    // 支持: 美元、人民币、欧元、日元之间的转换
    // 参数: from(货币), to(货币), amount(金额)
}
```

**测试用例**:
```bash
curl -X POST http://localhost:8080/api/agent/chat \
  -H "Content-Type: application/json" \
  -d '{"message": "将100美元转换为人民币"}'
```

#### 任务5.3: 前端交互优化

为前端页面添加一个新功能：

- [ ] 添加"清空当前对话"按钮
- [ ] 显示工具调用过程（可选展开）
- [ ] 添加响应时间显示

### 📖 推荐学习资源

| 资源类型 | 名称 | 链接 |
|----------|------|------|
| 官方文档 | Spring Boot文档 | spring.io/projects/spring-boot |
| 官方文档 | Spring AI文档 | spring.io/projects/spring-ai |
| 课程 | Full Stack LLM应用 | Full Stack Deep Learning |
| 书籍 | Building AI Applications | Manning Publications |

### ✅ 检验方法

- [ ] 成功复现整个项目
- [ ] 能够创建并集成自定义工具
- [ ] 理解模块间的交互逻辑
- [ ] 完成至少3个综合实践任务

---

## 📊 学习效果评估表

| 知识点 | Day 1 | Day 2 | Day 3 | Day 4 | Day 5 |
|--------|-------|-------|-------|-------|-------|
| AI Agent基础 | ⭐⭐⭐ | | | | |
| LLM原理 | | ⭐⭐⭐ | | | |
| Prompt工程 | | ⭐⭐⭐ | | | |
| ReAct框架 | | | ⭐⭐⭐ | | |
| Function Calling | | | | ⭐⭐⭐ | |
| 项目实战 | | | | | ⭐⭐⭐ |

---

## 🎓 后续学习路径

完成本计划后，建议继续学习：

1. **LangChain框架** - 快速构建LLM应用
2. **向量数据库** - 实现RAG增强检索
3. **多Agent系统** - Agent协作与通信
4. **自主Agent** - AutoGPT、BabyAGI等
5. **Agent安全** - 对齐、安全防护

---

**计划制定**: AI Agent学习团队  
**适用版本**: AI Agent Demo v1.0  
**学习交流**: 项目Issues页面
