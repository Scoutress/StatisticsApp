package com.scoutress.KaimuxAdminStats.Controllers;

import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.scoutress.KaimuxAdminStats.Services.playtime.PlaytimeService;
import com.scoutress.KaimuxAdminStats.entity.Playtime.Playtime;

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
