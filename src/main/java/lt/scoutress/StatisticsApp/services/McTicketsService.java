package lt.scoutress.StatisticsApp.services;

import lt.scoutress.StatisticsApp.entity.Employee;

public interface McTicketsService {

    void calculateMcTicketsAvgDaily(Employee employee);




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
