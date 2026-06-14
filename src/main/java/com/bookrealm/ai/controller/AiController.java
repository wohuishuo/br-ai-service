package com.bookrealm.ai.controller;

import com.bookrealm.ai.common.BaseResponse;
import com.bookrealm.ai.common.ResultUtils;
import com.bookrealm.ai.dto.AiDtos.AskRequest;
import com.bookrealm.ai.dto.AiDtos.AskResponse;
import com.bookrealm.ai.dto.AiDtos.EmbedRequest;
import com.bookrealm.ai.dto.AiDtos.EmbedResponse;
import com.bookrealm.ai.dto.AiDtos.SummaryRequest;
import com.bookrealm.ai.dto.AiDtos.SummaryResponse;
import com.bookrealm.ai.service.RagService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "AI 阅读")
@RestController
@RequestMapping("/ai")
public class AiController {
    private final RagService ragService;

    public AiController(RagService ragService) {
        this.ragService = ragService;
    }

    @Operation(summary = "章节摘要")
    @PostMapping("/summary")
    public BaseResponse<SummaryResponse> summary(@Valid @RequestBody SummaryRequest request) {
        return ResultUtils.success(ragService.summary(request.getChapterText()));
    }

    @Operation(summary = "为一本书建立 RAG 索引")
    @PostMapping("/embed")
    public BaseResponse<EmbedResponse> embed(@Valid @RequestBody EmbedRequest request) {
        return ResultUtils.success(ragService.embedBook(request.getBookId()));
    }

    @Operation(summary = "基于原文问答")
    @PostMapping("/ask")
    public BaseResponse<AskResponse> ask(@Valid @RequestBody AskRequest request) {
        return ResultUtils.success(ragService.ask(request));
    }
}
