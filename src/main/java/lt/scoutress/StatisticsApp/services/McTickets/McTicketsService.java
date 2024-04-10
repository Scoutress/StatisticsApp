package lt.scoutress.StatisticsApp.services.McTickets;

import java.util.List;

import lt.scoutress.StatisticsApp.entity.Employee;
import lt.scoutress.StatisticsApp.entity.McTickets.McTickets;
import lt.scoutress.StatisticsApp.entity.McTickets.McTicketsAvgDaily;
import lt.scoutress.StatisticsApp.entity.McTickets.McTicketsAvgDailyRatio;

public interface McTicketsService {

    void calculateMcTicketsAvgDaily(Employee employee);

    List<McTicketsAvgDaily> findAll();

    List<McTicketsAvgDailyRatio> calculateMcTicketsAvgDailyRatio(List<McTickets> mcTicketsList);

}
