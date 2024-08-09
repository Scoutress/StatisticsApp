package com.scoutress.KaimuxAdminStats.Servicesimpl;


import java.util.List;

import org.springframework.stereotype.Service;

import com.scoutress.KaimuxAdminStats.Entity.McTicket;
import com.scoutress.KaimuxAdminStats.Repositories.McTicketRepository;
import com.scoutress.KaimuxAdminStats.Services.McTicketService;

@Service
public class McTicketServiceImpl implements McTicketService {

    private final McTicketRepository mcTicketsRepository;

    public McTicketServiceImpl(McTicketRepository mcTicketsRepository) {
        this.mcTicketsRepository = mcTicketsRepository;
    }

    @Override
    public List<McTicket> getAllMcTickets() {
        return  mcTicketsRepository.findAll();
    }

    @Override
    public void saveAll(List<McTicket> mcTickets) {
        mcTicketsRepository.saveAll(mcTickets);
    }

    
}
