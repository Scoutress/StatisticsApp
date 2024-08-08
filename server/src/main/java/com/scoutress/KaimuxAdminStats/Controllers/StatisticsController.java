package com.scoutress.KaimuxAdminStats.Controllers;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.scoutress.KaimuxAdminStats.Services.ProductivityService;
import com.scoutress.KaimuxAdminStats.entity.Productivity;

@RestController
@RequestMapping("/stats")
public class StatisticsController {

    private final ProductivityService productivityService;

    public StatisticsController(ProductivityService productivityService) {
        this.productivityService = productivityService;
    }
        
    @GetMapping("/productivity")
    public List<Productivity> getAllProductivity() {
        return productivityService.findAll();
    }
}
