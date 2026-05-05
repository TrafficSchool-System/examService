package com.example.examService.shared.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

/**
 * WebClientConfig - Configuration for service-to-service communication
 * 
 * Purpose:
 * - Configure WebClient beans for calling other microservices
 * - Setup authentication headers (X-Internal-API-Key)
 * - Support for Railway DNS and local development
 * 
 * Services:
 * - QuizService: Fetch exam questions
 * 
 * Architecture:
 * - ExamService → QuizService for question retrieval
 * - Secure service-to-service communication with API key
 */
@Configuration
public class WebClientConfig {

    private static final Logger log = LoggerFactory.getLogger(WebClientConfig.class);

    @Value("${service.api.key}")
    private String serviceApiKey;

    @Value("${quiz.service.url:http://quiz-service}")
    private String quizServiceUrl;

    /**
     * WebClient for QuizService
     * 
     * Railway: quiz.service.url=http://quizservice:8085
     * Local: quiz.service.url=http://localhost:8085
     * 
     * Endpoints called:
     * - GET /api/quizzes/final-exam → Fetch 70 questions for exam
     * 
     * @return Configured WebClient for QuizService
     */
    @Bean
    public WebClient quizWebClient() {
        String fullUrl = quizServiceUrl + "/api/quizzes";

        log.debug("Configuring quizWebClient: url={} apiKeyConfigured={}",
                fullUrl, serviceApiKey != null && !serviceApiKey.isEmpty());

        return WebClient.builder()
                .baseUrl(fullUrl)
                .defaultHeader("Content-Type", "application/json")
                .defaultHeader("X-Internal-API-Key", serviceApiKey)
                .defaultHeader("X-Internal-Source", "exam-service")
                .build();
    }
}
