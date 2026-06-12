package com.bookrealm.ai.controller;

import com.bookrealm.ai.common.BaseResponse;
import com.bookrealm.ai.common.ResultUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "系统")
@RestController
public class HealthController {

    @Value("${spring.ai.openai.api-key}")
    private String apiKey;

    @Operation(summary = "健康检查(含 LLM key 是否已配置)")
    @GetMapping("/health")
    public BaseResponse<String> health() {
        boolean keyConfigured = apiKey != null && !apiKey.startsWith("dummy-key");
        return ResultUtils.success("br-ai-service is up; llmKeyConfigured=" + keyConfigured);
    }
}
