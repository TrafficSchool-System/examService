package com.example.examService.features.exam.dto;

import java.util.List;

/**
 * QuizQuestionDTO - Data Transfer Object for quiz questions
 * 
 * Source: QuizService
 * Used to: Store exam questions in ExamSession.questionsJson
 * 
 * Contains all question data needed for exam display and validation
 */
public class QuizQuestionDTO {

    /**
     * Question ID from QuizService
     */
    private Long id;

    /**
     * The question text
     */
    private String question;

    /**
     * SFI (Swedish for Immigrants) translated question text
     * Optional, for users who need simplified Swedish
     */
    private String sfi;

    /**
     * List of possible answers
     * Array index corresponds to correctAnswerIndex
     */
    private List<String> answers;

    /**
     * Index of the correct answer in the answers list
     * Used for validation when user submits answer
     */
    private int correctAnswerIndex;

    /**
     * Optional image URL for the question
     */
    private String image;

    /**
     * Explanation shown to student after exam completion
     * Helps learning from mistakes
     */
    private String explinationForStudent;

    // Constructors

    public QuizQuestionDTO() {
    }

    public QuizQuestionDTO(Long id, String question, String sfi, List<String> answers,
            int correctAnswerIndex, String image, String explinationForStudent) {
        this.id = id;
        this.question = question;
        this.sfi = sfi;
        this.answers = answers;
        this.correctAnswerIndex = correctAnswerIndex;
        this.image = image;
        this.explinationForStudent = explinationForStudent;
    }

    // Getters and Setters

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public String getSfi() {
        return sfi;
    }

    public void setSfi(String sfi) {
        this.sfi = sfi;
    }

    public List<String> getAnswers() {
        return answers;
    }

    public void setAnswers(List<String> answers) {
        this.answers = answers;
    }

    public int getCorrectAnswerIndex() {
        return correctAnswerIndex;
    }

    public void setCorrectAnswerIndex(int correctAnswerIndex) {
        this.correctAnswerIndex = correctAnswerIndex;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getExplinationForStudent() {
        return explinationForStudent;
    }

    public void setExplinationForStudent(String explinationForStudent) {
        this.explinationForStudent = explinationForStudent;
    }
}
