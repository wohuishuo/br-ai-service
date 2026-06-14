package com.bookrealm.ai;

import com.bookrealm.ai.dto.AiDtos.BookDetailDto;
import com.bookrealm.ai.dto.AiDtos.ChapterDetailDto;
import com.bookrealm.ai.dto.AiDtos.ChapterItemDto;
import com.bookrealm.ai.dto.AiDtos.ParagraphDto;
import com.bookrealm.ai.service.LibraryClient;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(properties = {
        "spring.ai.openai.api-key=dummy-key-app-boots-but-llm-calls-fail"
})
@AutoConfigureMockMvc
class AiControllerTest {
    @Autowired
    MockMvc mockMvc;

    @MockBean
    LibraryClient libraryClient;

    @Test
    void summaryShouldReturnLocalFallbackWhenNoKey() throws Exception {
        mockMvc.perform(post("/ai/summary")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"chapterText":"灵根育孕源流出，心性修持大道生。这里是一段很长很长的章节文本，用来验证没有 key 时服务仍然能给出友好摘要。"}
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(0))
                .andExpect(jsonPath("$.data.llmUsed").value(false));
    }

    @Test
    void embedAndAskShouldReturnReferencesWithoutKey() throws Exception {
        when(libraryClient.bookDetail(1L)).thenReturn(new BookDetailDto(
                1L, "西游记", "吴承恩", "神魔小说",
                List.of(new ChapterItemDto(1L, 1, "灵根育孕源流出"))
        ));
        when(libraryClient.chapterDetail(1L)).thenReturn(new ChapterDetailDto(
                1L, 1L, 1, "灵根育孕源流出",
                List.of(
                        new ParagraphDto(1L, 1, "盖闻天地之数，有十二万九千六百岁为一元。"),
                        new ParagraphDto(2L, 2, "那座山正当顶上，有一块仙石。其石有三丈六尺五寸高。"),
                        new ParagraphDto(3L, 3, "内育仙胞，一日迸裂，产一石卵，似圆球样大。")
                )
        ));

        mockMvc.perform(post("/ai/embed")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"bookId\":1}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.documentCount").value(3));

        mockMvc.perform(post("/ai/ask")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"bookId":1,"question":"仙石是什么"}
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(0))
                .andExpect(jsonPath("$.data.llmUsed").value(false))
                .andExpect(jsonPath("$.data.references[0].content").value("那座山正当顶上，有一块仙石。其石有三丈六尺五寸高。"));
    }
}
