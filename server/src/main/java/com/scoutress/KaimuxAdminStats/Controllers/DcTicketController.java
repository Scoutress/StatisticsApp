package com.scoutress.KaimuxAdminStats.Controllers;

import java.util.List;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.scoutress.KaimuxAdminStats.Services.DcTicketService;
import com.scoutress.KaimuxAdminStats.entity.dcTickets.DcTicket;

@RestController
@RequestMapping("/dc-tickets")
@CrossOrigin(origins = "http://localhost:5173")
public class DcTicketController {

    private final DcTicketService dcTicketService;

    public DcTicketController(DcTicketService dcTicketService) {
        this.dcTicketService = dcTicketService;
    }

    @PostMapping("/add")
    public void addDcTickets(@RequestBody List<DcTicket> dcTickets) {
        dcTicketService.saveAll(dcTickets);
    }
}