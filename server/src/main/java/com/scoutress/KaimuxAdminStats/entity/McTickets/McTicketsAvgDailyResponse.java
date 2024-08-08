package com.scoutress.KaimuxAdminStats.entity.McTickets;

public class McTicketsAvgDailyResponse {
    private String username;
    private double averageValues;

    public McTicketsAvgDailyResponse(String username, double averageValues) {
        this.username = username;
        this.averageValues = averageValues;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public double getAverageValues() {
        return averageValues;
    }

    public void setAverageValues(double averageValues) {
        this.averageValues = averageValues;
    }
}
