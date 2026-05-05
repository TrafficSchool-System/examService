package com.example.examService.features.exam.dto;

/**
 * ExamStatsDTO - User's exam statistics and performance metrics
 * 
 * Used when: Displaying user's exam performance dashboard
 * Endpoint: GET /api/exams/statistics
 * 
 * Provides insights into:
 * - Total exam attempts
 * - Pass/fail counts
 * - Average performance
 * - Streak tracking
 * - Readiness assessment
 */
public class ExamStatsDTO {

    /**
     * Total number of exams taken by user
     */
    private int totalExams;

    /**
     * Number of passed exams (>= 70%)
     */
    private int passedExams;

    /**
     * Number of failed exams (< 70%)
     */
    private int failedExams;

    /**
     * Average score percentage across all exams
     */
    private int averagePercentage;

    /**
     * Current streak of consecutive passed exams
     * Reset to 0 on first fail
     */
    private int currentStreak;

    /**
     * Best streak ever achieved
     */
    private int bestStreak;

    /**
     * Whether user is ready for real exam
     * Criteria: averagePercentage >= 80 AND passedExams >= 3
     */
    private boolean readyForRealExam;

    // Constructors

    public ExamStatsDTO() {
    }

    public ExamStatsDTO(int totalExams, int passedExams, int failedExams, int averagePercentage,
            int currentStreak, int bestStreak, boolean readyForRealExam) {
        this.totalExams = totalExams;
        this.passedExams = passedExams;
        this.failedExams = failedExams;
        this.averagePercentage = averagePercentage;
        this.currentStreak = currentStreak;
        this.bestStreak = bestStreak;
        this.readyForRealExam = readyForRealExam;
    }

    // Getters and Setters

    public int getTotalExams() {
        return totalExams;
    }

    public void setTotalExams(int totalExams) {
        this.totalExams = totalExams;
    }

    public int getPassedExams() {
        return passedExams;
    }

    public void setPassedExams(int passedExams) {
        this.passedExams = passedExams;
    }

    public int getFailedExams() {
        return failedExams;
    }

    public void setFailedExams(int failedExams) {
        this.failedExams = failedExams;
    }

    public int getAveragePercentage() {
        return averagePercentage;
    }

    public void setAveragePercentage(int averagePercentage) {
        this.averagePercentage = averagePercentage;
    }

    public int getCurrentStreak() {
        return currentStreak;
    }

    public void setCurrentStreak(int currentStreak) {
        this.currentStreak = currentStreak;
    }

    public int getBestStreak() {
        return bestStreak;
    }

    public void setBestStreak(int bestStreak) {
        this.bestStreak = bestStreak;
    }

    public boolean isReadyForRealExam() {
        return readyForRealExam;
    }

    public void setReadyForRealExam(boolean readyForRealExam) {
        this.readyForRealExam = readyForRealExam;
    }
}
