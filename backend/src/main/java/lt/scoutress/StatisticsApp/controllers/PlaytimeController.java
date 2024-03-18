package lt.scoutress.StatisticsApp.controllers;

import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import lt.scoutress.StatisticsApp.entity.playtime.Playtime;
import lt.scoutress.StatisticsApp.entity.playtime.PlaytimeDBCodes;
import lt.scoutress.StatisticsApp.services.playtime.PlaytimeDBCodesService;
import lt.scoutress.StatisticsApp.services.playtime.PlaytimeService;

@Controller
@RequestMapping("/playtime")
public class PlaytimeController {
    
    private final PlaytimeService playtimeService;
    private final PlaytimeDBCodesService playtimeDBCodesService;

    public PlaytimeController(PlaytimeService playtimeService, PlaytimeDBCodesService playtimeDBCodesService) {
        this.playtimeService = playtimeService;
        this.playtimeDBCodesService = playtimeDBCodesService;
    }

    @GetMapping("")
    public String getAllPlaytimeData(Model model) {
        List<Playtime> playtime = playtimeService.findAll();
        model.addAttribute("playtime", playtime);
        return "stats/playtime-data";
    }

    @GetMapping("/employeeCodes")
    public String getAllEmployeeDBCodes(Model model) {
        List<PlaytimeDBCodes> employeeCodes = playtimeDBCodesService.findAll();
        model.addAttribute("employeeCodes", employeeCodes);
        return "stats/playtime/playtime-db-codes";
    }

    @GetMapping("/showFormForUpdate")
    public String showFormForUpdate(@RequestParam("id") int id, Model model){
        PlaytimeDBCodes dbCodes = playtimeDBCodesService.findById(id);
        model.addAttribute("dbCodes", dbCodes);
        return "stats/playtime/playtime-db-code-edit-form";
    }

    @PostMapping("/save")
    public String saveDBCodes(@ModelAttribute("dbCodes") PlaytimeDBCodes dbCodes){
        playtimeDBCodesService.save(dbCodes);
        return "redirect:/playtime/employeeCodes";
    }

    // @RequestMapping(value = "/transferData", method = {RequestMethod.GET, RequestMethod.POST})
    // public String transferData() {
    //     //dataTransferService.transferDataFromSQLiteToMySQL();
    //     return "redirect:/playtime/employeeCodes";
    // }

}
