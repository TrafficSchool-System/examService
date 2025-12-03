package com.example.examService.Repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.examService.Entity.ExamSession;
import com.example.examService.Entity.Result;

public interface ResultRepository extends JpaRepository<Result, Long> {
    Optional<Result> findByExamSession(ExamSession examSession);
}
