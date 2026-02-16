package net.javaguides.lmms.service;

import com.google.genai.Client;
import com.google.genai.types.GenerateContentResponse;
import lombok.RequiredArgsConstructor;
import net.javaguides.lmms.dto.AIRequestDTO;
import net.javaguides.lmms.dto.AIResponseDTO;
import net.javaguides.lmms.dto.FullAIResponseDTO;
import net.javaguides.lmms.entity.BookPageDocument;
import org.springframework.data.elasticsearch.client.elc.NativeQuery;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AIService {

    private final Client client;
    private final ElasticsearchOperations elasticsearchOperations;


    /**
     * Tìm các trang sách dựa trên semantic search (vector similarity search)
     * So sánh embedding của câu hỏi với embedding của nội dung các trang
     * Phương pháp này hiểu được ngữ nghĩa của câu hỏi, không chỉ từ khóa
     */
    public List<AIResponseDTO> searchPages(AIRequestDTO aiRequestDTO) {
        String keyword = aiRequestDTO.getQuestion();

        NativeQuery query = NativeQuery.builder()
                .withQuery(q -> q.multiMatch(m -> m
                        .fields("content")
                        .query(keyword)
                ))
                .withMaxResults(10) // tăng lên để có nhiều nguồn hơn
                .build();

        SearchHits<BookPageDocument> results = elasticsearchOperations.search(query, BookPageDocument.class);

        StringBuilder context = new StringBuilder();
        for (SearchHit<BookPageDocument> hit : results.getSearchHits()) {}
        return results.getSearchHits().stream()
                .sorted((h1, h2) -> Double.compare(h2.getScore(), h1.getScore())) // sắp xếp giảm dần theo score
                .limit(2)
                .map(hit -> {
                    BookPageDocument doc = hit.getContent();
                    String preview = doc.getContent();
                    return new AIResponseDTO(doc.getBookTitle(), doc.getPageNumber(), preview, doc.getPdfPath());
                })
                .toList();
    }




    /**
     * Trả lời câu hỏi của người dùng dựa trên semantic search
     */
    public FullAIResponseDTO askGemini(AIRequestDTO aiRequestDTO) {
        // Sử dụng semantic search để tìm các trang liên quan
        List<AIResponseDTO> pages = searchPages(aiRequestDTO);

        // Gom nội dung các trang sách thành prompt
        StringBuilder context = new StringBuilder("Câu trả lời dựa trên các trang sau:\n");
        List<AIResponseDTO> sources = pages.stream()
                .map(p -> {
                    String snippet = p.getSnippet();
                    context.append("[").append(p.getBookTitle()).append(" - Page ")
                            .append(p.getPageNumber()).append("]: ")
                            .append(snippet).append("\n");
                    return new AIResponseDTO(p.getBookTitle(), p.getPageNumber(), snippet, p.getFilepath());
                })
                .collect(Collectors.toList());

        String prompt = "Dựa trên các trang sách dưới đây, trả lời câu hỏi: \n" +
                context +
                "\nCâu hỏi: " + aiRequestDTO.getQuestion();

        String answer;
        try {
            GenerateContentResponse response = client.models.generateContent(
                    "gemini-2.5-flash",
                    prompt,
                    null
            );

            answer = response.text();
            if (answer != null && !answer.isEmpty()) {
                answer = answer.stripLeading(); // Loại bỏ newline/space đầu
            }

        } catch (com.google.genai.errors.ClientException e) {
            // Bắt lỗi 429 Too Many Requests
            if (e.getMessage().contains("429")) {
                throw new ResponseStatusException(
                        HttpStatus.TOO_MANY_REQUESTS,
                        "Bạn đã vượt giới hạn số lần dùng Gemini API. Vui lòng thử lại sau."
                );
            }
            // Bắt các lỗi khác
            throw new ResponseStatusException(
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    "Đã xảy ra lỗi khi gọi Gemini API: " + e.getMessage()
            );
        }
        return new FullAIResponseDTO(answer, sources);
    }
}
