package com.bookrealm.ai;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * 书域 MVP-4 · AI 服务。
 * 职责:章节摘要、RAG 读书问答(向量化 → 检索 → 带引用回答)。
 */
@SpringBootApplication
public class AiApplication {
    public static void main(String[] args) {
        SpringApplication.run(AiApplication.class, args);
    }
}
