package lt.scoutress.StatisticsApp.Services;

import java.util.List;

import lt.scoutress.StatisticsApp.entity.Employees.Employee;
import lt.scoutress.StatisticsApp.entity.McTickets.McTickets;
import lt.scoutress.StatisticsApp.entity.McTickets.McTicketsAvgDaily;
import lt.scoutress.StatisticsApp.entity.McTickets.McTicketsAvgDailyRatio;

public interface McTicketsService {

    void calculateMcTicketsAvgDaily(Employee employee);

    List<McTicketsAvgDaily> findAllAvgDaily();

    List<McTicketsAvgDailyRatio> calculateMcTicketsAvgDailyRatio(List<McTickets> mcTicketsList);

    List<McTicketsAvgDailyRatio> findAllAvgDailyRatio();

}
