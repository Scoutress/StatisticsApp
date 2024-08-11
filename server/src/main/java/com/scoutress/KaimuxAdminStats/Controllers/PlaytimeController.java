package com.scoutress.KaimuxAdminStats.Controllers;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.scoutress.KaimuxAdminStats.Entity.Playtime.Playtime;
import com.scoutress.KaimuxAdminStats.Repositories.PlaytimeRepository;

@RestController
@RequestMapping("/playtime")
@CrossOrigin(origins = "http://localhost:5173")
public class PlaytimeController {

    private final PlaytimeRepository playtimeRepository;

    public PlaytimeController(PlaytimeRepository playtimeRepository) {
        this.playtimeRepository = playtimeRepository;
    }

    @GetMapping("/all")
    public List<Playtime> getAllPlaytime() {
        return playtimeRepository.findAll();
    }

    @PostMapping("/add")
    public ResponseEntity<String> addPlaytime(@RequestBody List<Playtime> playtimeList) {
        playtimeRepository.saveAll(playtimeList);
        return ResponseEntity.ok("Playtime and AFK Playtime data added successfully");
    }
}
