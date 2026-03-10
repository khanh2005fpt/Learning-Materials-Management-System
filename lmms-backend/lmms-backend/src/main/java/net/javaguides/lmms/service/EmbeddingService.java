package net.javaguides.lmms.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.*;

@Service
@Slf4j
public class EmbeddingService {

    private final RestTemplate restTemplate = new RestTemplate();

    private String normalizeText(String text) {

        if (text == null) return "";

        return text
                .replace("\t", " ")
                .replace("\n", " ")
                .replace("\r", " ")
                .replaceAll("\\s+", " ")
                .trim();
    }

    public float[] generateEmbedding(String text) {
        text = normalizeText(text);

        long startTime = System.currentTimeMillis();

        log.info("Starting embedding generation...");
        log.info("Input text length: {}", text.length());

        String url = "http://localhost:11434/api/embeddings";

        Map<String, Object> body = new HashMap<>();
        body.put("model", "nomic-embed-text");
        body.put("prompt", text);


        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<Map<String, Object>> request = new HttpEntity<>(body, headers);

        ResponseEntity<Map> response =
                restTemplate.postForEntity(url, request, Map.class);


        List<Double> embedding = (List<Double>) response.getBody().get("embedding");

        if (embedding == null) {
            log.error("Embedding response is null!");
            return new float[0];
        }

        log.info("Embedding size returned: {}", embedding.size());

        float[] vector = new float[embedding.size()];

        for (int i = 0; i < embedding.size(); i++) {
            vector[i] = embedding.get(i).floatValue();
        }

        long endTime = System.currentTimeMillis();

        log.info("Embedding generation completed in {} ms", (endTime - startTime));

        return vector;
    }

    public float[] generateContentEmbedding(String text) {
        log.info("Generating content embedding...");
        return generateEmbedding(text);
    }

    public float[] generateQuestionEmbedding(String text) {
        log.info("Generating question embedding...");
        return generateEmbedding(text);
    }
}