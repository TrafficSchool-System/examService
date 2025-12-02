package com.example.examService.Service;

import com.example.examService.Dto.ExamSessionDTO;
import com.example.examService.Entity.Answer;
import com.example.examService.Entity.ExamSession;
import com.example.examService.Entity.Result;
import com.example.examService.Exception.ExamNotFoundException;
import com.example.examService.Repository.AnswerRepository;
import com.example.examService.Repository.ExamSessionRepository;
import com.example.examService.Repository.ResultRepository;

import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.time.LocalDateTime;

@Service
public class ExamService implements ExamServiceInterface {

        private final WebClient quizWebClient;
        private final ExamSessionRepository examSessionRepository;
        private final AnswerRepository answerRepository;
        private final ResultRepository resultRepository;

        public ExamService(WebClient quizWebClient, ExamSessionRepository examSessionRepository,
                        AnswerRepository answerRepository, ResultRepository resultRepository) {
                this.quizWebClient = quizWebClient;
                this.examSessionRepository = examSessionRepository;
                this.answerRepository = answerRepository;
                this.resultRepository = resultRepository;
        }

        @Override
        public ExamSessionDTO startExam(Long userId) {
                // Avsluta gamla sessioner
                examSessionRepository.findTopByUserIdAndFinishedFalseOrderByStartsAtDesc(userId)
                                .ifPresent(oldSession -> {
                                        oldSession.setFinished(true);
                                        examSessionRepository.save(oldSession);
                                });

                // Hämta frågor från quizService
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

                examSessionRepository.save(session);

                exam.setStartsAt(session.getStartsAt());
                exam.setExpiresAt(session.getExpiresAt());

                return exam;
        }

        @Override
        public ExamSessionDTO getExamStatus(Long userId) {
                ExamSession session = examSessionRepository.findTopByUserIdAndFinishedFalseOrderByStartsAtDesc(userId)
                                .orElse(null);
                if (session == null)
                        return null;

                ExamSessionDTO exam = quizWebClient.get()
                                .uri("/final-exam")
                                .retrieve()
                                .bodyToMono(ExamSessionDTO.class)
                                .block();

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

                // 3. Hämta frågorna från quizService för att kontrollera om svaret är rätt
                ExamSessionDTO exam = quizWebClient.get()
                                .uri("/final-exam")
                                .retrieve()
                                .bodyToMono(ExamSessionDTO.class)
                                .block();

                boolean isCorrect = exam.getQuestions().stream()
                                .filter(q -> q.getId().equals(questionId))
                                .anyMatch(q -> q.getAnswers().get(q.getCorrectAnswerIndex())
                                                .trim().equalsIgnoreCase(selectedAnswer.trim()));

                answer.setCorrect(isCorrect);

                // 4. Spara svaret i databasen (skapar nytt eller uppdaterar befintligt)
                answerRepository.save(answer);
        }

        @Override
        public void finishExam(Long userId) {
                // 1. Hämta den aktiva sessionen
                ExamSession session = examSessionRepository
                                .findTopByUserIdAndFinishedFalseOrderByStartsAtDesc(userId)
                                .orElseThrow(() -> new ExamNotFoundException(
                                                "Ingen aktiv exam-session för användaren"));

                // 2. Hämta alla svar för användaren
                var answer = answerRepository.findByUserId(userId);

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
}