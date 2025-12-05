package com.example.examService.Dto;

public class ExamStatsDTO {

    private int totalExams; 
    private int passedExams; 
    private int failedExams; 
    private int averagePercentage; // Genomsnittlig procent
    private int currentStreak; 
    private int bestStreak; 
    private boolean readyForRealExam; 

    public ExamStatsDTO () {}

    

    public ExamStatsDTO(int totalExams, int passedExams, int failedExams, int averagePercentage, int currentStreak,
            int bestStreak, boolean readyForRealExam) {
        this.totalExams = totalExams;
        this.passedExams = passedExams;
        this.failedExams = failedExams;
        this.averagePercentage = averagePercentage;
        this.currentStreak = currentStreak;
        this.bestStreak = bestStreak;
        this.readyForRealExam = readyForRealExam;
    }


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
