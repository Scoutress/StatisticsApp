package com.scoutress.KaimuxAdminStats.Services;

import java.util.List;

import com.scoutress.KaimuxAdminStats.Entity.Employees.Employee;
import com.scoutress.KaimuxAdminStats.Entity.McTickets.McTickets;
import com.scoutress.KaimuxAdminStats.Entity.McTickets.McTicketsAvgDaily;
import com.scoutress.KaimuxAdminStats.Entity.McTickets.McTicketsAvgDailyRatio;

public interface McTicketsService {

    void calculateMcTicketsAvgDaily(Employee employee);

    List<McTicketsAvgDaily> findAllAvgDaily();

    List<McTicketsAvgDailyRatio> calculateMcTicketsAvgDailyRatio(List<McTickets> mcTicketsList);

    List<McTicketsAvgDailyRatio> findAllAvgDailyRatio();

}
