package com.example.examService.Repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.examService.Entity.Result;

public interface ResultRepository extends JpaRepository<Result, Long> {

}
