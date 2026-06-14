# 实现笔记

> **结论先行**:AI 功能最容易犯的错是"为了演示效果编造结果"。本项目宁可返回检索依据,也不假装模型已经回答。

## 一、无 key 也必须能启动

`DEEPSEEK_API_KEY` 不存在时,健康检查返回:

```text
llmKeyConfigured=false
```

摘要接口返回本地截断摘要;问答接口返回检索到的原文引用和提示。

## 二、检索排序要看真实问题

第一次问"仙石是什么"时,轻量评分把含"什么"的段落排在了含"仙石"的段落前面。

修复:连续二字命中权重大于零散单字命中。修复后第一引用命中:

```text
那座山正当顶上,有一块仙石...
```

## 三、RAG 比聊天重要

如果没有检索原文,模型可能给出泛泛解释。RAG 的价值是把回答拉回文本现场,让读者知道依据来自哪一段。

## 四、真实验证记录

- `mvn test`:2 tests, 0 failures;
- `GET /api/health`:服务启动且 `llmKeyConfigured=false`;
- `POST /api/ai/summary`:无 key 返回本地摘要;
- `POST /api/ai/embed {"bookId":1}`:返回 `documentCount=39`;
- `POST /api/ai/ask {"bookId":1,"question":"仙石是什么"}`:第一引用命中第 12 段仙石原文。
