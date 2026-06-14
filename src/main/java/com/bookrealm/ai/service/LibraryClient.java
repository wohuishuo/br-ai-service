package com.bookrealm.ai.service;

import com.bookrealm.ai.dto.AiDtos.BaseResponse;
import com.bookrealm.ai.dto.AiDtos.BookDetailDto;
import com.bookrealm.ai.dto.AiDtos.ChapterDetailDto;
import com.bookrealm.ai.exception.BusinessException;
import com.bookrealm.ai.common.ErrorCode;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Component
public class LibraryClient {
    private final RestClient restClient;
    private final ObjectMapper objectMapper;

    public LibraryClient(@Value("${bookrealm.library-base-url}") String baseUrl, ObjectMapper objectMapper) {
        this.restClient = RestClient.builder().baseUrl(baseUrl).build();
        this.objectMapper = objectMapper;
    }

    public BookDetailDto bookDetail(Long bookId) {
        String json = restClient.get().uri("/books/{id}", bookId).retrieve().body(String.class);
        JavaType type = objectMapper.getTypeFactory().constructParametricType(BaseResponse.class, BookDetailDto.class);
        return readData(json, type);
    }

    public ChapterDetailDto chapterDetail(Long chapterId) {
        String json = restClient.get().uri("/chapters/{id}", chapterId).retrieve().body(String.class);
        JavaType type = objectMapper.getTypeFactory().constructParametricType(BaseResponse.class, ChapterDetailDto.class);
        return readData(json, type);
    }

    private <T> T readData(String json, JavaType type) {
        try {
            BaseResponse<T> response = objectMapper.readValue(json, type);
            if (response.code() != 0 || response.data() == null) {
                throw new BusinessException(ErrorCode.SYSTEM_ERROR, response.message());
            }
            return response.data();
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "书库响应解析失败: " + e.getMessage());
        }
    }
}
