package com.scoutress.KaimuxAdminStats.config;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

import com.scoutress.KaimuxAdminStats.services.DataFetchingService;

import jakarta.transaction.Transactional;

@Configuration
@EnableScheduling
public class ScheduledTasksConfig {

  private final DataFetchingService dataFetchingService;

  public ScheduledTasksConfig(DataFetchingService dataFetchingService) {
    this.dataFetchingService = dataFetchingService;
  }

  @Scheduled(initialDelay = 1000, fixedRate = 86400000)
  @Transactional
  public void runDataExtractionFromAPI() {
    System.out.println("Scheduled tasks started at: " + getCurrentTimestamp());
    System.out.println("");

    dataFetchingService.fetchAndSaveData(1, "discord");
    dataFetchingService.fetchAndSaveData(1, "minecraft");
    dataFetchingService.fetchAndSaveData(1, "visitors");

    System.out.println("Scheduled tasks completed at: " + getCurrentTimestamp());
    System.out.println("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");
  }

  private String getCurrentTimestamp() {
    return LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
  }
}