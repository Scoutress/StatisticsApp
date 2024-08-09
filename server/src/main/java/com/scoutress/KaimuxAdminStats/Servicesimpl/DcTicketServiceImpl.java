package com.scoutress.KaimuxAdminStats.Servicesimpl;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.scoutress.KaimuxAdminStats.Entity.DcTickets.DcTicket;
import com.scoutress.KaimuxAdminStats.Entity.Productivity;
import com.scoutress.KaimuxAdminStats.Repositories.DcTicketRepository;
import com.scoutress.KaimuxAdminStats.Repositories.ProductivityRepository;
import com.scoutress.KaimuxAdminStats.Services.DcTicketService;

@Service
public class DcTicketServiceImpl implements DcTicketService {

    private final DcTicketRepository dcTicketRepository;
    private final ProductivityRepository productivityRepository;

    public DcTicketServiceImpl(DcTicketRepository dcTicketRepository, ProductivityRepository productivityRepository) {
        this.dcTicketRepository = dcTicketRepository;
        this.productivityRepository = productivityRepository;
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

        for (Map.Entry<Integer, List<DcTicket>> entry : ticketsByEmployee.entrySet()){
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

            if(earliestDate != null && latestDate != null){
                long daysBetween = ChronoUnit.DAYS.between(earliestDate, latestDate);

                int totalTickets = employeeTickets.stream()
                    .mapToInt(DcTicket::getTicketCount)
                    .sum();

                double averageTicketsPerDay = (double) totalTickets / daysBetween;

                Productivity productivity = productivityRepository.findByEmployeeId(employeeId);
                if(productivity != null){
                    productivity.setDiscordTickets(averageTicketsPerDay);
                    productivityRepository.save(productivity);
                }
            }
        }
    }
}
