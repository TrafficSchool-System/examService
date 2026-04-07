package com.example.examService.Service;

import com.example.examService.Dto.ExamResultDTO;
import com.example.examService.Dto.ExamResultSummaryDTO;
import com.example.examService.Dto.ExamSessionDTO;
import com.example.examService.Dto.ExamStatsDTO;
import com.example.examService.Dto.QuizQuestionDTO;
import com.example.examService.Entity.Answer;
import com.example.examService.Entity.ExamSession;
import com.example.examService.Entity.Result;
import com.example.examService.Exception.ExamNotFoundException;
import com.example.examService.Exception.JsonParseException;
import com.example.examService.Repository.AnswerRepository;
import com.example.examService.Repository.ExamSessionRepository;
import com.example.examService.Repository.ResultRepository;

import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.time.Duration;

@Service
public class ExamService implements ExamServiceInterface {

        private final WebClient quizWebClient;
        private final ExamSessionRepository examSessionRepository;
        private final AnswerRepository answerRepository;
        private final ResultRepository resultRepository;
        private final ObjectMapper objectMapper;

        public ExamService(WebClient quizWebClient, ExamSessionRepository examSessionRepository,
                        AnswerRepository answerRepository, ResultRepository resultRepository,
                        ObjectMapper objectMapper) {
                this.quizWebClient = quizWebClient;
                this.examSessionRepository = examSessionRepository;
                this.answerRepository = answerRepository;
                this.resultRepository = resultRepository;
                this.objectMapper = objectMapper;
        }

        // -------------------------------------------------------
        // 📌 startExam — Startar ett prov, hämta frågor skapa session
        // -------------------------------------------------------
        @Override
        public ExamSessionDTO startExam(Long userId) {
                // Avsluta gamla sessioner
                // Varje användare får bara ha ett aktiv prov
                // Gammalt pågående prov -> markeras som avslutat
                examSessionRepository.findTopByUserIdAndFinishedFalseOrderByStartsAtDesc(userId)
                                .ifPresent(oldSession -> {
                                        oldSession.setFinished(true);
                                        examSessionRepository.save(oldSession);
                                });

                // Hämta frågor från quizService med internal API key (konfigurerad i WebClient)
                ExamSessionDTO exam = quizWebClient.get()
                                .uri("/final-exam")
                                .retrieve()
                                .bodyToMono(ExamSessionDTO.class)
                                .block();

                // Skapa ny session
                ExamSession session = new ExamSession();
                session.setUserId(userId);
                session.setStartsAt(LocalDateTime.now());
                session.setExpiresAt(session.getStartsAt().plusMinutes(exam.getDurationMinutes()));
                session.setFinished(false);

                // Spara frågorna som JSON
                try {
                        session.setQuestionsJson(objectMapper.writeValueAsString(exam.getQuestions()));
                } catch (Exception e) {
                        throw new JsonParseException("Could not save questions as JSON", e);
                }

                examSessionRepository.save(session);

                exam.setStartsAt(session.getStartsAt());
                exam.setExpiresAt(session.getExpiresAt());

                return exam;
        }

        // -------------------------------------------------------
        // 📌 getExamStatus — Hämta status på ett prov
        // -------------------------------------------------------
        @Override
        public ExamSessionDTO getExamStatus(Long userId) {

                //
                ExamSession session = examSessionRepository.findTopByUserIdAndFinishedFalseOrderByStartsAtDesc(userId)
                                .orElse(null);
                if (session == null)
                        return null;

                // Läs sparade frågor från JSON
                List<QuizQuestionDTO> questions;
                try {
                        questions = objectMapper.readValue(
                                        session.getQuestionsJson(),
                                        objectMapper.getTypeFactory().constructCollectionType(
                                                        List.class,
                                                        QuizQuestionDTO.class));
                } catch (Exception e) {
                        throw new JsonParseException("Could not read questions from JSON", e);
                }

                // Skapa ExamSessionDTO med de sparade frågorna
                ExamSessionDTO exam = new ExamSessionDTO();
                exam.setQuestions(questions);
                exam.setDurationMinutes(50);
                exam.setStartsAt(session.getStartsAt());
                exam.setExpiresAt(session.getExpiresAt());

                // Hämta sparade svar för denna session
                var savedAnswers = answerRepository.findByExamSession(session).stream()
                                .collect(java.util.stream.Collectors.toMap(
                                                Answer::getQuestionId,
                                                Answer::getSelectedAnswer));
                exam.setSavedAnswers(savedAnswers);

                return exam;
        }

