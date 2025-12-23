package net.javaguides.lmms.service;

import com.google.genai.Client;
import com.google.genai.types.GenerateContentResponse;
import lombok.RequiredArgsConstructor;
import net.javaguides.lmms.dto.AIRequestDTO;
import net.javaguides.lmms.dto.AIResponseDTO;
import net.javaguides.lmms.entity.BookPageDocument;
import org.springframework.data.elasticsearch.client.elc.NativeQuery;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AIService {

    private final Client client;
    private final ElasticsearchOperations elasticsearchOperations;

    /**
     * Tìm các trang sách dựa trên từ khóa question
     */
    public List<AIResponseDTO> searchPages(AIRequestDTO aiRequestDTO) {
        String keyword = aiRequestDTO.getQuestion();

        NativeQuery query = NativeQuery.builder()
                .withQuery(q -> q.multiMatch(m -> m
                        .fields("content", "bookTitle")
                        .query(keyword)
                ))
                .withMaxResults(5)
                .build();

        SearchHits<BookPageDocument> results = elasticsearchOperations.search(query, BookPageDocument.class);

        return results.getSearchHits().stream()
                .map(hit -> {
                    BookPageDocument doc = hit.getContent();
                    return new AIResponseDTO(doc.getBookTitle(), doc.getPageNumber(), doc.getContent());
                })
                .collect(Collectors.toList());
    }

    /**
     * Hỏi Gemini AI dựa trên question và các trang sách tìm được
     */
    public String askGemini(AIRequestDTO aiRequestDTO) {
        List<AIResponseDTO> pages = searchPages(aiRequestDTO);

        // Gom nội dung các trang sách thành prompt
        StringBuilder context = new StringBuilder();
        for (AIResponseDTO page : pages) {
            context.append("[").append(page.getBookTitle()).append(" - Page ")
                    .append(page.getPageNumber()).append("]: ")
                    .append(page.getContent()).append("\n");
        }

        String prompt = "Dựa trên các trang sách dưới đây, trả lời câu hỏi: \n" +
                context +
                "\nCâu hỏi: " + aiRequestDTO.getQuestion();

        // Gọi Gemini AI
        GenerateContentResponse response = client.models.generateContent(
                "gemini-2.5-flash",
                prompt,
                null
        );

        return response.text();
    }
}
