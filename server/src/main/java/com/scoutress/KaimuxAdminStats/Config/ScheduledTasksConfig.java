package com.scoutress.KaimuxAdminStats.config;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

import com.scoutress.KaimuxAdminStats.services.ProjectVisitorsRawDataService;
import com.scoutress.KaimuxAdminStats.services.discordTickets.DiscordTicketsReactionsService;
import com.scoutress.KaimuxAdminStats.services.minecraftTickets.MinecraftTicketsAnswersService;
import com.scoutress.KaimuxAdminStats.servicesimpl.discordTickets.DiscordTicketsReactionsServiceImpl;

import jakarta.transaction.Transactional;

@Configuration
@EnableScheduling
public class ScheduledTasksConfig {

  private final DiscordTicketsReactionsService discordTicketsReactionsService;
  private final MinecraftTicketsAnswersService minecraftTicketsAnswersService;
  private final ProjectVisitorsRawDataService projectVisitorsRawDataService;

  public ScheduledTasksConfig(
      DiscordTicketsReactionsServiceImpl discordTicketsReactionsService,
      MinecraftTicketsAnswersService minecraftTicketsAnswersService,
      ProjectVisitorsRawDataService projectVisitorsRawDataService) {
    this.discordTicketsReactionsService = discordTicketsReactionsService;
    this.minecraftTicketsAnswersService = minecraftTicketsAnswersService;
    this.projectVisitorsRawDataService = projectVisitorsRawDataService;
  }

  @Scheduled(initialDelay = 0, fixedRate = 86400000)
  @Transactional
  public void runDataExtractionFromAPI() {
    System.out.println("Scheduled tasks started at: " + getCurrentTimestamp());
    System.out.println("");

    if (shouldTerminateLongTasks) {
      runWithTimeout(() -> discordTicketsReactionsService.fetchAndSaveData(), "Discord API Call", 10);
      runWithTimeout(() -> minecraftTicketsAnswersService.fetchAndSaveData(), "Minecraft API Call", 10);
      runWithTimeout(() -> projectVisitorsRawDataService.fetchAndSaveData(), "Visitors API Call", 10);
    } else {
      measureExecutionTime(() -> discordTicketsReactionsService.fetchAndSaveData(), "Discord API Call");
      measureExecutionTime(() -> minecraftTicketsAnswersService.fetchAndSaveData(), "Minecraft API Call");
      measureExecutionTime(() -> projectVisitorsRawDataService.fetchAndSaveData(), "Visitors API Call");
    }

    System.out.println("Scheduled tasks completed at: " + getCurrentTimestamp());
    System.out.println("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");
  }

  private final boolean shouldTerminateLongTasks = true; // true - on, false - off

  private void runWithTimeout(Runnable task, String taskName, int timeoutMinutes) {
    ExecutorService executor = Executors.newSingleThreadExecutor();
    Future<?> future = executor.submit(() -> measureExecutionTime(task, taskName));

    try {
      future.get(timeoutMinutes, TimeUnit.MINUTES);
    } catch (TimeoutException e) {
      System.err.println(taskName + " exceeded " + timeoutMinutes + " minutes and was terminated.");
      future.cancel(true);
    } catch (InterruptedException e) {
      System.err.println(taskName + " was interrupted.");
      Thread.currentThread().interrupt();
    } catch (ExecutionException e) {
      System.err.println("Error during " + taskName + ": " + e.getCause().getMessage());
    } finally {
      executor.shutdown();
    }
  }

  private void measureExecutionTime(Runnable task, String taskName) {
    Instant start = Instant.now();
    System.out.println("");
    System.out.println("Starting " + taskName);
    task.run();
    Instant end = Instant.now();
    Duration duration = Duration.between(start, end);
    System.out.println(taskName + " >> " + duration.toMillis() + " ms");
  }

  private String getCurrentTimestamp() {
    return LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
  }
}