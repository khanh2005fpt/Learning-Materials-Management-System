package net.javaguides.lmms.config;

import com.google.genai.Client;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


@Configuration
public class WebClientConfig {

    @Bean
    public Client getClient() {
        return new Client();
    }
}
