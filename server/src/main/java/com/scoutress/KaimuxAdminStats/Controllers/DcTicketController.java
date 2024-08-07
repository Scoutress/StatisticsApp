package com.scoutress.KaimuxAdminStats.Controllers;

import java.util.List;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.scoutress.KaimuxAdminStats.Entity.DcTickets.DcTicket;
import com.scoutress.KaimuxAdminStats.Entity.DcTickets.DcTicketPercentage;
import com.scoutress.KaimuxAdminStats.Repositories.DcTickets.DcTicketPercentageRepository;
import com.scoutress.KaimuxAdminStats.Repositories.DcTickets.DcTicketRepository;
import com.scoutress.KaimuxAdminStats.Services.DcTicketService;

@RestController
@RequestMapping("/dc-tickets")
@CrossOrigin(origins = "http://localhost:5173")
public class DcTicketController {

    private final DcTicketService dcTicketService;
    private final DcTicketRepository dcTicketRepository;
    private final DcTicketPercentageRepository dcTicketPercentageRepository;

    public DcTicketController(DcTicketService dcTicketService, DcTicketRepository dcTicketRepository, DcTicketPercentageRepository dcTicketPercentageRepository) {
        this.dcTicketService = dcTicketService;
        this.dcTicketRepository = dcTicketRepository;
        this.dcTicketPercentageRepository = dcTicketPercentageRepository;
    }

    @PostMapping("/add")
    public void addDcTickets(@RequestBody List<DcTicket> dcTickets) {
        dcTicketService.saveAll(dcTickets);
    }

    @GetMapping("/all")
    public List<DcTicket> getAllDcTickets() {
        return dcTicketRepository.findAll();
    }

    @GetMapping("/compare")
    public List<DcTicketPercentage> getAllComparedDcTickets() {
        return dcTicketPercentageRepository.findAll();
    }
}