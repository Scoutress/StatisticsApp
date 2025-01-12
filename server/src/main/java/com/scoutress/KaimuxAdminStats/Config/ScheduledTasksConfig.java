package com.scoutress.KaimuxAdminStats.config;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

import com.scoutress.KaimuxAdminStats.services.TaskService;

import jakarta.transaction.Transactional;

@Configuration
@EnableScheduling
public class ScheduledTasksConfig {

  private final TaskService taskService;

  public ScheduledTasksConfig(TaskService taskService) {
    this.taskService = taskService;
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

  // @Scheduled(cron = "0 * * * * *")
  @Transactional
  public void testTasks() {
    System.out.println("-----------------------------------------------");
    System.out.println("Started testing tasks at: " + getCurrentTimestamp());
    System.out.println("");

    System.out.println("");
    System.out.println("Testing tasks completed at: " + getCurrentTimestamp());
    System.out.println("-----------------------------------------------");
  }

  private String getCurrentTimestamp() {
    return LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
  }
}