        // -------------------------------------------------------
        // 📌 saveAnswer — Spara svar
        // -------------------------------------------------------
        @Override
        public void saveAnswer(Long userId, Long questionId, String selectedAnswer) {

                // 1. Hämta den aktiva exam-sessionen för användaren
                ExamSession session = examSessionRepository.findTopByUserIdAndFinishedFalseOrderByStartsAtDesc(userId)
                                .orElseThrow(() -> new ExamNotFoundException(
                                                "Ingen aktiv exam-session för användaren"));

                // 2. Kolla om det redan finns ett svar för denna fråga i denna session
                Answer answer = answerRepository.findByExamSession(session).stream()
                                .filter(a -> a.getQuestionId().equals(questionId))
                                .findFirst()
                                .orElse(null);

                // Om inget svar finns, skapa ett nytt
                if (answer == null) {
                        answer = new Answer();
                        answer.setUserId(userId);
                        answer.setQuestionId(questionId);
                        answer.setExamSession(session);
                }

                // Uppdatera svaret (oavsett om det är nytt eller gammalt)
                answer.setSelectedAnswer(selectedAnswer);

                // 3. Hämta sparade frågor från sessionen för att kontrollera om svaret är rätt
                List<QuizQuestionDTO> questions;
                try {
                        questions = objectMapper.readValue(
                                        session.getQuestionsJson(),
                                        objectMapper.getTypeFactory().constructCollectionType(
                                                        List.class,
                                                        QuizQuestionDTO.class));
                } catch (Exception e) {
                        throw new JsonParseException("Could not read questions from JSON", e);
                }

                boolean isCorrect = questions.stream()
                                .filter(q -> q.getId().equals(questionId))
                                .anyMatch(q -> q.getAnswers().get(q.getCorrectAnswerIndex())
                                                .trim().equalsIgnoreCase(selectedAnswer.trim()));

                answer.setCorrect(isCorrect);

                // 4. Spara svaret i databasen (skapar nytt eller uppdaterar befintligt)
                answerRepository.save(answer);
        }

        // -------------------------------------------------------
        // 📌 finishExam — Avsluta ett prov
        // -------------------------------------------------------
        @Override
        public void finishExam(Long userId) {
                // 1. Hämta den aktiva sessionen
                ExamSession session = examSessionRepository
                                .findTopByUserIdAndFinishedFalseOrderByStartsAtDesc(userId)
                                .orElseThrow(() -> new ExamNotFoundException(
                                                "Ingen aktiv exam-session för användaren"));

                // 2. Hämta alla svar för DENNA session (inte alla användarens svar)
                var answer = answerRepository.findByExamSession(session);

                // 3. Räkna antal rätt
                int score = (int) answer.stream()
                                .filter(Answer::isCorrect)
                                .count();

                // 4. Bestämm om provet är godkänt
                int totalQuestions = answer.size();
                boolean passed = totalQuestions > 0 && ((double) score / totalQuestions) >= 0.7;

                // 5. Skapa resultat
                Result result = new Result();
                result.setScore(score);
                result.setPassed(passed);
                result.setFinishedAt(LocalDateTime.now());
                result.setExamSession(session);

                // 6. Markera session som avsluatad
                session.setFinished(true);
                examSessionRepository.save(session);

                // 7. Spara resultatet
                resultRepository.save(result);

        }

        // -------------------------------------------------------
        // 📌 getExamResult — Hämta resultat
        // -------------------------------------------------------
        @Override
        public ExamResultDTO getExamResult(Long userId) {
                // 1. Hämta den senaste avslutade sessionen
                ExamSession session = examSessionRepository
                                .findTopByUserIdAndFinishedTrueOrderByStartsAtDesc(userId)
                                .orElseThrow(() -> new ExamNotFoundException(
                                                "Ingen avslutad exam-session hittades för användaren"));

                // 2. Hämta resultat för sessionen
                Result result = resultRepository.findByExamSession(session)
                                .orElseThrow(() -> new ExamNotFoundException("Inget resultat hittades"));

                // 3. Hämta sparade frågor från sessionen
                List<QuizQuestionDTO> questions;
                try {
                        questions = objectMapper.readValue(
                                        session.getQuestionsJson(),
                                        objectMapper.getTypeFactory().constructCollectionType(
                                                        List.class,
                                                        QuizQuestionDTO.class));
                } catch (Exception e) {
                        throw new JsonParseException("Could not read questions from JSON", e);
                }

                // 4. Hämta användarens svar
                var answers = answerRepository.findByExamSession(session);
                var userAnswersMap = answers.stream()
                                .collect(java.util.stream.Collectors.toMap(
                                                Answer::getQuestionId,
                                                Answer::getSelectedAnswer));

                // 5. Räkna ut hur lång tid provet tog (i minuter)
                Duration duration = Duration.between(session.getStartsAt(), result.getFinishedAt());
                int timeTaken = (int) duration.toMinutes();

                // 6. Skapa och returnera ResultDTO
                return new ExamResultDTO(
                                result.getScore(),
                                result.isPassed(),
                                questions,
                                userAnswersMap,
                                timeTaken);
        }

