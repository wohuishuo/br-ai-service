package com.bookrealm.ai.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public class AiDtos {
    public static class SummaryRequest {
        @NotBlank
        private String chapterText;
        public String getChapterText() { return chapterText; }
        public void setChapterText(String chapterText) { this.chapterText = chapterText; }
    }

    public record SummaryResponse(String summary, boolean llmUsed, String message) {}

    public static class EmbedRequest {
        @NotNull
        private Long bookId;
        public Long getBookId() { return bookId; }
        public void setBookId(Long bookId) { this.bookId = bookId; }
    }

    public record EmbedResponse(Long bookId, int documentCount) {}

    public static class AskRequest {
        @NotNull
        private Long bookId;
        private Long chapterId;
        @NotBlank
        private String question;
        private String selectedText;

        public Long getBookId() { return bookId; }
        public void setBookId(Long bookId) { this.bookId = bookId; }
        public Long getChapterId() { return chapterId; }
        public void setChapterId(Long chapterId) { this.chapterId = chapterId; }
        public String getQuestion() { return question; }
        public void setQuestion(String question) { this.question = question; }
        public String getSelectedText() { return selectedText; }
        public void setSelectedText(String selectedText) { this.selectedText = selectedText; }
    }

    public record Reference(Long bookId, Long chapterId, int paragraphSeq, String content) {}

    public record AskResponse(String answer, boolean llmUsed, List<Reference> references, String message) {}

    public record BaseResponse<T>(int code, T data, String message) {}

    public record BookDetailDto(Long id, String title, String author, String intro, List<ChapterItemDto> chapters) {}

    public record ChapterItemDto(Long id, int seq, String title) {}

    public record ChapterDetailDto(Long id, Long bookId, int seq, String title, List<ParagraphDto> paragraphs) {}

    public record ParagraphDto(Long id, int seq, String content) {}
}
