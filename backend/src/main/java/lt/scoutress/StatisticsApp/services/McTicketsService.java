package lt.scoutress.StatisticsApp.services;

import java.util.List;

import lt.scoutress.StatisticsApp.entity.McTickets.McTicketsAnswered;
import lt.scoutress.StatisticsApp.entity.McTickets.McTicketsCalculations;

public interface McTicketsService {

    List<McTicketsAnswered> findAll();

    List<McTicketsCalculations> findAllCalc();

    void save(McTicketsAnswered mcTickets);
    
}