        // -------------------------------------------------------
        // 📌 getAllExamResults — Hämta alla provresultat för en användare
        // -------------------------------------------------------
        @Override
        public List<ExamResultSummaryDTO> getAllExamResults(Long userId) {
                // 1. Hämta alla avslutade sessioner för användaren
                List<ExamSession> sessions = examSessionRepository
                                .findAllByUserIdAndFinishedTrueOrderByStartsAtDesc(userId);

                // 2. Konvertera varje session till ExamResultSummaryDTO
                return sessions.stream()
                                .map(session -> {
                                        // Hämta resultat för denna session
                                        Result result = resultRepository.findByExamSession(session)
                                                        .orElse(null);

                                        if (result == null) {
                                                return null; // Hoppa över sessioner utan resultat
                                        }

                                        // Hämta antal frågor från sparade svar
                                        int totalQuestions = answerRepository.findByExamSession(session).size();

                                        // Räkna ut hur lång tid provet tog (i minuter)
                                        Duration duration = Duration.between(session.getStartsAt(),
                                                        result.getFinishedAt());
                                        int timeTaken = (int) duration.toMinutes();

                                        // Beräkna procent
                                        int percentage = totalQuestions > 0
                                                        ? (int) Math.round(((double) result.getScore() / totalQuestions)
                                                                        * 100)
                                                        : 0;

                                        // Skapa DTO och sätt percentage
                                        ExamResultSummaryDTO dto = new ExamResultSummaryDTO(
                                                        result.getId(),
                                                        result.getScore(),
                                                        totalQuestions,
                                                        result.isPassed(),
                                                        timeTaken,
                                                        result.getFinishedAt());
                                        dto.setPercentage(percentage);

                                        return dto;
                                })
                                .filter(dto -> dto != null) // Ta bort null-värden
                                .collect(Collectors.toList());
        }

        @Override
        public ExamStatsDTO getExamStats(Long userId) {

                // Hämta alla resultat
                List<ExamResultSummaryDTO> results = getAllExamResults(userId);

                // Räkna statestik
                int total = results.size();
                int passed = (int) results.stream().filter(r -> r.isPassed()).count();
                int failed = total - passed;

                // Beräkna genomsnitt
                int averagePercentage = results.isEmpty() ? 0
                                : (int) results.stream()
                                                .mapToInt(ExamResultSummaryDTO::getPercentage)
                                                .average()
                                                .orElse(0);

                // Beräkna streak (från senaste och bakåt)
                int currentStreak = 0;
                int bestStreak = 0;
                int tempStreak = 0;

                for (ExamResultSummaryDTO result : results) {
                        if (result.isPassed()) {
                                tempStreak++;
                                if (tempStreak > bestStreak) {
                                        bestStreak = tempStreak;
                                }

                        } else {
                                tempStreak = 0;
                        }
                }

                // Current streak är från senaste provet
                for (ExamResultSummaryDTO result : results) {
                        if (result.isPassed()) {
                                currentStreak++;
                        } else {
                                break; // Stoppa vid första underkänt
                        }
                }

                // Kan användaren göra riktig prov?
                boolean readyForRealExam = false;
                if (currentStreak >= 5) {
                        readyForRealExam = results.stream()
                                        .limit(5)
                                        .allMatch(r -> r.isPassed() && r.getPercentage() >= 80);

                }
                // Retunera DTO
                ExamStatsDTO stats = new ExamStatsDTO();
                stats.setTotalExams(total);
                stats.setPassedExams(passed);
                stats.setFailedExams(failed);
                stats.setAveragePercentage(averagePercentage);
                stats.setCurrentStreak(currentStreak);
                stats.setBestStreak(bestStreak);
                stats.setReadyForRealExam(readyForRealExam);

                return stats;
        }

        @Override
        public Map<String, Integer> getExamCounts() {
                Map<String, Integer> counts = new HashMap<>();
                counts.put("activeExams", (int) examSessionRepository.countByFinishedFalse());
                counts.put("completedExams", (int) examSessionRepository.countByFinishedTrue());
                return counts;
        }

}
