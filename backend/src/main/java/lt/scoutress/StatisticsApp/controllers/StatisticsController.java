package lt.scoutress.StatisticsApp.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import lt.scoutress.StatisticsApp.entity.McTickets.McTicketsAnswered;
import lt.scoutress.StatisticsApp.services.McTicketsService;

@Controller
@RequestMapping("/stats")
public class StatisticsController {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    private final McTicketsService mcTicketsService;
    
    public StatisticsController(McTicketsService mcTicketsService) {
        this.mcTicketsService = mcTicketsService;
    }

    @GetMapping("/productivity")
    public String getHelpRequestsPage() {
        return "productivity";
    }

    @GetMapping("/createTable")
    public String createTablePage() {
        return "create-table";
    }

    @PostMapping("/createTable")
    public String createTable(@RequestParam String username) {
        String dcTable = username + "_dc";
        String helpTable = username + "_help";
        String playtimeTable = username + "_playtime";
        String productivityTable = username + "_productivity";

        String createDcTableQuery = "CREATE TABLE " + dcTable + " ("
                + "id INT AUTO_INCREMENT PRIMARY KEY,"
                + "date DATE,"
                + "dc_tickets INT,"
                + "dc_comparison DECIMAL(5,2)"
                + ")";
        jdbcTemplate.execute(createDcTableQuery);

        String createHelpTableQuery = "CREATE TABLE " + helpTable + " ("
                + "id INT AUTO_INCREMENT PRIMARY KEY,"
                + "date DATE,"
                + "help_answered INT,"
                + "help_count INT,"
                + "help_comparison DECIMAL(5,2),"
                + "help_percent DECIMAL(5,2)"
                + ")";
        jdbcTemplate.execute(createHelpTableQuery);

        String createPlaytimeTableQuery = "CREATE TABLE " + playtimeTable + " ("
                + "id INT AUTO_INCREMENT PRIMARY KEY,"
                + "date DATE,"
                + "playtime_survival_connect DECIMAL(5,2),"
                + "playtime_survival_disconnect DECIMAL(5,2),"
                + "playtime_survival_afk DECIMAL(5,2),"
                + "playtime_skyblock_connect DECIMAL(5,2),"
                + "playtime_skyblock_disconnect DECIMAL(5,2),"
                + "playtime_skyblock_afk DECIMAL(5,2),"
                + "playtime_creative_connect DECIMAL(5,2),"
                + "playtime_creative_disconnect DECIMAL(5,2),"
                + "playtime_creative_afk DECIMAL(5,2),"
                + "playtime_boxpvp_connect DECIMAL(5,2),"
                + "playtime_boxpvp_disconnect DECIMAL(5,2),"
                + "playtime_boxpvp_afk DECIMAL(5,2),"
                + "playtime_prison_connect DECIMAL(5,2),"
                + "playtime_prison_disconnect DECIMAL(5,2),"
                + "playtime_prison_afk DECIMAL(5,2),"
                + "playtime_events_connect DECIMAL(5,2),"
                + "playtime_events_disconnect DECIMAL(5,2),"
                + "playtime_events_afk DECIMAL(5,2)"
                + ")";
        jdbcTemplate.execute(createPlaytimeTableQuery);

        String createProductivityTableQuery = "CREATE TABLE " + productivityTable + " ("
                + "id INT AUTO_INCREMENT PRIMARY KEY,"
                + "days_since_join INT "
                // + "help_answered INT,"
                // + "help_count INT,"
                // + "help_comparison DECIMAL(5,2),"
                // + "help_percent DECIMAL(5,2)"
                + ")";
        jdbcTemplate.execute(createProductivityTableQuery);

        return "redirect:/main";
    }

    @GetMapping("/allTables")
    public String openHelpRequestsMain() {
        return "stats-tables";
    }

    @GetMapping("/mcTicketsData")
    public String getAllMcTicketsData(Model model) {
        List<McTicketsAnswered> tickets = mcTicketsService.findAll();
        model.addAttribute("tickets", tickets);
        return "stats/mc-tickets-data";
    }

    @GetMapping("/addMcTickets")
    public String showAddMcTicketsForm(Model model) {
        model.addAttribute("mcTickets", new McTicketsAnswered());
        return "mc-tickets-add";
    }

    @PostMapping("/saveMcTickets")
    public String saveMcTickets(@ModelAttribute McTicketsAnswered mcTickets) {
        mcTicketsService.save(mcTickets);
        return "redirect:/stats/mcTicketsData";
    }

}
