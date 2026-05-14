# AI Agent Demo API 文档

## 目录

1. [基础信息](#1-基础信息)
2. [API端点](#2-API端点)
   - 2.1 聊天接口
   - 2.2 工具列表
   - 2.3 对话管理
   - 2.4 健康检查
3. [数据模型](#3-数据模型)
4. [错误处理](#4-错误处理)
5. [示例代码](#5-示例代码)

---

## 1. 基础信息

### 服务地址
- **开发环境**: `http://localhost:8080`
- **API前缀**: `/api/agent`

### Content-Type
- 请求体: `application/json`
- 响应体: `application/json`

### 认证
- 当前版本无需认证
- 生产环境建议添加API Key认证

---

## 2. API端点

### 2.1 聊天接口

**POST** `/api/agent/chat`

发送消息给AI Agent，支持多轮对话

#### 请求参数

| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| conversationId | String | 否 | 对话ID，不传则创建新对话 |
| message | String | 是 | 用户输入消息 |

#### 请求示例

```json
{
  "conversationId": "550e8400-e29b-41d4-a716-446655440000",
  "message": "帮我搜索AI Agent最新技术"
}
```

#### 响应示例

```json
{
  "conversationId": "550e8400-e29b-41d4-a716-446655440000",
  "answer": "根据最新搜索结果，AI Agent技术正在快速发展...",
  "thoughts": [
    "用户需要了解AI Agent最新技术",
    "调用web_search工具搜索相关信息",
    "整理搜索结果并总结"
  ],
  "toolsUsed": ["web_search"],
  "toolUsed": true,
  "responseTimeMs": 2350
}
```

#### 响应字段说明

| 字段 | 类型 | 说明 |
|------|------|------|
| conversationId | String | 对话唯一ID |
| answer | String | AI的最终回答 |
| thoughts | Array | 思考过程列表 |
| toolsUsed | Array | 使用的工具列表 |
| toolUsed | Boolean | 是否使用了工具 |
| responseTimeMs | Number | 响应时间（毫秒） |

---

### 2.2 获取工具列表

**GET** `/api/agent/tools`

获取当前可用的工具列表

#### 请求参数

无

#### 响应示例

```json
{
  "count": 5,
  "tools": [
    "web_search: 用于搜索互联网获取最新信息",
    "fetch_webpage: 获取网页内容，提取标题、正文和链接",
    "read_file: 读取文件内容",
    "list_directory: 列出指定目录下的文件和文件夹",
    "manage_todo: 管理待办事项"
  ]
}
```

---

### 2.3 对话管理

#### 2.3.1 获取所有对话

**GET** `/api/agent/conversations`

获取所有对话列表

**响应示例**:
```json
{
  "count": 3,
  "conversation_ids": [
    "550e8400-e29b-41d4-a716-446655440000",
    "7c9e6679-7425-40de-944b-e07fc1f90ae7",
    "a1b2c3d4-e5f6-7890-abcd-ef1234567890"
  ]
}
```

#### 2.3.2 删除对话

**DELETE** `/api/agent/conversations/{id}`

删除指定对话

**路径参数**:
| 参数 | 类型 | 说明 |
|------|------|------|
| id | String | 对话ID |

**响应示例**:
```json
{
  "success": true,
  "message": "对话已删除"
}
```

---

### 2.4 健康检查

**GET** `/api/agent/health`

检查服务状态

#### 响应示例

```json
{
  "status": "UP",
  "service": "AI Agent Demo",
  "tool_count": 5,
  "conversation_count": 0
}
```

---

## 3. 数据模型

### 3.1 Message（消息）

```json
{
  "role": "user",
  "content": "消息内容",
  "toolName": "工具名称",
  "toolCallId": "工具调用ID"
}
```

| 字段 | 类型 | 说明 |
|------|------|------|
| role | String | 角色：system/user/assistant/tool |
| content | String | 消息内容 |
| toolName | String | 工具名称（tool角色时） |
| toolCallId | String | 工具调用ID |

### 3.2 Conversation（对话）

```json
{
  "id": "550e8400-e29b-41d4-a716-446655440000",
  "createdAt": "2024-01-15T10:30:00",
  "updatedAt": "2024-01-15T10:35:00",
  "messages": [...],
  "title": "新对话"
}
```

### 3.3 ToolCall（工具调用）

```json
{
  "id": "call_abc123",
  "toolName": "web_search",
  "arguments": "{\"query\": \"AI Agent\"}"
}
```

---

## 4. 错误处理

### 错误响应格式

```json
{
  "answer": "Error: 错误描述",
  "toolUsed": false,
  "responseTimeMs": 0
}
```

### 常见错误码

| 状态码 | 说明 |
|--------|------|
| 400 | 请求参数错误 |
| 404 | 资源未找到 |
| 500 | 服务器内部错误 |
| 503 | 服务不可用 |

### 错误示例

```json
{
  "answer": "Error: API请求失败",
  "toolUsed": false,
  "responseTimeMs": 0
}
```

---

## 5. 示例代码

### 5.1 cURL示例

```bash
# 发送消息
curl -X POST http://localhost:8080/api/agent/chat \
  -H "Content-Type: application/json" \
  -d '{"message": "你好"}'

# 获取工具列表
curl http://localhost:8080/api/agent/tools

# 获取对话列表
curl http://localhost:8080/api/agent/conversations

# 删除对话
curl -X DELETE http://localhost:8080/api/agent/conversations/{id}
```

### 5.2 JavaScript示例

```javascript
async function sendMessage(message, conversationId = '') {
  const response = await fetch('/api/agent/chat', {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json'
    },
    body: JSON.stringify({
      conversationId,
      message
    })
  });
  return await response.json();
}

// 使用示例
sendMessage('帮我搜索AI Agent').then(data => {
  console.log(data.answer);
});
```

### 5.3 Python示例

```python
import requests

def send_message(message, conversation_id=''):
    url = 'http://localhost:8080/api/agent/chat'
    data = {
        'conversationId': conversation_id,
        'message': message
    }
    response = requests.post(url, json=data)
    return response.json()

# 使用示例
result = send_message('帮我搜索AI Agent')
print(result['answer'])
```

---

## 附录：工具参数说明

### web_search

| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| query | String | 是 | 搜索关键词 |

### fetch_webpage

| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| url | String | 是 | 网页URL |

### read_file

| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| file_path | String | 是 | 文件路径 |
| search_text | String | 否 | 搜索关键词 |
| chunk_size | Integer | 否 | 分块大小 |

### list_directory

| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| path | String | 否 | 目录路径（默认当前目录） |

### manage_todo

| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| action | String | 是 | 操作类型：add/delete/list/complete |
| task | String | 否 | 任务内容（add时必填） |
| id | Integer | 否 | 任务ID（delete/complete时必填） |

---

**文档版本**: v1.0  
**最后更新**: 2024年
