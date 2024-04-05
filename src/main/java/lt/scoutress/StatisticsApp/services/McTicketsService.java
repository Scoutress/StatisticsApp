package lt.scoutress.StatisticsApp.services;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import lt.scoutress.StatisticsApp.entity.McTickets.McTicketsCalculations;
import lt.scoutress.StatisticsApp.entity.McTickets.McTicketsCounting;

public interface McTicketsService {

    List<McTicketsCounting> findAll();

    List<McTicketsCalculations> findAllCalc();

    Optional<McTicketsCounting> findById(int id);

    LocalDate getOldestDate();

    LocalDate getNewestDate();

    boolean columnExists(String lowercaseUsername);

    Double getTicketsCountByUsernameAndDate(String lowercaseUsername, LocalDate currentDate);

    void saveMcTicketsCalculations(McTicketsCalculations mcTicketsCalculations);
}
