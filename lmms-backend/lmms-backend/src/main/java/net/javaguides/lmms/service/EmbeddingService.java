package net.javaguides.lmms.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.genai.Client;
import com.google.genai.types.ContentEmbedding;
import com.google.genai.types.EmbedContentConfig;
import com.google.genai.types.EmbedContentResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

/**
 * EmbeddingService - Gemini embedding version
 * Features:
 * 1) L2-normalized embeddings
 * 2) In-memory cache
 * 3) Retry + exponential backoff
 * 4) Deterministic fallback
 * 5) Raw logging for debugging
 */
@Service
@Slf4j
public class EmbeddingService {

    private final Client googleClient;
    private static final String MODEL = "gemini-embedding-001";
    private static final int DIMENSION = 768;

    private final Map<String, float[]> cache = new ConcurrentHashMap<>();
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Autowired
    public EmbeddingService(Client googleGenAIClient) {
        this.googleClient = googleGenAIClient;
        log.info("EmbeddingService initialized with model {}", MODEL);
    }

    public float[] extractEmbeddingFromResponse(EmbedContentResponse resp) {
        if (resp == null || resp.embeddings() == null || resp.embeddings().isEmpty()) {
            return null;
        }

        // unwrap embeddings
        if (resp.embeddings().isPresent()) {
            List<ContentEmbedding> list = resp.embeddings().get();

            if (list != null && !list.isEmpty()) {
                ContentEmbedding first = list.get(0);

                if (first.values() != null && first.values().isPresent() && !first.values().get().isEmpty()) {
                    List<Float> values = first.values().get();
                    float[] embedding = new float[values.size()];
                    for (int i = 0; i < values.size(); i++) embedding[i] = values.get(i);
                    return embedding;
                }
            }
        }

        // fallback nếu không có embedding
        return null;
    }

    public float[] generateEmbedding(String text) {
        if (text == null) return generateZeroEmbedding();

        String key = sha256Hex(text);
        float[] cached = cache.get(key);
        if (cached != null) return cached.clone();

        float[] embedding = null;
        int maxRetries = 3;
        long backoffMs = 200;

        for (int attempt = 1; attempt <= maxRetries; attempt++) {
            try {
                if (googleClient == null) break;
                EmbedContentConfig config = EmbedContentConfig.builder()
                        .outputDimensionality(768)
                        .build();
                EmbedContentResponse resp = googleClient.models.embedContent(MODEL, text, config);
                // Log raw response as JSON
                try {
                    String rawJson = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(resp);
                    log.info("Embedding input text: '{}', length {}", text, text.length());
                    log.info("Raw embedding JSON:\n{}", rawJson);
                } catch (Exception e) {
                    log.warn("Cannot convert response to JSON: {}", e.getMessage());
                }

                log.info("MODEL used: {}", MODEL);

                embedding = extractEmbeddingFromResponse(resp);

                if (embedding == null) {
                    log.error("Embedding extraction returned NULL");
                } else {
                    log.info("Embedding extracted size: {}", embedding.length);
                }

                if (embedding != null && embedding.length > 0) break;

            } catch (Exception e) {
                log.debug("Embedding API attempt {} failed: {}", attempt, e.getMessage());
            }

            try { TimeUnit.MILLISECONDS.sleep(backoffMs); } catch (InterruptedException ignored) {}
            backoffMs *= 2;
        }

        if (embedding == null || embedding.length == 0) {
            log.warn("Using deterministic fallback embedding for text (length {}): '{}'...", text.length(), preview(text));
            embedding = generateFallbackEmbedding(text);
        }

        // Ensure correct dimension
        if (embedding.length != DIMENSION) {
            float[] adapted = new float[DIMENSION];
            System.arraycopy(embedding, 0, adapted, 0, Math.min(embedding.length, DIMENSION));
            embedding = adapted;
        }
//        log.info("Embedding extracted length: {}",
//                embedding == null ? 0 : embedding.length);

        float[] normalized = l2Normalize(embedding);
        cache.put(key, normalized.clone());
        return normalized;
    }

    public float[] generateQuestionEmbedding(String question) {
        return generateEmbedding(question);
    }

    public float[] generateContentEmbedding(String content) {
        return generateEmbedding(content);
    }

    // --- Helpers -------------------------------------------------

    private String sha256Hex(String s) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] d = md.digest(s.getBytes(StandardCharsets.UTF_8));
            StringBuilder sb = new StringBuilder();
            for (byte b : d) sb.append(String.format("%02x", b));
            return sb.toString();
        } catch (Exception e) {
            return Integer.toHexString(Objects.hashCode(s));
        }
    }

    private String preview(String s) {
        if (s == null) return "";
        return s.length() <= 32 ? s : s.substring(0, 32) + "...";
    }

    private float[] l2Normalize(float[] v) {
        double sum = 0.0;
        for (float x : v) sum += (double) x * x;
        if (sum == 0.0) return generateZeroEmbedding();
        double norm = Math.sqrt(sum);
        float[] out = new float[v.length];
        for (int i = 0; i < v.length; i++) out[i] = (float) (v[i] / norm);
        return out;
    }

    private float[] generateZeroEmbedding() {
        return new float[DIMENSION];
    }

    private float[] generateFallbackEmbedding(String text) {
        byte[] hash;
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            md.update((text != null ? text : "").getBytes(StandardCharsets.UTF_8));
            hash = md.digest();
        } catch (Exception e) {
            hash = Long.toString((text != null ? text.hashCode() : System.nanoTime())).getBytes(StandardCharsets.UTF_8);
        }

        float[] vec = new float[DIMENSION];
        long seed = ByteBuffer.wrap(Arrays.copyOf(hash, 8)).getLong();
        java.util.Random rnd = new java.util.Random(seed);
        for (int i = 0; i < DIMENSION; i++) {
            vec[i] = rnd.nextFloat() * 2f - 1f; // [-1,1]
        }
        return l2Normalize(vec);
    }
}