package com.scoutress.KaimuxAdminStats.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.scoutress.KaimuxAdminStats.services.FinalStatsService;

@RestController
@RequestMapping("/user/stats")
public class UserStatsController {

  private final FinalStatsService finalStatsService;

  public UserStatsController(FinalStatsService finalStatsService) {
    this.finalStatsService = finalStatsService;
  }

  @GetMapping("/{employeeId}")
  public ResponseEntity<Double> getProductivity(@PathVariable Short employeeId) {
    Double productivity = finalStatsService.getProductivity(employeeId);
    return ResponseEntity.ok(productivity);
  }
}
