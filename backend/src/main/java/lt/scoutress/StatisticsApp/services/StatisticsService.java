package lt.scoutress.StatisticsApp.services;

import java.util.List;

import lt.scoutress.StatisticsApp.entity.Calculations;
import lt.scoutress.StatisticsApp.entity.McTickets.McTicketsCounting;

public interface StatisticsService {
    
    public List<McTicketsCounting> findAllMcTickets();

    public void saveMcTickets(McTicketsCounting mcTickets);

    public void calculateDaysSinceJoinAndSave();

    public void calculateTotalDailyMcTickets();

    public void calculateDailyTicketRatio();

    public void calculateTotalDailyDcMessages();

    public void calculateDailyDcMessagesRatio();

    public void calculateAvgDailyDcMessages();

    public void calculateAvgDailyDcMessagesRatio();

    public void calculateAvgDailyMcTickets();

    public void migrateSurvivalPlaytimeData();

    //public void calculateAvgDailyPlaytime();

    //public void calculateAvgDailyMcTicketsRatio();

    //public void calculatePlaytimeInLastHalfYear();

    
    public List<Calculations> findCalculations();

    
}
