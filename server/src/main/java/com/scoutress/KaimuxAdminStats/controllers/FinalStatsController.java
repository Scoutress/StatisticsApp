package com.scoutress.KaimuxAdminStats.controllers;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.scoutress.KaimuxAdminStats.entity.FinalStats;
import com.scoutress.KaimuxAdminStats.services.FinalStatsService;

@RestController
@RequestMapping("/stats")
public class FinalStatsController {

  private final FinalStatsService finalStatsService;

  public FinalStatsController(FinalStatsService finalStatsService) {
    this.finalStatsService = finalStatsService;
  }

  @GetMapping("/productivity")
  public List<FinalStats> getProductivityStats() {
    return finalStatsService.getAllFinalStats();
  }
}
