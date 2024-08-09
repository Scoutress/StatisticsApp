package com.scoutress.KaimuxAdminStats.Controllers;

import java.util.List;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.scoutress.KaimuxAdminStats.Entity.DcTickets.DcTicket;
import com.scoutress.KaimuxAdminStats.Repositories.DcTicketRepository;
import com.scoutress.KaimuxAdminStats.Services.DcTicketService;

@RestController
@RequestMapping("/dc-tickets")
@CrossOrigin(origins = "http://localhost:5173")
public class DcTicketController {

    private final DcTicketService dcTicketService;
    private final DcTicketRepository dcTicketRepository;

    public DcTicketController(DcTicketService dcTicketService, DcTicketRepository dcTicketRepository) {
        this.dcTicketService = dcTicketService;
        this.dcTicketRepository = dcTicketRepository;
    }

    @PostMapping("/add")
    public void addDcTickets(@RequestBody List<DcTicket> dcTickets) {
        dcTicketService.saveAll(dcTickets);
    }

    @GetMapping("/all")
    public List<DcTicket> getAllDcTickets() {
        return dcTicketRepository.findAll();
    }
}