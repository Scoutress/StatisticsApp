package lt.scoutress.StatisticsApp.services;

import java.util.List;

import lt.scoutress.StatisticsApp.entity.Calculations;
import lt.scoutress.StatisticsApp.entity.McTickets.McTicketsAnswered;

public interface StatisticsService {
    
    public String showForm();

    public List<McTicketsAnswered> findAllMcTickets();

    public void saveMcTickets(McTicketsAnswered mcTickets);

    public List<Calculations> findCalculations();

    public void calculateDaysSinceJoinAndSave();

    public void calculateTotalDailyMcTickets();

    public void calculateDailyTicketDifference();

    public void calculateDailyTicketRatio();

    public void calculateTotalDailyDcMessages();

    public void calculateDailyDcMessagesRatio();

    public void calculateAvgDailyDcMessages();
}
