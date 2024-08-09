package com.scoutress.KaimuxAdminStats.Servicesimpl;


import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.scoutress.KaimuxAdminStats.Entity.Employee;
import com.scoutress.KaimuxAdminStats.Entity.McTicket;
import com.scoutress.KaimuxAdminStats.Entity.Productivity;
import com.scoutress.KaimuxAdminStats.Repositories.EmployeeRepository;
import com.scoutress.KaimuxAdminStats.Repositories.McTicketRepository;
import com.scoutress.KaimuxAdminStats.Repositories.ProductivityRepository;
import com.scoutress.KaimuxAdminStats.Services.McTicketService;

@Service
public class McTicketServiceImpl implements McTicketService {

    private final McTicketRepository mcTicketsRepository;
    private final ProductivityRepository productivityRepository;
    private final EmployeeRepository employeeRepository;

    public McTicketServiceImpl(McTicketRepository mcTicketsRepository, EmployeeRepository employeeRepository, ProductivityRepository productivityRepository) {
        this.mcTicketsRepository = mcTicketsRepository;
        this.productivityRepository = productivityRepository;
        this.employeeRepository = employeeRepository;
    }

    @Override
    public List<McTicket> getAllMcTickets() {
        return  mcTicketsRepository.findAll();
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
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'calculateMcTicketsPercentage'");
    }

    @Override
    public void updateAverageMcTicketsPercentages() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'updateAverageMcTicketsPercentages'");
    }

    
}
