package com.scoutress.KaimuxAdminStats.Servicesimpl;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.scoutress.KaimuxAdminStats.Entity.old.DcTickets.DcTicket;
import com.scoutress.KaimuxAdminStats.Entity.old.DcTickets.DcTicketPercentage;
import com.scoutress.KaimuxAdminStats.Entity.old.Employees.Employee;
import com.scoutress.KaimuxAdminStats.Entity.old.Employees.EmployeePromotions;
import com.scoutress.KaimuxAdminStats.Entity.old.Productivity;
import com.scoutress.KaimuxAdminStats.Repositories.old.DcTickets.DcTicketPercentageRepository;
import com.scoutress.KaimuxAdminStats.Repositories.old.DcTickets.DcTicketRepository;
import com.scoutress.KaimuxAdminStats.Repositories.old.EmployeePromotionsRepository;
import com.scoutress.KaimuxAdminStats.Repositories.old.EmployeeRepository;
import com.scoutress.KaimuxAdminStats.Repositories.old.ProductivityRepository;
import com.scoutress.KaimuxAdminStats.Services.old.DcTicketService;

@Service
public class DcTicketServiceImpl implements DcTicketService {

    private final DcTicketRepository dcTicketRepository;
    private final ProductivityRepository productivityRepository;
    private final DcTicketPercentageRepository dcTicketPercentageRepository;
    private final EmployeeRepository employeeRepository;
    private final EmployeePromotionsRepository employeePromotionsRepository;

    public DcTicketServiceImpl(DcTicketRepository dcTicketRepository, ProductivityRepository productivityRepository,
            DcTicketPercentageRepository dcTicketPercentageRepository, EmployeeRepository employeeRepository,
            EmployeePromotionsRepository employeePromotionsRepository) {
        this.dcTicketRepository = dcTicketRepository;
        this.productivityRepository = productivityRepository;
        this.dcTicketPercentageRepository = dcTicketPercentageRepository;
        this.employeeRepository = employeeRepository;
        this.employeePromotionsRepository = employeePromotionsRepository;
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

            Employee employee = employeeRepository.findById(employeeId).orElse(null);
            if (employee == null) {
                System.out.println("Employee with ID " + employeeId + " not found. Skipping.");
                continue;
            }

            LocalDate joinDate = employee.getJoinDate();
            EmployeePromotions promotions = employeePromotionsRepository.findByEmployeeId(employeeId);
            LocalDate supportDate = promotions != null ? promotions.getToSupport() : null;

            if (supportDate == null || joinDate == null) {
                System.out.println(
                        "Employee with ID " + employeeId + " has no support promotion date or join date. Skipping.");
                continue;
            }

            List<DcTicket> filteredTickets = employeeTickets
                    .stream()
                    .filter(ticket -> !ticket.getDate().isBefore(joinDate))
                    .filter(ticket -> !ticket.getDate().isBefore(supportDate))
                    .collect(Collectors.toList());

            if (filteredTickets.isEmpty()) {
                System.out
                        .println("Employee with ID " + employeeId + " has no valid tickets after filtering. Skipping.");
                continue;
            }

            LocalDate earliestDate = filteredTickets.stream()
                    .map(DcTicket::getDate)
                    .min(LocalDate::compareTo)
                    .orElse(null);

            LocalDate latestDate = filteredTickets.stream()
                    .map(DcTicket::getDate)
                    .max(LocalDate::compareTo)
                    .orElse(null);

            if (earliestDate != null && latestDate != null) {
                long daysBetween = ChronoUnit.DAYS.between(earliestDate, latestDate) + 1;

                int totalTickets = filteredTickets.stream()
                        .mapToInt(DcTicket::getTicketCount)
                        .sum();

                double averageTicketsPerDay = (double) totalTickets / daysBetween;

                Productivity productivity = productivityRepository.findByEmployeeId(employeeId);
                if (productivity == null) {
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

        for (Map.Entry<LocalDate, List<DcTicket>> entry : ticketsByDate.entrySet()) {
            LocalDate date = entry.getKey();
            List<DcTicket> dailyTickets = entry.getValue();

            List<DcTicket> filteredTickets = dailyTickets
                    .stream()
                    .filter(ticket -> {
                        Employee employee = employeeRepository.findById(ticket.getEmployeeId()).orElse(null);
                        if (employee == null) {
                            return false;
                        }
                        LocalDate joinDate = employee.getJoinDate();
                        EmployeePromotions promotions = employeePromotionsRepository
                                .findByEmployeeId(ticket.getEmployeeId());
                        LocalDate supportDate = promotions != null ? promotions.getToSupport() : null;
                        return supportDate != null && joinDate != null && !date.isBefore(joinDate)
                                && !date.isBefore(supportDate);
                    })
                    .collect(Collectors.toList());

            if (filteredTickets.isEmpty()) {
                continue;
            }

            int totalTicketsForDay = filteredTickets.stream()
                    .mapToInt(DcTicket::getTicketCount)
                    .sum();

            for (DcTicket ticket : filteredTickets) {
                double percentage = totalTicketsForDay > 0
                        ? (double) ticket.getTicketCount() / totalTicketsForDay * 100
                        : 0.0;

                DcTicketPercentage dcTicketPercentage = new DcTicketPercentage(
                        ticket.getEmployeeId(), date, percentage);
                dcTicketPercentageRepository.save(dcTicketPercentage);
            }
        }
    }

    @Override
    public void updateAverageDcTicketsPercentages() {
        List<DcTicketPercentage> allPercentages = dcTicketPercentageRepository.findAll();

        Map<Integer, List<DcTicketPercentage>> percentagesPerEmployee = allPercentages.stream()
                .collect(Collectors.groupingBy(DcTicketPercentage::getEmployeeId));

        for (Map.Entry<Integer, List<DcTicketPercentage>> entry : percentagesPerEmployee.entrySet()) {
            Integer employeeId = entry.getKey();
            List<DcTicketPercentage> employeePercentages = entry.getValue();

            double averagePercentage = employeePercentages.stream()
                    .mapToDouble(DcTicketPercentage::getPercentage)
                    .average()
                    .orElse(0.0);

            Productivity productivity = productivityRepository.findByEmployeeId(employeeId);
            if (productivity != null) {
                productivity.setDiscordTicketsTaking(averagePercentage);
                productivityRepository.save(productivity);
            }
        }
    }
}
