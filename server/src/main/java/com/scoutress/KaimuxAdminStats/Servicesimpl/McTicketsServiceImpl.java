package com.scoutress.KaimuxAdminStats.Servicesimpl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.scoutress.KaimuxAdminStats.Entity.Employees.Employee;
import com.scoutress.KaimuxAdminStats.Entity.McTickets.McTickets;
import com.scoutress.KaimuxAdminStats.Entity.McTickets.McTicketsAvgDaily;
import com.scoutress.KaimuxAdminStats.Entity.McTickets.McTicketsAvgDailyRatio;
import com.scoutress.KaimuxAdminStats.Repositories.McTickets.McTicketsAvgDailyRatioRepository;
import com.scoutress.KaimuxAdminStats.Repositories.McTickets.McTicketsAvgValuesRepository;
import com.scoutress.KaimuxAdminStats.Repositories.McTickets.McTicketsRepository;
import com.scoutress.KaimuxAdminStats.Services.McTicketsService;

@Service
public class McTicketsServiceImpl implements McTicketsService {

    private final McTicketsAvgValuesRepository mcTicketsAvgValuesRepository;
    private final McTicketsAvgDailyRatioRepository mcTicketsAvgDailyRatioRepository;
    private final McTicketsRepository mcTicketsRepository;

    public McTicketsServiceImpl(McTicketsAvgValuesRepository mcTicketsAvgValuesRepository,
            McTicketsRepository mcTicketsRepository,
            McTicketsAvgDailyRatioRepository mcTicketsAvgDailyRatioRepository) {
        this.mcTicketsAvgValuesRepository = mcTicketsAvgValuesRepository;
        this.mcTicketsAvgDailyRatioRepository = mcTicketsAvgDailyRatioRepository;
        this.mcTicketsRepository = mcTicketsRepository;
    }

    @Override
    public void calculateMcTicketsAvgDaily(Employee employee) {
        List<McTickets> mcTicketsList = mcTicketsRepository.findByEmployee(employee);
        if (!mcTicketsList.isEmpty()) {
            int totalMcTicketsCount = mcTicketsList.stream().mapToInt(McTickets::getMcTicketsCount).sum();
            McTicketsAvgDaily mcTicketsAvgDaily = mcTicketsAvgValuesRepository.findByEmployeeId(employee.getId());
            if (mcTicketsAvgDaily != null) {
                mcTicketsAvgDaily.setAverageValues(totalMcTicketsCount / (double) mcTicketsList.size());
                mcTicketsAvgValuesRepository.save(mcTicketsAvgDaily);
            } else {
                mcTicketsAvgDaily = new McTicketsAvgDaily();
                mcTicketsAvgDaily.setEmployee(employee);
                mcTicketsAvgDaily.setAverageValues(totalMcTicketsCount / (double) mcTicketsList.size());
                mcTicketsAvgValuesRepository.save(mcTicketsAvgDaily);
            }
        }
    }

    @Override
    public List<McTicketsAvgDailyRatio> calculateMcTicketsAvgDailyRatio(List<McTickets> mcTicketsList) {
        Map<Employee, Integer> employeeMcTicketsCountMap = new HashMap<>();

        for (McTickets mcTickets : mcTicketsList) {
            Employee employee = mcTickets.getEmployee();

            int mcTicketsCount = mcTickets.getMcTicketsCount();

            employeeMcTicketsCountMap.put(employee,
                    employeeMcTicketsCountMap.getOrDefault(employee, 0) + mcTicketsCount);
        }

        int totalMcTicketsCount = mcTicketsList.stream().mapToInt(McTickets::getMcTicketsCount).sum();

        List<McTicketsAvgDailyRatio> existingMcTicketsAvgDailyRatioList = mcTicketsAvgDailyRatioRepository.findAll();
        List<McTicketsAvgDailyRatio> updatedMcTicketsAvgDailyRatioList = new ArrayList<>();

        for (Map.Entry<Employee, Integer> entry : employeeMcTicketsCountMap.entrySet()) {
            Employee employee = entry.getKey();
            int employeeMcTicketsCount = entry.getValue();

            double ratio = (double) employeeMcTicketsCount / totalMcTicketsCount;

            Optional<McTicketsAvgDailyRatio> existingMcTicketsAvgDailyRatio = existingMcTicketsAvgDailyRatioList
                    .stream()
                    .filter(m -> m.getEmployee().equals(employee))
                    .findFirst();

            if (existingMcTicketsAvgDailyRatio.isPresent()) {
                McTicketsAvgDailyRatio updatedMcTicketsAvgDailyRatio = existingMcTicketsAvgDailyRatio.get();
                updatedMcTicketsAvgDailyRatio.setAverageDailyRatio(ratio);
                mcTicketsAvgDailyRatioRepository.save(updatedMcTicketsAvgDailyRatio);
                updatedMcTicketsAvgDailyRatioList.add(updatedMcTicketsAvgDailyRatio);
            } else {
                McTicketsAvgDailyRatio newMcTicketsAvgDailyRatio = new McTicketsAvgDailyRatio();
                newMcTicketsAvgDailyRatio.setEmployee(employee);
                newMcTicketsAvgDailyRatio.setAverageDailyRatio(ratio);
                mcTicketsAvgDailyRatioRepository.save(newMcTicketsAvgDailyRatio);
                updatedMcTicketsAvgDailyRatioList.add(newMcTicketsAvgDailyRatio);
            }
        }
        return updatedMcTicketsAvgDailyRatioList;
    }

    @Override
    public List<McTicketsAvgDaily> findAllAvgDaily() {
        return mcTicketsAvgValuesRepository.findAll();
    }

    @Override
    public List<McTicketsAvgDailyRatio> findAllAvgDailyRatio() {
        return mcTicketsAvgDailyRatioRepository.findAll();
    }
}
