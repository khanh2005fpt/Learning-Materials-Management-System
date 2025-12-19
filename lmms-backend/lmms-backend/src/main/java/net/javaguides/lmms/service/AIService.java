package net.javaguides.lmms.service;

import lombok.RequiredArgsConstructor;
import net.javaguides.lmms.dto.AIRequestDTO;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.util.retry.Retry;

import java.time.Duration;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class AIService {

    @Value("${spring.ai.openai.api-key}")
    private String apiKey;

    @Value("${spring.ai.openai.api.url}")
    private String apiUrl;

    public String generate(AIRequestDTO requestDTO) {

        Map<String, Object> payload = Map.of(
                "model", "gpt-4o-mini",
                "messages", List.of(
                        Map.of("role", "user", "content", requestDTO.getQuestion())
                )
        );

        WebClient webClient = WebClient.builder()
                .baseUrl(apiUrl) // https://api.openai.com/v1
                .defaultHeader("Authorization", "Bearer " + apiKey)
                .defaultHeader("Content-Type", "application/json")
                .build();

        Map<String, Object> response = webClient.post()
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(payload)
                .retrieve()
                .bodyToMono(Map.class)
                .retryWhen(Retry.backoff(3, Duration.ofSeconds(2))) // retry nếu 429
                .block();

        if (response == null) return "No response";

        List<Map<String, Object>> choices = (List<Map<String, Object>>) response.get("choices");
        if (choices == null || choices.isEmpty()) return "No choices returned";

        Map<String, Object> message = (Map<String, Object>) choices.get(0).get("message");
        if (message == null || message.get("content") == null) return "No content returned";

        return message.get("content").toString();
    }
}
