package com.scoutress.KaimuxAdminStats.Controllers;

import java.time.LocalDate;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.scoutress.KaimuxAdminStats.Entity.Playtime;
import com.scoutress.KaimuxAdminStats.Repositories.PlaytimeRepository;

@RestController
@RequestMapping("/playtime")
@CrossOrigin(origins = "http://localhost:5173")
public class PlaytimeController {

    @Autowired
    private PlaytimeRepository playtimeRepository;

    @GetMapping("/all")
    public List<Playtime> getAllPlaytime() {
        return playtimeRepository.findAll();
    }

    @GetMapping("/by-date")
    public List<Playtime> getPlaytimeByDate(@RequestParam("date") LocalDate date) {
        return playtimeRepository.findByDate(date);
    }

    @GetMapping("/by-employee")
    public List<Playtime> getPlaytimeByEmployee(@RequestParam("employeeId") Integer employeeId,
                                                @RequestParam("startDate") LocalDate startDate,
                                                @RequestParam("endDate") LocalDate endDate) {
        return playtimeRepository.findByEmployeeIdAndDateBetween(employeeId, startDate, endDate);
    }
}
