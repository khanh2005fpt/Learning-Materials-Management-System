package net.javaguides.lmms.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.genai.Client;
import com.google.genai.types.GenerateContentResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.javaguides.lmms.dto.AIRequestDTO;
import net.javaguides.lmms.dto.AIResponseDTO;
import net.javaguides.lmms.dto.FullAIResponseDTO;
import net.javaguides.lmms.entity.BookPageDocument;
import org.springframework.data.domain.Sort;
import org.springframework.data.elasticsearch.client.elc.NativeQuery;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class AIService {

    private final Client client;
    private final ElasticsearchOperations elasticsearchOperations;
    private final EmbeddingService embeddingService;

    /**
     * Tìm các trang sách dựa trên semantic search (vector similarity search)
     * So sánh embedding của câu hỏi với embedding của nội dung các trang
     * Phương pháp này hiểu được ngữ nghĩa của câu hỏi, không chỉ từ khóa
     */



    public List<AIResponseDTO> searchPages(AIRequestDTO aiRequestDTO) {

        float[] questionEmbedding = embeddingService.generateQuestionEmbedding(aiRequestDTO.getQuestion());
        System.out.println(aiRequestDTO.getQuestion());
        List<Float> vectorQuestion = new ArrayList<>();
        for (float f : questionEmbedding)
        {
            vectorQuestion.add(f);
        }

        NativeQuery query = NativeQuery.builder()
                .withQuery(q -> q.bool(b -> b
                        // should = OR giữa các điều kiện
                        .should(s -> s.knn(k -> k
                                .field("embedding")
                                .queryVector(vectorQuestion)
                                .k(60)
                                .numCandidates(120)
                                .boost(1.2f)
                        ))
                        .should(s -> s.match(m -> m
                                .field("content")           // hoặc "content^2" nếu muốn boost
                                .query(aiRequestDTO.getQuestion())
                                .boost(0.8f)
                        ))
                        // Có thể thêm minimum_should_match để ép phải khớp ít nhất 1 nhánh
                        .minimumShouldMatch("1")
                ))
                .withSort(Sort.by(Sort.Order.desc("_score")))   // sắp xếp theo score tổng
                .withMaxResults(25)     // lấy nhiều hơn để lọc sau
                .build();

        SearchHits<BookPageDocument> results = elasticsearchOperations.search(query, BookPageDocument.class);
        for (SearchHit<BookPageDocument> hit : results) {
            float sim = cosineSimilarity(questionEmbedding, hit.getContent().getEmbedding());
            if(sim >= 0.45f)
            {
                System.out.println("Page " + hit.getContent().getPageNumber() + " sim=" + sim + " Book " + hit.getContent().getBookTitle());
            }
        }

        // Filter theo cosine similarity
        return results.getSearchHits().stream()
                .filter(hit -> cosineSimilarity(questionEmbedding, hit.getContent().getEmbedding()) >= 0.45f)
                .map(SearchHit::getContent)
                .map(doc -> new AIResponseDTO(
                        doc.getBookTitle(),
                        doc.getPageNumber(),
                        doc.getContent(),
                        doc.getPdfPath(),
                        doc.getEmbedding()
                ))
                .limit(10)
                .toList();
    }

    // Hàm cosine similarity
    public float cosineSimilarity(float[] vec1, float[] vec2) {
        float dot = 0f, normA = 0f, normB = 0f;
        for (int i = 0; i < vec1.length; i++) {
            dot += vec1[i] * vec2[i];
            normA += vec1[i] * vec1[i];
            normB += vec2[i] * vec2[i];
        }
        return dot / ((float) Math.sqrt(normA) * (float) Math.sqrt
                (normB) + 1e-10f);
    }

    /**
     * Trả lời câu hỏi của người dùng dựa trên semantic search
     * Prompt được tối ưu để AI:
     *  - Chỉ trả lời dựa trên các tài liệu đã upload
     *  - Liệt kê nguồn nếu cần
     *  - Tránh trả lời ngoài dữ liệu
     */
    public FullAIResponseDTO askGemini(AIRequestDTO aiRequestDTO) {
        // Lấy các trang liên quan bằng semantic search
        List<AIResponseDTO> pages = searchPages(aiRequestDTO);



        // Gom nội dung các trang sách thành prompt
        StringBuilder context = new StringBuilder();
        for (AIResponseDTO p : pages) {
            context.append("[").append(p.getBookTitle())
                    .append(" - Trang ").append(p.getPageNumber())
                    .append("]: ").append(p.getSnippet())
                    .append("\n");
        }

        // Prompt tối ưu: rõ ràng, chỉ dựa trên tài liệu, trả lời chi tiết
        String prompt = """
                Bạn là một trợ lý học thuật thông minh. Trả lời câu hỏi dựa trên các tài liệu đã cung cấp.
                Chỉ sử dụng thông tin từ các trang được liệt kê bên dưới. Nếu thông tin không có trong tài liệu, hãy trả lời "Không có dữ liệu để trả lời".
                Hãy trả lời đầy đủ, kết hợp thông tin từ tất cả các trang. Không bỏ sót thông tin vì bị cắt trang.
                
                --- Tài liệu tham khảo ---
                %s
                --- Hỏi ---
                %s
                
                Hãy trả lời một cách chi tiết, dễ hiểu, và nếu cần, ghi chú nguồn [Tên sách - Số trang].
                """.formatted(context.toString(), aiRequestDTO.getQuestion());

        String answer;
        if (pages.isEmpty()) {
            answer = "Không tìm thấy tài liệu cho câu trả lời";
        }
        try {
            GenerateContentResponse response = client.models.generateContent(
                    "gemini-2.5-flash",
                    prompt,
                    null
            );
            answer = response.text();
            if (answer != null) answer = answer.trim();
        } catch (com.google.genai.errors.ClientException e) {
            if (e.getMessage().contains("429")) {
                throw new ResponseStatusException(
                        HttpStatus.TOO_MANY_REQUESTS,
                        "Bạn đã vượt giới hạn số lần dùng Gemini API. Vui lòng thử lại sau."
                );
            }
            throw new ResponseStatusException(
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    "Đã xảy ra lỗi khi gọi Gemini API: " + e.getMessage()
            );
        }

        return new FullAIResponseDTO(answer, pages);
    }
}