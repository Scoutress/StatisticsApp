package lt.scoutress.StatisticsApp.controllers;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import lt.scoutress.StatisticsApp.entity.McTickets.McTicketsAvgDaily;
import lt.scoutress.StatisticsApp.entity.McTickets.McTicketsAvgDailyResponse;
import lt.scoutress.StatisticsApp.services.McTickets.McTicketsService;

@Controller
@RequestMapping("/stats")
public class StatisticsController {

    @Autowired
    private final McTicketsService mcTicketsService;

    public StatisticsController(McTicketsService mcTicketsService) {
        this.mcTicketsService = mcTicketsService;
    }

    @GetMapping("/avgMcTickets")
    public String getAvgMcTickets(Model model) {
        List<McTicketsAvgDaily> mcTicketsAvgDailyList = mcTicketsService.findAll();

        List<McTicketsAvgDailyResponse> responseList = mcTicketsAvgDailyList.stream()
                .map(mcTicketsAvgDaily -> new McTicketsAvgDailyResponse(mcTicketsAvgDaily
                        .getEmployee()
                        .getUsername(),
                        mcTicketsAvgDaily
                                .getAverageValues()))
                .collect(Collectors.toList());

        model.addAttribute("responses", responseList);
        return "stats/mc-tickets/mc-tickets-avg";
    }
}
