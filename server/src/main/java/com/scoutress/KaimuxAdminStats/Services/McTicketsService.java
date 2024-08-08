package com.scoutress.KaimuxAdminStats.Services;

import java.util.List;

import com.scoutress.KaimuxAdminStats.entity.Employees.Employee;
import com.scoutress.KaimuxAdminStats.entity.McTickets.McTickets;
import com.scoutress.KaimuxAdminStats.entity.McTickets.McTicketsAvgDaily;
import com.scoutress.KaimuxAdminStats.entity.McTickets.McTicketsAvgDailyRatio;

public interface McTicketsService {

    void calculateMcTicketsAvgDaily(Employee employee);

    List<McTicketsAvgDaily> findAllAvgDaily();

    List<McTicketsAvgDailyRatio> calculateMcTicketsAvgDailyRatio(List<McTickets> mcTicketsList);

    List<McTicketsAvgDailyRatio> findAllAvgDailyRatio();

}
