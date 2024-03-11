package lt.scoutress.StatisticsApp.services;

import java.util.List;

import lt.scoutress.StatisticsApp.entity.McTickets;

public interface StatisticsService {
    
    public String showForm();

    public List<McTickets> findAllMcTickets();

}
