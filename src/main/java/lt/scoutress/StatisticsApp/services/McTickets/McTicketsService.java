package lt.scoutress.StatisticsApp.services.McTickets;

import java.util.List;

import lt.scoutress.StatisticsApp.entity.Employee;
import lt.scoutress.StatisticsApp.entity.McTickets.McTicketsAvgDaily;

public interface McTicketsService {

    void calculateMcTicketsAvgDaily(Employee employee);

    List<McTicketsAvgDaily> findAll();



    // Old methods
    // List<McTicketsCounting> findAll();
    // List<McTicketsCalculations> findAllCalc();
    // Optional<McTicketsCounting> findById(int id);
    // LocalDate getOldestDate();
    // LocalDate getNewestDate();
    // boolean columnExists(String lowercaseUsername);
    // Double getTicketsCountByUsernameAndDate(String lowercaseUsername, LocalDate currentDate);
    // void saveMcTicketsCalculations(McTicketsCalculations mcTicketsCalculations);
}
