# 设计说明

> **结论先行**:MVP-4 的核心不是"接一个聊天接口",而是让回答有原文依据。我们先检索书中段落,再把相关段落交给模型。

## 一、边界

AI 服务不存书。书的来源仍然是 MVP-1 书库服务:

- `GET /api/books/{id}` 拿章节目录;
- `GET /api/chapters/{id}` 拿段落。

AI 服务只做摘要、索引和问答。

## 二、RAG 链路

```
POST /api/ai/embed
  → 拉取一本书的所有章节段落
  → 建立本地轻量索引

POST /api/ai/ask
  → 检索 Top-3 相关段落
  → 有 key:组 prompt 调 DeepSeek
  → 无 key:返回检索依据和友好提示
```

## 三、接口

| 接口 | 说明 |
| --- | --- |
| `POST /api/ai/summary` | 章节摘要 |
| `POST /api/ai/embed` | 建索引 |
| `POST /api/ai/ask` | 原文问答 |

## 四、取舍

DeepSeek 当前不提供稳定 embedding 接口。本 MVP 不伪装成"真向量库",而是先用轻量文本检索完成 RAG 结构。后续可以把 `RagService` 内部索引替换为真正的 `VectorStore + embedding model`,Controller 契约不用变。

## 五、验收

- 无 key 服务可启动;
- `mvn test` 不依赖真实 key;
- `embed` 一本书后文档数等于段落数;
- `ask` 返回引用段落;
- 有 key 时才调用 DeepSeek,无 key 不编造回答。
