package com.scoutress.KaimuxAdminStats.Controllers;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.scoutress.KaimuxAdminStats.Entity.Employees.EmployeePromotionsPlus;
import com.scoutress.KaimuxAdminStats.Entity.Productivity;
import com.scoutress.KaimuxAdminStats.Services.EmployeePromotionsService;
import com.scoutress.KaimuxAdminStats.Services.ProductivityService;

@RestController
@RequestMapping("/stats")
@CrossOrigin(origins = "http://localhost:5173")
public class StatisticsController {

    private final ProductivityService productivityService;
    private final EmployeePromotionsService employeePromotionsService;

    public StatisticsController(ProductivityService productivityService,
            EmployeePromotionsService employeePromotionsService) {
        this.productivityService = productivityService;
        this.employeePromotionsService = employeePromotionsService;
    }

    @GetMapping("/productivity")
    public List<Productivity> getAllProductivity() {
        return productivityService.findAll();
    }

    @GetMapping("/promotions")
    public ResponseEntity<List<EmployeePromotionsPlus>> getAllEmployeePromotions() {
        List<EmployeePromotionsPlus> promotions = employeePromotionsService.getAllEmployeePromotionsWithEmployeeData();
        return new ResponseEntity<>(promotions, HttpStatus.OK);
    }
}
