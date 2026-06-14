package com.bookrealm.ai.service;

import com.bookrealm.ai.exception.BusinessException;
import com.bookrealm.ai.common.ErrorCode;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.util.List;
import java.util.Map;

@Component
public class DeepSeekClient {
    private final String apiKey;
    private final String model;
    private final RestClient restClient;

    public DeepSeekClient(@Value("${spring.ai.openai.api-key}") String apiKey,
                          @Value("${spring.ai.openai.chat.options.model:deepseek-chat}") String model,
                          @Value("${spring.ai.openai.base-url}") String baseUrl) {
        this.apiKey = apiKey;
        this.model = model;
        this.restClient = RestClient.builder().baseUrl(baseUrl).build();
    }

    public boolean keyConfigured() {
        return apiKey != null && !apiKey.isBlank() && !apiKey.startsWith("dummy-key");
    }

    public String chat(String prompt) {
        if (!keyConfigured()) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "未配置 DEEPSEEK_API_KEY");
        }
        Map<String, Object> body = Map.of(
                "model", model,
                "messages", List.of(Map.of("role", "user", "content", prompt)),
                "temperature", 0.2
        );
        try {
            @SuppressWarnings("unchecked")
            Map<String, Object> response = restClient.post()
                    .uri("/chat/completions")
                    .contentType(MediaType.APPLICATION_JSON)
                    .header("Authorization", "Bearer " + apiKey)
                    .body(body)
                    .retrieve()
                    .body(Map.class);
            List<?> choices = (List<?>) response.get("choices");
            Map<?, ?> first = (Map<?, ?>) choices.getFirst();
            Map<?, ?> message = (Map<?, ?>) first.get("message");
            return String.valueOf(message.get("content"));
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "LLM 调用失败: " + e.getMessage());
        }
    }
}
