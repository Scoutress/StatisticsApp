package com.scoutress.KaimuxAdminStats.Servicesimpl;


import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.scoutress.KaimuxAdminStats.Entity.Employee;
import com.scoutress.KaimuxAdminStats.Entity.McTickets.McTicket;
import com.scoutress.KaimuxAdminStats.Entity.McTickets.McTicketPercentage;
import com.scoutress.KaimuxAdminStats.Entity.Productivity;
import com.scoutress.KaimuxAdminStats.Repositories.EmployeeRepository;
import com.scoutress.KaimuxAdminStats.Repositories.McTickets.McTicketPercentageRepository;
import com.scoutress.KaimuxAdminStats.Repositories.McTickets.McTicketRepository;
import com.scoutress.KaimuxAdminStats.Repositories.ProductivityRepository;
import com.scoutress.KaimuxAdminStats.Services.McTicketService;

@Service
public class McTicketServiceImpl implements McTicketService {

    private final McTicketRepository mcTicketsRepository;
    private final ProductivityRepository productivityRepository;
    private final EmployeeRepository employeeRepository;
    private final McTicketPercentageRepository mcTicketPercentageRepository;

    public McTicketServiceImpl(McTicketRepository mcTicketsRepository, EmployeeRepository employeeRepository, ProductivityRepository productivityRepository, McTicketPercentageRepository mcTicketPercentageRepository) {
        this.mcTicketsRepository = mcTicketsRepository;
        this.productivityRepository = productivityRepository;
        this.employeeRepository = employeeRepository;
        this.mcTicketPercentageRepository = mcTicketPercentageRepository;
    }

    @Override
    public void saveAll(List<McTicket> mcTickets) {
        mcTicketsRepository.saveAll(mcTickets);
    }

    @Override
    public void updateMinecraftTicketsAverage() {
        List<McTicket> mcTickets = mcTicketsRepository.findAll();

        Map<Integer, List<McTicket>> ticketsByEmployee = mcTickets.stream()
            .collect(Collectors.groupingBy(McTicket::getEmployeeId));

        for (Map.Entry<Integer, List<McTicket>> entry : ticketsByEmployee.entrySet()){
            Integer employeeId = entry.getKey();
            List<McTicket> employeeTickets = entry.getValue();

            LocalDate earliestDate = employeeTickets.stream()
                .map(McTicket::getDate)
                .min(LocalDate::compareTo)
                .orElse(null);

            LocalDate latestDate = employeeTickets.stream()
                .map(McTicket::getDate)
                .max(LocalDate::compareTo)
                .orElse(null);

            if(earliestDate != null && latestDate != null){
                long daysBetween = ChronoUnit.DAYS.between(earliestDate, latestDate) + 1;

                int totalTickets = employeeTickets.stream()
                    .mapToInt(McTicket::getTicketCount)
                    .sum();

                double averageTicketsPerDay = (double) totalTickets / daysBetween;

                Productivity productivity = productivityRepository.findByEmployeeId(employeeId);
                if(productivity == null){
                    Employee employee = employeeRepository.findById(employeeId).orElse(null);
                    if(employee == null){
                        System.out.println("Employee with ID " + employeeId + " not found. Skipping.");
                        continue;
                    }
                    productivity = new Productivity();
                    productivity.setEmployee(employee);
                }
                productivity.setServerTickets(averageTicketsPerDay);
                productivityRepository.save(productivity);
            }
        }
    }

    @Override
    public void calculateMcTicketsPercentage() {
        List<McTicket> mcTickets = mcTicketsRepository.findAll();

        Map<LocalDate, List<McTicket>> ticketsByDate = mcTickets.stream()
            .collect(Collectors.groupingBy(McTicket::getDate));

        for(Map.Entry<LocalDate, List<McTicket>> entry : ticketsByDate.entrySet()){
            LocalDate date = entry.getKey();
            List<McTicket> dailyTickets = entry.getValue();

            int totalTicketsPerDay = dailyTickets.stream()
                .mapToInt(McTicket::getTicketCount)
                .sum();

            for(McTicket ticket : dailyTickets){
                double percentage = totalTicketsPerDay > 0
                ? (double) ticket.getTicketCount() / totalTicketsPerDay * 100
                : 0.0;

                McTicketPercentage mcTicketPercentage = new McTicketPercentage(
                    ticket.getEmployeeId(), date, percentage
                );
                mcTicketPercentageRepository.save(mcTicketPercentage);
            }
        }
    }

    @Override
    public void updateAverageMcTicketsPercentages() {
        List<McTicketPercentage> allPercentages = mcTicketPercentageRepository.findAll();

        Map<Integer, List<McTicketPercentage>> percentagesPerEmployee = allPercentages.stream()
            .collect(Collectors.groupingBy(McTicketPercentage::getEmployeeId));

        for(Map.Entry<Integer, List<McTicketPercentage>> entry : percentagesPerEmployee.entrySet()){
            Integer employeeId = entry.getKey();
            List<McTicketPercentage> employeePercentages = entry.getValue();

            double averagePercentage = employeePercentages.stream()
                .mapToDouble(McTicketPercentage::getPercentage)
                .average()
                .orElse(0.0);

            Productivity productivity = productivityRepository.findByEmployeeId(employeeId);
            if(productivity != null){
                productivity.setServerTicketsTaking(averagePercentage);
                productivityRepository.save(productivity);
            }
        }
    }    
}
