package com.scoutress.KaimuxAdminStats.config;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

import com.scoutress.KaimuxAdminStats.services.DataFetchingService;
import com.scoutress.KaimuxAdminStats.services.discordTickets.DiscordTicketsService;
import com.scoutress.KaimuxAdminStats.services.productivity.ProductivityService;

import jakarta.transaction.Transactional;

@Configuration
@EnableScheduling
public class ScheduledTasksConfig {

  private final DataFetchingService dataFetchingService;
  private final DiscordTicketsService discordTicketsService;
  private final ProductivityService productivityService;

  public ScheduledTasksConfig(
      DataFetchingService dataFetchingService,
      DiscordTicketsService discordTicketsService,
      ProductivityService productivityService) {
    this.dataFetchingService = dataFetchingService;
    this.discordTicketsService = discordTicketsService;
    this.productivityService = productivityService;
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

    // runDailyObjectiveProductivityCalculations();
    // runDailyProductivityCalculations();

    System.out.println("Scheduled tasks completed at: " + getCurrentTimestamp());
    System.out.println("-----------------------------------------------");
  }

  @SuppressWarnings("unused")
  private void runDiscordDataExtractionFromAPI() {
    System.out.println("Running: runDiscordDataExtractionFromAPI");
    dataFetchingService.fetchAndSaveData(1, "discord");
    System.out.println("Completed: runDiscordDataExtractionFromAPI");
    System.out.println("");
  }

  @SuppressWarnings("unused")
  private void runMinecraftDataExtractionFromAPI() {
    System.out.println("Running: runMinecraftDataExtractionFromAPI");
    dataFetchingService.fetchAndSaveData(1, "minecraft");
    System.out.println("Completed: runMinecraftDataExtractionFromAPI");
    System.out.println("");
  }

  @SuppressWarnings("unused")
  private void runVisitorsDataExtractionFromAPI() {
    System.out.println("Running: runVisitorsDataExtractionFromAPI");
    dataFetchingService.fetchAndSaveData(1, "visitors");
    System.out.println("Completed: runVisitorsDataExtractionFromAPI");
    System.out.println("");
  }

  @SuppressWarnings("unused")
  private void runDiscordTicketsRawDataDuplicateRemover() {
    System.out.println("Running: runDiscordTicketsRawDataDuplicateRemover");
    discordTicketsService.removeDuplicateReactions();
    System.out.println("Completed: runDiscordTicketsRawDataDuplicateRemover");
    System.out.println("");
  }

  @SuppressWarnings("unused")
  private void runDiscordTicketsDataConvertor() {
    System.out.println("Running: runDiscordTicketsDataConvertor");
    discordTicketsService.convertDiscordTicketsResponses();
    System.out.println("Completed: runDiscordTicketsDataConvertor");
    System.out.println("");
  }

  @SuppressWarnings("unused")
  private void runDiscordTicketsConvertedDataDuplicateRemover() {
    System.out.println("Running: runDiscordTicketsConvertedDataDuplicateRemover");
    discordTicketsService.removeDuplicateTicketsData();
    System.out.println("Completed: runDiscordTicketsConvertedDataDuplicateRemover");
    System.out.println("");
  }

  @SuppressWarnings("unused")
  private void runDailyObjectiveProductivityCalculations() {
    System.out.println("Running: runDailyObjectiveProductivityCalculations");
    productivityService.calculateDailyObjectiveProductivity();
    System.out.println("Completed: runDailyObjectiveProductivityCalculations");
    System.out.println("");
  }

  @SuppressWarnings("unused")
  private void runDailyProductivityCalculations() {
    System.out.println("Running: runProductivityCalculations");
    productivityService.calculateDailyProductivity();
    System.out.println("Completed: runProductivityCalculations");
    System.out.println("");
  }

  private String getCurrentTimestamp() {
    return LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
  }
}
