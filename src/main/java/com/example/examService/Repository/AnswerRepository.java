package com.example.examService.Repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.examService.Entity.Answer;
import com.example.examService.Entity.ExamSession;

public interface AnswerRepository extends JpaRepository<Answer, Long> {
    List<Answer> findByUserId(Long userId);

    List<Answer> findByExamSession(ExamSession examSession);
}
