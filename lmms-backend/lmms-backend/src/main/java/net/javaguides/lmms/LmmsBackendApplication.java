package net.javaguides.lmms;

import net.javaguides.lmms.service.AIService;
import net.javaguides.lmms.service.EmbeddingService;
import net.javaguides.lmms.service.JwtService;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

import java.util.Arrays;

@SpringBootApplication
@EnableAsync
public class LmmsBackendApplication {

    public static void main(String[] args) {
        var context = SpringApplication.run(LmmsBackendApplication.class, args);

        JwtService jwtService = context.getBean(JwtService.class);

        String username = "Lala";
        String role = "USER";

        // 1. Tạo token
        String token = jwtService.generateToken(username, role);
        System.out.println("Token: " + token);


//        // 2. Lấy username từ token
//        String extractedUsername = jwtService.extractUsername(token);
//        System.out.println("Username extracted: " + extractedUsername);
//
//        // 3. Lấy role từ token
//        String extractedRole = jwtService.extractRole(token);
//        System.out.println("Role extracted: " + extractedRole);

//        EmbeddingService embeddingService = context.getBean(EmbeddingService.class);
//        AIService aiService = context.getBean(AIService.class);
//        String text1 = "Con gà";
//        String text2 = "chicken";
//
//        float[] v1 = embeddingService.generateEmbedding(text1);
//        float[] v2 = embeddingService.generateEmbedding(text2);
//
//        float sim = aiService.cosineSimilarity(v1, v2);
//        System.out.println("Similarity (AI vs AI VN): " + sim);
//        System.out.println("Vector text1 (first 10 dims): " +
//                Arrays.toString(Arrays.copyOf(v1, 10)));
//
//        System.out.println("Vector text2 (first 10 dims): " +
//                Arrays.toString(Arrays.copyOf(v2, 10)));
    }

}
