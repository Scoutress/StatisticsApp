package com.scoutress.KaimuxAdminStats.controllers;

import java.util.HashMap;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.scoutress.KaimuxAdminStats.entity.RecommendationUser;
import com.scoutress.KaimuxAdminStats.repositories.RecommendationUserRepository;
import com.scoutress.KaimuxAdminStats.services.FinalStatsService;

@RestController
@RequestMapping("/user")
public class UserStatsController {

  private final FinalStatsService finalStatsService;
  private final RecommendationUserRepository recommendationUserRepository;

  public UserStatsController(
      FinalStatsService finalStatsService,
      RecommendationUserRepository recommendationUserRepository) {
    this.finalStatsService = finalStatsService;
    this.recommendationUserRepository = recommendationUserRepository;
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

  @GetMapping("/recommendation/{employeeId}")
  public ResponseEntity<Map<String, Object>> getEmployeeRecommendation(@PathVariable Short employeeId) {
    RecommendationUser recommendationData = recommendationUserRepository.findByEmployeeId(employeeId);
    Map<String, Object> response = new HashMap<>();

    response.put("text", recommendationData.getText());
    return ResponseEntity.ok(response);
  }
}
