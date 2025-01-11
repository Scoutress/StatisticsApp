package com.scoutress.KaimuxAdminStats.controllers;

import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.scoutress.KaimuxAdminStats.services.FinalStatsService;

@RestController
@RequestMapping("/user")
public class UserStatsController {

  private final FinalStatsService finalStatsService;

  public UserStatsController(FinalStatsService finalStatsService) {
    this.finalStatsService = finalStatsService;
  }

  @GetMapping("/stats/{employeeId}")
  public ResponseEntity<Double> getProductivity(@PathVariable Short employeeId) {
    Double productivity = finalStatsService.getProductivity(employeeId);
    return ResponseEntity.ok(productivity);
  }

  @GetMapping("/ranking/{employeeId}")
  public ResponseEntity<Map<String, Object>> getEmployeeRanking(@PathVariable Short employeeId) {
    Map<String, Object> rankingData = finalStatsService.getEmployeeRanking(employeeId);
    return ResponseEntity.ok(rankingData);
  }
}
