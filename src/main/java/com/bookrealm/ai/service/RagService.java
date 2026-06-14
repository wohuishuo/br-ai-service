package com.bookrealm.ai.service;

import com.bookrealm.ai.dto.AiDtos.AskRequest;
import com.bookrealm.ai.dto.AiDtos.AskResponse;
import com.bookrealm.ai.dto.AiDtos.BookDetailDto;
import com.bookrealm.ai.dto.AiDtos.ChapterDetailDto;
import com.bookrealm.ai.dto.AiDtos.EmbedResponse;
import com.bookrealm.ai.dto.AiDtos.Reference;
import com.bookrealm.ai.dto.AiDtos.SummaryResponse;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class RagService {
    private final LibraryClient libraryClient;
    private final DeepSeekClient deepSeekClient;
    private final Map<Long, List<ParagraphDocument>> index = new ConcurrentHashMap<>();

    public RagService(LibraryClient libraryClient, DeepSeekClient deepSeekClient) {
        this.libraryClient = libraryClient;
        this.deepSeekClient = deepSeekClient;
    }

    public SummaryResponse summary(String chapterText) {
        String text = normalize(chapterText);
        if (!deepSeekClient.keyConfigured()) {
            return new SummaryResponse(localSummary(text), false, "未配置 DEEPSEEK_API_KEY,返回本地截断摘要");
        }
        String prompt = "请用不超过100字概括下面章节内容,只输出摘要:\n\n" + text;
        return new SummaryResponse(deepSeekClient.chat(prompt), true, "ok");
    }

    public EmbedResponse embedBook(Long bookId) {
        BookDetailDto book = libraryClient.bookDetail(bookId);
        List<ParagraphDocument> docs = new ArrayList<>();
        if (book.chapters() != null) {
            for (var chapter : book.chapters()) {
                ChapterDetailDto detail = libraryClient.chapterDetail(chapter.id());
                if (detail.paragraphs() == null) {
                    continue;
                }
                detail.paragraphs().forEach(paragraph -> docs.add(new ParagraphDocument(
                        bookId,
                        detail.id(),
                        paragraph.seq(),
                        normalize(paragraph.content())
                )));
            }
        }
        index.put(bookId, docs);
        return new EmbedResponse(bookId, docs.size());
    }

    public AskResponse ask(AskRequest request) {
        List<ParagraphDocument> docs = index.computeIfAbsent(request.getBookId(), id -> {
            embedBook(id);
            return index.getOrDefault(id, List.of());
        });
        List<Reference> references = search(docs, request.getChapterId(), request.getQuestion(), request.getSelectedText())
                .stream()
                .map(ParagraphDocument::toReference)
                .toList();

        if (references.isEmpty()) {
            return new AskResponse("没有检索到足够相关的原文段落。", false, references, "no references");
        }
        if (!deepSeekClient.keyConfigured()) {
            return new AskResponse(fallbackAnswer(request.getQuestion(), references), false, references,
                    "未配置 DEEPSEEK_API_KEY,返回检索结果与本地说明");
        }
        String answer = deepSeekClient.chat(buildPrompt(request.getQuestion(), references));
        return new AskResponse(answer, true, references, "ok");
    }

    int indexedDocumentCount(Long bookId) {
        return index.getOrDefault(bookId, List.of()).size();
    }

    List<Reference> searchForTest(Long bookId, Long chapterId, String question, String selectedText) {
        return search(index.getOrDefault(bookId, List.of()), chapterId, question, selectedText)
                .stream()
                .map(ParagraphDocument::toReference)
                .toList();
    }

    private List<ParagraphDocument> search(List<ParagraphDocument> docs, Long chapterId, String question, String selectedText) {
        String query = normalize((selectedText == null ? "" : selectedText) + " " + question);
        return docs.stream()
                .filter(doc -> chapterId == null || doc.chapterId().equals(chapterId))
                .map(doc -> new ScoredDocument(doc, score(doc.content(), query)))
                .filter(scored -> scored.score() > 0)
                .sorted(Comparator.comparingInt(ScoredDocument::score).reversed())
                .limit(3)
                .map(ScoredDocument::document)
                .toList();
    }

    private int score(String content, String query) {
        int score = 0;
        if (content.contains(query)) {
            score += query.length() * 10;
        }
        for (int i = 0; i < query.length() - 1; i++) {
            String gram = query.substring(i, i + 2).trim();
            if (gram.length() == 2 && content.contains(gram)) {
                score += 8;
            }
        }
        for (String token : query.split("\\s+")) {
            if (!token.isBlank() && content.contains(token)) {
                score += token.length() + 2;
            }
        }
        for (int i = 0; i < query.length(); i++) {
            char ch = query.charAt(i);
            if (!Character.isWhitespace(ch) && content.indexOf(ch) >= 0) {
                score++;
            }
        }
        return score;
    }

    private String buildPrompt(String question, List<Reference> references) {
        StringBuilder sb = new StringBuilder();
        sb.append("你是电子书阅读助手。只能根据给定原文回答,并标注引用的段落序号。\n");
        sb.append("问题:").append(question).append("\n\n原文:\n");
        for (Reference ref : references) {
            sb.append("第").append(ref.paragraphSeq()).append("段:").append(ref.content()).append("\n");
        }
        sb.append("\n请给出简洁回答。");
        return sb.toString();
    }

    private String fallbackAnswer(String question, List<Reference> references) {
        Reference first = references.getFirst();
        return "已检索到与「" + question + "」相关的原文。未配置模型 key,先返回最相关依据:第"
                + first.paragraphSeq() + "段「" + first.content() + "」。";
    }

    private String localSummary(String text) {
        if (text.length() <= 100) {
            return text;
        }
        return text.substring(0, 100) + "...";
    }

    private String normalize(String value) {
        return value == null ? "" : value.replaceAll("\\s+", " ").trim();
    }

    private record ParagraphDocument(Long bookId, Long chapterId, int paragraphSeq, String content) {
        Reference toReference() {
            return new Reference(bookId, chapterId, paragraphSeq, content);
        }
    }

    private record ScoredDocument(ParagraphDocument document, int score) {}
}
