package com.scoutress.KaimuxAdminStats.Servicesimpl;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.scoutress.KaimuxAdminStats.Entity.DcTickets.DcTicket;
import com.scoutress.KaimuxAdminStats.Entity.DcTickets.DcTicketPercentage;
import com.scoutress.KaimuxAdminStats.Entity.Employee;
import com.scoutress.KaimuxAdminStats.Entity.Productivity;
import com.scoutress.KaimuxAdminStats.Repositories.DcTickets.DcTicketPercentageRepository;
import com.scoutress.KaimuxAdminStats.Repositories.DcTickets.DcTicketRepository;
import com.scoutress.KaimuxAdminStats.Repositories.EmployeeRepository;
import com.scoutress.KaimuxAdminStats.Repositories.ProductivityRepository;
import com.scoutress.KaimuxAdminStats.Services.DcTicketService;

@Service
public class DcTicketServiceImpl implements DcTicketService {

    private final DcTicketRepository dcTicketRepository;
    private final ProductivityRepository productivityRepository;
    private final DcTicketPercentageRepository dcTicketPercentageRepository;
    private final EmployeeRepository employeeRepository;

    public DcTicketServiceImpl(DcTicketRepository dcTicketRepository, ProductivityRepository productivityRepository, DcTicketPercentageRepository dcTicketPercentageRepository, EmployeeRepository employeeRepository) {
        this.dcTicketRepository = dcTicketRepository;
        this.productivityRepository = productivityRepository;
        this.dcTicketPercentageRepository = dcTicketPercentageRepository;
        this.employeeRepository = employeeRepository;
    }

    @Override
    public void saveAll(List<DcTicket> dcTickets) {
        dcTicketRepository.saveAll(dcTickets);
    }

    @Override
    public void updateDiscordTicketsAverage() {
        List<DcTicket> dcTickets = dcTicketRepository.findAll();

        Map<Integer, List<DcTicket>> ticketsByEmployee = dcTickets.stream()
            .collect(Collectors.groupingBy(DcTicket::getEmployeeId));

        for (Map.Entry<Integer, List<DcTicket>> entry : ticketsByEmployee.entrySet()) {
            Integer employeeId = entry.getKey();
            List<DcTicket> employeeTickets = entry.getValue();

            LocalDate earliestDate = employeeTickets.stream()
                .map(DcTicket::getDate)
                .min(LocalDate::compareTo)
                .orElse(null);

            LocalDate latestDate = employeeTickets.stream()
                .map(DcTicket::getDate)
                .max(LocalDate::compareTo)
                .orElse(null);

            if (earliestDate != null && latestDate != null) {
                long daysBetween = ChronoUnit.DAYS.between(earliestDate, latestDate) + 1;

                int totalTickets = employeeTickets.stream()
                    .mapToInt(DcTicket::getTicketCount)
                    .sum();

                double averageTicketsPerDay = (double) totalTickets / daysBetween;

                Productivity productivity = productivityRepository.findByEmployeeId(employeeId);
                if (productivity == null) {
                    Employee employee = employeeRepository.findById(employeeId).orElse(null);
                    if (employee == null) {
                        System.out.println("Employee with ID " + employeeId + " not found. Skipping.");
                        continue;
                    }
                    productivity = new Productivity();
                    productivity.setEmployee(employee);
                }
                productivity.setDiscordTickets(averageTicketsPerDay);
                productivityRepository.save(productivity);
            }
        }
    }


    @Override
    public void calculateDcTicketsPercentage() {
        List<DcTicket> dcTickets = dcTicketRepository.findAll();

        Map<LocalDate, List<DcTicket>> ticketsByDate = dcTickets.stream()
            .collect(Collectors.groupingBy(DcTicket::getDate));

        for(Map.Entry<LocalDate, List<DcTicket>> entry : ticketsByDate.entrySet()){
            LocalDate date = entry.getKey();
            List<DcTicket> dailyTickets = entry.getValue();

            int totalTicketsForDay = dailyTickets.stream()
                .mapToInt(DcTicket::getTicketCount)
                .sum();
            
            for(DcTicket ticket : dailyTickets){
                double percentage = totalTicketsForDay > 0 
                ? (double) ticket.getTicketCount() / totalTicketsForDay * 100
                : 0.0;

                DcTicketPercentage dcTicketPercentage = new DcTicketPercentage(
                    ticket.getEmployeeId(), date, percentage
                );
                dcTicketPercentageRepository.save(dcTicketPercentage);
            }
        }
    }

    @Override
    public void updateAverageDcTicketsPercentages() {
        List<DcTicketPercentage> allPercentages = dcTicketPercentageRepository.findAll();

        Map<Integer, List<DcTicketPercentage>> percentagesPerEmployee = allPercentages.stream()
            .collect(Collectors.groupingBy(DcTicketPercentage::getEmployeeId));
        
        for(Map.Entry<Integer, List<DcTicketPercentage>> entry : percentagesPerEmployee.entrySet()){
            Integer employeeId = entry.getKey();
            List<DcTicketPercentage> employPercentages = entry.getValue();

            double averagePercentage = employPercentages.stream()
                .mapToDouble(DcTicketPercentage::getPercentage)
                .average()
                .orElse(0.0);

            Productivity productivity = productivityRepository.findByEmployeeId(employeeId);
            if(productivity != null){
                productivity.setDiscordTicketsTaking(averagePercentage);
                productivityRepository.save(productivity);
            }
        }
    }
}
