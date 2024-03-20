package lt.scoutress.StatisticsApp.services;

import java.util.List;
import java.util.Optional;

import lt.scoutress.StatisticsApp.entity.McTickets.McTicketsCalculations;
import lt.scoutress.StatisticsApp.entity.McTickets.McTicketsCounting;

public interface McTicketsService {

    List<McTicketsCounting> findAll();

    List<McTicketsCalculations> findAllCalc();

    Optional<McTicketsCounting> findById(int id);

    // void save(McTicketsAnswered mcTickets);
    
}
