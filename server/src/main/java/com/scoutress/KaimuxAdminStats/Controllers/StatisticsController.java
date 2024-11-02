package com.scoutress.KaimuxAdminStats.Controllers;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

// import com.scoutress.KaimuxAdminStats.Entity.old.Productivity;
// import com.scoutress.KaimuxAdminStats.Services.old.ProductivityService;

@RestController
@RequestMapping("/stats")
@CrossOrigin(origins = "http://localhost:5173")
public class StatisticsController {

    // private final ProductivityService productivityService;

    // public StatisticsController(ProductivityService productivityService) {
    // this.productivityService = productivityService;
    // }

    // @GetMapping("/productivity")
    // public List<Productivity> getAllProductivity() {
    // return productivityService.findAll();
    // }

    // @GetMapping("/promotions")
    // public ResponseEntity<List<EmployeePromotionsPlus>>
    // getAllEmployeePromotions() {
    // List<EmployeePromotionsPlus> promotions =
    // employeePromotionsService.getAllEmployeePromotionsWithEmployeeData();
    // return new ResponseEntity<>(promotions, HttpStatus.OK);
    // }
}
