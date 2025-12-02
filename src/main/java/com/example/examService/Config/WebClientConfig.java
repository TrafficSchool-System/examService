package com.example.examService.Config;

import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class WebClientConfig {

    // Skapar en WebClient.Builder bean med load balancing
    @Bean
    @LoadBalanced
    public WebClient.Builder webClientBuilder() {
        return WebClient.builder();
    }

    // Skapar en WebClient för quizService med Eureka service discovery
    @Bean
    public WebClient quizWebClient(WebClient.Builder builder) {
        return builder
                .baseUrl("http://quiz-service/api/questions")
                .build();
    }
}