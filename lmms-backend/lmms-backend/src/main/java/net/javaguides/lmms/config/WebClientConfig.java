package net.javaguides.lmms.config;

import com.google.genai.Client;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


@Configuration
public class WebClientConfig {


    @Value("${gemini.api.key}")
    private String apiKey; // inject vào field

    @Bean
    public Client googleClient() {
        if (apiKey == null || apiKey.isBlank()) {
            throw new RuntimeException("Google API Key is missing!");
        }
        System.out.println("Injected API Key: " + apiKey);

        return Client.builder()
                .apiKey(apiKey)
                .build();
    }
}
