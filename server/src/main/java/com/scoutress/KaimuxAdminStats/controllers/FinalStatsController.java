package com.scoutress.KaimuxAdminStats.controllers;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.scoutress.KaimuxAdminStats.entity.FinalStats;
import com.scoutress.KaimuxAdminStats.services.FinalStatsService;
import com.scoutress.KaimuxAdminStats.services.TaskService;

@RestController
@RequestMapping("/stats")
public class FinalStatsController {

  private final FinalStatsService finalStatsService;
  private final TaskService taskService;

  public FinalStatsController(
      FinalStatsService finalStatsService,
      TaskService taskService) {
    this.finalStatsService = finalStatsService;
    this.taskService = taskService;
  }

  @GetMapping("/productivity")
  public List<FinalStats> getProductivityStats() {
    return finalStatsService.getAllFinalStats();
  }

  @PostMapping("/update")
  public ResponseEntity<String> triggerMethods() {
    // taskService.runScheduledTasks();
    taskService.runBackupDataUploadingTasks();
    return ResponseEntity.ok("Tasks completed successfully.");
  }
}
