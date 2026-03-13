package com.example.examService.Config;

import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

/**
 * ==========================================
 * WEBCLIENT CONFIGURATION
 * ==========================================
 * Konfigurerar load-balanced WebClient för service-to-service kommunikation.
 * 
 * ARKITEKTUR:
 * - ExamService använder QuizService för att hämta quiz-frågor för prov
 * - @LoadBalanced aktiverar Eureka service discovery
 * - Service namn (http://quiz-service) översätts automatiskt till IP:PORT
 * 
 * ANVÄNDNING:
 * - ExamService -> QuizService för att hämta frågor till prov
 * - Inga direkta HTTP-anrop till localhost:PORT
 * - Eureka hanterar automatisk service discovery och load balancing
 */
@Configuration
public class WebClientConfig {

    /**
     * WebClient.Builder med load balancing
     * 
     * @LoadBalanced aktiverar Eureka service discovery
     */
    @Bean
    @LoadBalanced
    public WebClient.Builder loadBalancedWebClientBuilder() {
        return WebClient.builder();
    }

    /**
     * WebClient för QuizService
     * Base URL: http://quiz-service/api/quizzes
     * 
     * Endpoints som anropas:
     * - GET /subjects?subjects={ids}&limit={n} → Hämta frågor för prov
     * - GET /final-exam → Hämta final exam frågor
     */
    @Bean
    public WebClient quizWebClient(WebClient.Builder builder) {
        return builder
                .baseUrl("http://quiz-service/api/quizzes")
                .defaultHeader("X-Internal-Source", "exam-service")
                .build();
    }
}