package com.scoutress.KaimuxAdminStats.Servicesimpl;

import java.util.List;

import org.springframework.stereotype.Service;

import com.scoutress.KaimuxAdminStats.Entity.DcTickets.DcTicket;
import com.scoutress.KaimuxAdminStats.Repositories.DcTicketRepository;
import com.scoutress.KaimuxAdminStats.Services.DcTicketService;

@Service
public class DcTicketServiceImpl implements DcTicketService {

    private final DcTicketRepository dcTicketRepository;

    public DcTicketServiceImpl(DcTicketRepository dcTicketRepository) {
        this.dcTicketRepository = dcTicketRepository;
    }

    @Override
    public void saveAll(List<DcTicket> dcTickets) {
        dcTicketRepository.saveAll(dcTickets);
    }
}
