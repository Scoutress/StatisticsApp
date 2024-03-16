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

import lt.scoutress.StatisticsApp.entity.Productivity;
import lt.scoutress.StatisticsApp.entity.DcMessages.DcMessagesCalc;
import lt.scoutress.StatisticsApp.entity.DcMessages.DcMessagesTexted;
import lt.scoutress.StatisticsApp.entity.McTickets.McTicketsAnswered;
import lt.scoutress.StatisticsApp.entity.McTickets.McTicketsCalculations;
import lt.scoutress.StatisticsApp.entity.playtime.Playtime;
import lt.scoutress.StatisticsApp.repositories.DcMessagesRepository;
import lt.scoutress.StatisticsApp.repositories.McTicketsRepository;
import lt.scoutress.StatisticsApp.services.DcMessagesService;
import lt.scoutress.StatisticsApp.services.McTicketsService;
import lt.scoutress.StatisticsApp.services.PlaytimeService;
import lt.scoutress.StatisticsApp.services.ProductivityService;

@Controller
@RequestMapping("/stats")
public class StatisticsController {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    private final McTicketsService mcTicketsService;
    private final DcMessagesService dcMessagesService;
    private final PlaytimeService playtimeService;
    private final ProductivityService productivityService;
    private final McTicketsRepository mcTicketsRepository;
    private final DcMessagesRepository dcMessagesRepository;
    
    public StatisticsController(McTicketsService mcTicketsService, McTicketsRepository mcTicketsRepository, DcMessagesService dcMessagesService, ProductivityService productivityService, DcMessagesRepository dcMessagesRepository, PlaytimeService playtimeService) {
        this.mcTicketsService = mcTicketsService;
        this.dcMessagesService = dcMessagesService;
        this.playtimeService = playtimeService;
        this.productivityService = productivityService;
        this.mcTicketsRepository = mcTicketsRepository;
        this.dcMessagesRepository = dcMessagesRepository;
    }

    @GetMapping("/productivity")
    public String getHelpRequestsPage(Model model) {
        List<Productivity> product = productivityService.findAll();
        model.addAttribute("product", product);
        return "stats/productivity";
    }

    @GetMapping("/createTable")
    public String createTablePage() {
        return "add-data/create-table";
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
        return "stats/stats-tables";
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
        return "add-data/mc-tickets-add";
    }

    @PostMapping("/saveMcTickets")
    public String saveMcTickets(@ModelAttribute("mcTickets") McTicketsAnswered mcTickets) {
        Double sharansMcTickets = mcTickets.getSharansMcTickets();
        Double updatedSharansMcTickets = sharansMcTickets + 189;
        mcTickets.setSharansMcTickets(updatedSharansMcTickets);
        mcTicketsRepository.save(mcTickets);
        return "redirect:/stats/mcTicketsData";
    }

    @GetMapping("/mcTicketsCalc")
    public String getAllMcTicketsCalculations(Model model) {
        List<McTicketsCalculations> tickets = mcTicketsService.findAllCalc();
        model.addAttribute("tickets", tickets);
        return "stats/mc-tickets-calc";
    }

    @GetMapping("/dcMessagesData")
    public String getAllDcMessagesData(Model model) {
        List<DcMessagesTexted> messages = dcMessagesService.findAll();
        model.addAttribute("messages", messages);
        return "stats/dc-messages-data";
    }

    @GetMapping("/dcMessagesCalc")
    public String getAllDcMessagesCalculations(Model model) {
        List<DcMessagesCalc> messages = dcMessagesService.findAllCalc();
        model.addAttribute("messages", messages);
        return "stats/dc-messages-calc";
    }

    @GetMapping("/addDcMessages")
    public String showAddDcMessagesForm(Model model) {
        model.addAttribute("dcMessages", new DcMessagesTexted());
        return "add-data/dc-messages-add";
    }

    @SuppressWarnings("null")
    @PostMapping("/saveDcMessages")
    public String saveDcMessages(@ModelAttribute("dcMessages") DcMessagesTexted dcMessages) {
        dcMessagesRepository.save(dcMessages);
        return "redirect:/stats/dcMessagesData";
    }

    @GetMapping("/playtime")
    public String getAllPlaytimeData(Model model) {
        List<Playtime> playtime = playtimeService.findAll();
        model.addAttribute("playtime", playtime);
        return "stats/playtime-data";
    }
}
