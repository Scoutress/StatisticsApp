package com.scoutress.KaimuxAdminStats.Controllers;

import java.util.List;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.scoutress.KaimuxAdminStats.Entity.McTickets.McTicket;
import com.scoutress.KaimuxAdminStats.Repositories.McTickets.McTicketRepository;
import com.scoutress.KaimuxAdminStats.Services.McTicketService;

@RestController
@RequestMapping("/mc-tickets")
@CrossOrigin(origins = "http://localhost:5173")
public class McTicketController {
  
  private final McTicketService mcTicketsService;
  private final McTicketRepository mcTicketRepository;

    public McTicketController(McTicketService mcTicketsService, McTicketRepository mcTicketRepository) {
        this.mcTicketsService = mcTicketsService;
        this.mcTicketRepository = mcTicketRepository;
    }

    @PostMapping("/add")
    public void addMcTickets(@RequestBody List<McTicket> mcTickets){
      mcTicketsService.saveAll(mcTickets);
    }
    
    @GetMapping("/all")
    public List<McTicket> getAllMcTickets() {
        return mcTicketRepository.findAll();
    }
}
