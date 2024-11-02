package com.scoutress.KaimuxAdminStats.Controllers;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

// import com.scoutress.KaimuxAdminStats.Entity.old.DcTickets.DcTicket;
// import com.scoutress.KaimuxAdminStats.Entity.old.DcTickets.DcTicketPercentage;
// import com.scoutress.KaimuxAdminStats.Repositories.old.DcTickets.DcTicketPercentageRepository;
// import com.scoutress.KaimuxAdminStats.Repositories.old.DcTickets.DcTicketRepository;
// import com.scoutress.KaimuxAdminStats.Services.old.DcTicketService;

@RestController
@RequestMapping("/dc-tickets")
@CrossOrigin(origins = "http://localhost:5173")
public class DcTicketController {

    // private final DcTicketService dcTicketService;
    // private final DcTicketRepository dcTicketRepository;
    // private final DcTicketPercentageRepository dcTicketPercentageRepository;

    // public DcTicketController(DcTicketService dcTicketService, DcTicketRepository
    // dcTicketRepository,
    // DcTicketPercentageRepository dcTicketPercentageRepository) {
    // this.dcTicketService = dcTicketService;
    // this.dcTicketRepository = dcTicketRepository;
    // this.dcTicketPercentageRepository = dcTicketPercentageRepository;
    // }

    // @PostMapping("/add")
    // public void addDcTickets(@RequestBody List<DcTicket> dcTickets) {
    // dcTicketService.saveAll(dcTickets);
    // }

    // @GetMapping("/all")
    // public List<DcTicket> getAllDcTickets() {
    // return dcTicketRepository.findAll();
    // }

    // @GetMapping("/compare")
    // public List<DcTicketPercentage> getAllComparedDcTickets() {
    // return dcTicketPercentageRepository.findAll();
    // }
}