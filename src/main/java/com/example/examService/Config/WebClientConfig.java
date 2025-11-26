package com.example.examService.Config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class WebClientConfig {

    // Skapar en WebClient.Builder bean
    @Bean
    public WebClient.Builder webClientBuilder() {
        return WebClient.builder();
    }

    // Skapar en WebClient för quizService
    @Bean
    public WebClient quizWebClient(WebClient.Builder builder) {
        return builder
                .baseUrl("http://localhost:8081/api/questions")
                .build();
    }
}