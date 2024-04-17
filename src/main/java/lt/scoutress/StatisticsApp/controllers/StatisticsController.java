package lt.scoutress.StatisticsApp.Controllers;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import lt.scoutress.StatisticsApp.Services.ProductivityService;
import lt.scoutress.StatisticsApp.Services.McTickets.McTicketsService;
import lt.scoutress.StatisticsApp.entity.Productivity;
import lt.scoutress.StatisticsApp.entity.McTickets.McTicketsAvgDaily;
import lt.scoutress.StatisticsApp.entity.McTickets.McTicketsAvgDailyRatio;
import lt.scoutress.StatisticsApp.entity.McTickets.McTicketsAvgDailyRatioResponse;
import lt.scoutress.StatisticsApp.entity.McTickets.McTicketsAvgDailyResponse;


@Controller
@RequestMapping("/stats")
public class StatisticsController {

    @Autowired
    private final McTicketsService mcTicketsService;
    private final ProductivityService productivityService;

    public StatisticsController(McTicketsService mcTicketsService, ProductivityService productivityService) {
        this.mcTicketsService = mcTicketsService;
        this.productivityService = productivityService;
    }

    @GetMapping("/avgMcTickets")
    public String getAvgMcTickets(Model model) {
        List<McTicketsAvgDaily> mcTicketsAvgDailyList = mcTicketsService.findAllAvgDaily();

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

    @GetMapping("/avgMcTicketsRatio")
    public String getAvgMcTicketsRatio(Model model) {
        List<McTicketsAvgDailyRatio> mcTicketsAvgDailyRatioList = mcTicketsService.findAllAvgDailyRatio();

        List<McTicketsAvgDailyRatioResponse> responseList = mcTicketsAvgDailyRatioList.stream()
                .map(mcTicketsAvgDaily -> new McTicketsAvgDailyRatioResponse(
                        mcTicketsAvgDaily.getEmployee(),
                        mcTicketsAvgDaily.getAverageDailyRatio()
                ))
                .collect(Collectors.toList());

        model.addAttribute("responses", responseList);
        return "stats/mc-tickets/mc-tickets-ratio";
    }

    //avgMcTicketsPerPlaytime

    @GetMapping("/productivity")
    public String getProductivity(Model model) {
        List<Productivity> productivity = productivityService.findAll();
        model.addAttribute("productivities", productivity);
        return "stats/productivity";
    }
}
