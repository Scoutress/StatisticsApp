package lt.scoutress.StatisticsApp.Controllers;

import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import lt.scoutress.StatisticsApp.Services.playtime.PlaytimeService;
import lt.scoutress.StatisticsApp.entity.Playtime.Playtime;

@Controller
@RequestMapping("/playtime")
public class PlaytimeController {
    
    private final PlaytimeService playtimeService;

    public PlaytimeController(PlaytimeService playtimeService) {
        this.playtimeService = playtimeService;
    }

    @GetMapping("")
    public String getAllPlaytimeData(Model model) {
        List<Playtime> playtime = playtimeService.findAll();
        model.addAttribute("playtime", playtime);
        return "stats/playtime-data";
    }

    // @RequestMapping(value = "/transferData", method = {RequestMethod.GET, RequestMethod.POST})
    // public String transferData() {
    //     //dataTransferService.transferDataFromSQLiteToMySQL();
    //     return "redirect:/playtime/employeeCodes";
    // }

}
