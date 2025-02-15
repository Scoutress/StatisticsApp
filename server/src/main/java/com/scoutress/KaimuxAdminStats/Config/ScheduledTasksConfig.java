package com.scoutress.KaimuxAdminStats.config;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

import com.scoutress.KaimuxAdminStats.services.ApiDataExtractionService;
import com.scoutress.KaimuxAdminStats.services.TaskService;
import com.scoutress.KaimuxAdminStats.services.minecraftTickets.MinecraftTicketsService;

import jakarta.transaction.Transactional;

@Configuration
@EnableScheduling
public class ScheduledTasksConfig {

  private final ScheduledExecutorService scheduledExecutorService = Executors.newSingleThreadScheduledExecutor();
  private final TaskService taskService;
  private final ApiDataExtractionService apiDataExtractionService;
  private final MinecraftTicketsService minecraftTicketsService;

  public ScheduledTasksConfig(TaskService taskService,
      ApiDataExtractionService apiDataExtractionService,
      MinecraftTicketsService minecraftTicketsService) {
    this.taskService = taskService;
    this.apiDataExtractionService = apiDataExtractionService;
    this.minecraftTicketsService = minecraftTicketsService;
  }

  @Scheduled(cron = "0 0 0 * * ?")
  @Transactional
  public void runScheduledTasks() {
    System.out.println("-----------------------------------------------");
    System.out.println("Started scheduled tasks at: " + getCurrentTimestamp());
    System.out.println("");

    taskService.runScheduledTasks();

    System.out.println("");
    System.out.println("Scheduled tasks completed at: " + getCurrentTimestamp());
    System.out.println("-----------------------------------------------");
  }

  // @Scheduled(cron = "0 40 * * * *")
  @Transactional
  public void testTasks() {
    System.out.println("-----------------------------------------------");
    System.out.println("Started testing tasks at: " + getCurrentTimestamp());
    System.out.println("");

    scheduledExecutorService.schedule(() -> runApiDataExtraction(), 0, TimeUnit.MINUTES);
    scheduledExecutorService.schedule(() -> runConvertMinecraftTicketsAnswers(), 1, TimeUnit.MINUTES);
    scheduledExecutorService.schedule(() -> runCalculateAverageDailyMinecraftTicketsValues(), 2, TimeUnit.MINUTES);

    System.out.println("");
    System.out.println("Testing tasks completed at: " + getCurrentTimestamp());
    System.out.println("-----------------------------------------------");
  }

  private void runApiDataExtraction() {
    apiDataExtractionService.handleMinecraftTicketsRawData();
    System.out.println("handleMinecraftTicketsRawData completed at: " + getCurrentTimestamp());
  }

  private void runConvertMinecraftTicketsAnswers() {
    minecraftTicketsService.convertMinecraftTicketsAnswers();
    System.out.println("convertMinecraftTicketsAnswers completed at: " + getCurrentTimestamp());
  }

  private void runCalculateAverageDailyMinecraftTicketsValues() {
    minecraftTicketsService.calculateAverageDailyMinecraftTicketsValues();
    System.out.println("calculateAverageDailyMinecraftTicketsValues completed at: " + getCurrentTimestamp());
  }

  private String getCurrentTimestamp() {
    return LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
  }
}
