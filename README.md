# 🤖 AI 服务 · br-ai-service

**[书域 BookRealm](https://github.com/wohuishuo/book-realm) 电子书平台的 AI 模块(MVP-4)**

书域是拆成 5 个独立模块的电子书平台;本仓负责**让阅读变聪明**:
读者划词提问,服务在该书的章节里检索相关段落(RAG),让 DeepSeek 引用原文回答。

> 🚧 骨架阶段:Spring AI + DeepSeek 接线完成,**无 key 也能启动**(health 会显示 `llmKeyConfigured`);摘要/向量化/问答接口按 [工单](https://github.com/wohuishuo/book-realm/blob/main/工单-MVP4-AI服务.md) 开发中。

## 快速开始

```powershell
# 配 key(没有也能起,只是 LLM 调用会失败)
[Environment]::SetEnvironmentVariable("DEEPSEEK_API_KEY","sk-xxx","User")
mvn spring-boot:run     # :8084,Swagger: http://localhost:8084/api/swagger-ui.html
curl http://localhost:8084/api/health   # 看 llmKeyConfigured 是否 true
```

讲解见平台书:[MVP-4 实战章](https://wohuishuo.github.io/book-realm/project/ai)
