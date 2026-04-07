package com.example.examService.Config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

/**
 * ==========================================
 * WEBCLIENT CONFIGURATION
 * ==========================================
 * Konfigurerar WebClient för service-to-service kommunikation.
 * 
 * ARKITEKTUR:
 * - ExamService använder QuizService för att hämta quiz-frågor för prov
 * - Använder X-Internal-API-Key för service-to-service authentication
 * - Railway DNS support via miljövariabel
 * 
 * ANVÄNDNING:
 * - ExamService -> QuizService för att hämta frågor till prov
 * - Säker service-to-service kommunikation med API key
 */
@Configuration
public class WebClientConfig {

    @Value("${service.api.key}")
    private String serviceApiKey;

    @Value("${QUIZ_SERVICE_URL:http://quiz-service}")
    private String quizServiceUrl;

    /**
     * WebClient för QuizService
     * 
     * Railway: QUIZ_SERVICE_URL=http://quizservice:8085
     * Lokal: QUIZ_SERVICE_URL=http://localhost:8085 (eller låt Eureka hantera)
     * 
     * Endpoints som anropas:
     * - GET /subjects?subjects={ids}&limit={n} → Hämta frågor för prov
     * - GET /final-exam → Hämta final exam frågor
     */
    @Bean
    public WebClient quizWebClient() {
        return WebClient.builder()
                .baseUrl(quizServiceUrl + "/api/quizzes")
                .defaultHeader("Content-Type", "application/json")
                .defaultHeader("X-Internal-API-Key", serviceApiKey)
                .defaultHeader("X-Internal-Source", "exam-service")
                .build();
    }
}