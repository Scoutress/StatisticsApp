package com.scoutress.KaimuxAdminStats.DummyData;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;


@Controller
@RequestMapping("/dummy")
public class DummyDataController {
    
    private final DummyDataService dummyDataService;

    public DummyDataController(DummyDataService dummyDataService) {
        this.dummyDataService = dummyDataService;
    }

    @GetMapping("/insertMcTickets")
    public ResponseEntity<String> insertDummyMcTicketsData(){
        dummyDataService.insertDummyMcTicketsData();
        return ResponseEntity.ok("Dummy data inserted successfully!");
    }
}
