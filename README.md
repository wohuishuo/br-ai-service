# 🤖 AI 服务 · br-ai-service

**[书域 BookRealm](https://github.com/wohuishuo/book-realm) 电子书平台的 AI 模块(MVP-4)**

本仓负责让阅读变聪明:从书库拉取章节段落,建立轻量 RAG 索引,让用户围绕原文做摘要和问答。

> ✅ MVP-4 第一版已完成:摘要、embed、ask 三个接口可用;无 `DEEPSEEK_API_KEY` 也能启动并返回检索依据;有 key 时可调用 DeepSeek 生成回答。

## 它解决什么

AI 不能凭空回答一本书的问题。正确链路是:

```
书库服务(书/章/段)
   │
AI 服务 embed 一本书
   │
用户提问 → 检索相关段落 → 组 prompt → DeepSeek 回答
```

没有 key 时,服务仍返回检索到的原文引用,不编造模型回答。

## 快速开始

```powershell
# 可选:配置 DeepSeek key
[Environment]::SetEnvironmentVariable("DEEPSEEK_API_KEY","sk-xxx","User")

# 确保书库服务 :8082 已启动
mvn spring-boot:run
curl http://localhost:8084/api/health
```

Swagger: <http://localhost:8084/api/swagger-ui.html>

## API

| 方法 | 路径 | 说明 |
| --- | --- | --- |
| GET | `/api/health` | 健康检查,显示 key 是否配置 |
| POST | `/api/ai/summary` | 章节摘要 |
| POST | `/api/ai/embed` | 从书库拉段落并建立索引 |
| POST | `/api/ai/ask` | 基于原文问答 |

## 真实验证

```powershell
curl -X POST http://localhost:8084/api/ai/embed `
  -H "Content-Type: application/json" `
  -d "{\"bookId\":1}"

curl -X POST http://localhost:8084/api/ai/ask `
  -H "Content-Type: application/json" `
  -d "{\"bookId\":1,\"question\":\"仙石是什么\"}"
```

实测无 key 返回:第一引用命中《西游记》第一回第 12 段"那座山正当顶上,有一块仙石..."。

## 项目文档

| 文档 | 内容 |
| --- | --- |
| [`docs/design.md`](docs/design.md) | RAG 链路、接口、取舍 |
| [`docs/notes.md`](docs/notes.md) | 真实踩坑与验证记录 |
| [平台书 · MVP-4 实战章](https://wohuishuo.github.io/book-realm/project/ai) | 平台视角讲解 |

## 测试

```powershell
mvn test
```

当前 2 条测试:

- 无 key 时摘要接口返回本地友好结果;
- embed + ask 能返回相关原文引用。
