package lt.scoutress.StatisticsApp.services;

import java.util.List;

import lt.scoutress.StatisticsApp.entity.McTickets.McTicketsAnswered;

public interface McTicketsService {

    List<McTicketsAnswered> findAll();

    void save(McTicketsAnswered mcTickets);
    
}
