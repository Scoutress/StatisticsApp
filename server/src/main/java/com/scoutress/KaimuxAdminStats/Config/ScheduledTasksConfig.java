package com.scoutress.KaimuxAdminStats.config;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

import com.scoutress.KaimuxAdminStats.services.DataFetchingService;
import com.scoutress.KaimuxAdminStats.services.discordTickets.DiscordTicketsService;

import jakarta.transaction.Transactional;

@Configuration
@EnableScheduling
public class ScheduledTasksConfig {

  private final DataFetchingService dataFetchingService;
  private final DiscordTicketsService discordTicketsService;

  public ScheduledTasksConfig(
      DataFetchingService dataFetchingService,
      DiscordTicketsService discordTicketsService) {
    this.dataFetchingService = dataFetchingService;
    this.discordTicketsService = discordTicketsService;
  }

  @Scheduled(initialDelay = 1000, fixedRate = 86400000)
  @Transactional
  public void runScheduledTasks() {
    System.out.println("-----------------------------------------------");
    System.out.println("Started scheduled tasks at: " + getCurrentTimestamp());
    System.out.println("");

    // runDiscordDataExtractionFromAPI();
    // runMinecraftDataExtractionFromAPI();
    // runVisitorsDataExtractionFromAPI();

    // runDiscordTicketsRawDataDuplicateRemover();

    // runDiscordTicketsDataConvertor();

    // runDiscordTicketsConvertedDataDuplicateRemover();

    System.out.println("Scheduled tasks completed at: " + getCurrentTimestamp());
    System.out.println("-----------------------------------------------");
  }

  private void runDiscordDataExtractionFromAPI() {
    System.out.println("Running: runDiscordDataExtractionFromAPI");
    dataFetchingService.fetchAndSaveData(1, "discord");
    System.out.println("Completed: runDiscordDataExtractionFromAPI");
    System.out.println("");
  }

  private void runMinecraftDataExtractionFromAPI() {
    System.out.println("Running: runMinecraftDataExtractionFromAPI");
    dataFetchingService.fetchAndSaveData(1, "minecraft");
    System.out.println("Completed: runMinecraftDataExtractionFromAPI");
    System.out.println("");
  }

  private void runVisitorsDataExtractionFromAPI() {
    System.out.println("Running: runVisitorsDataExtractionFromAPI");
    dataFetchingService.fetchAndSaveData(1, "visitors");
    System.out.println("Completed: runVisitorsDataExtractionFromAPI");
    System.out.println("");
  }

  private void runDiscordTicketsRawDataDuplicateRemover() {
    System.out.println("Running: runDuplicateRemover");
    discordTicketsService.removeDuplicateReactions();
    System.out.println("Completed: runDuplicateRemover");
    System.out.println("");
  }

  private void runDiscordTicketsDataConvertor() {
    System.out.println("Running: runDuplicateRemover");
    discordTicketsService.convertDiscordTicketsResponses();
    System.out.println("Completed: runDuplicateRemover");
    System.out.println("");
  }

  private void runDiscordTicketsConvertedDataDuplicateRemover() {
    System.out.println("Running: runDuplicateRemover");
    discordTicketsService.removeDuplicateTicketsData();
    System.out.println("Completed: runDuplicateRemover");
    System.out.println("");
  }

  private String getCurrentTimestamp() {
    return LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
  }
}
