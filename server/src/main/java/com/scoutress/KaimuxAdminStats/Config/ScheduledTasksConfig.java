package com.scoutress.KaimuxAdminStats.config;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

import com.scoutress.KaimuxAdminStats.services.DataFetchingService;
import com.scoutress.KaimuxAdminStats.services.FinalStatsService;
import com.scoutress.KaimuxAdminStats.services.RecommendationsService;
import com.scoutress.KaimuxAdminStats.services.SQLiteToMySQLService;
import com.scoutress.KaimuxAdminStats.services.complaints.ComplaintsService;
import com.scoutress.KaimuxAdminStats.services.discordMessages.DiscordMessagesComparedService;
import com.scoutress.KaimuxAdminStats.services.discordMessages.DiscordMessagesService;
import com.scoutress.KaimuxAdminStats.services.discordTickets.DiscordTicketsService;
import com.scoutress.KaimuxAdminStats.services.employees.EmployeeDataService;
import com.scoutress.KaimuxAdminStats.services.minecraftTickets.MinecraftTicketsComparedService;
import com.scoutress.KaimuxAdminStats.services.minecraftTickets.MinecraftTicketsService;
import com.scoutress.KaimuxAdminStats.services.playtime.AnnualyPlaytimeService;
import com.scoutress.KaimuxAdminStats.services.playtime.AveragePlaytimeOverallService;
import com.scoutress.KaimuxAdminStats.services.playtime.DailyPlaytimeService;
import com.scoutress.KaimuxAdminStats.services.playtime.SessionDurationService;
import com.scoutress.KaimuxAdminStats.services.productivity.ProductivityService;

import jakarta.transaction.Transactional;

@Configuration
@EnableScheduling
public class ScheduledTasksConfig {

  private final DataFetchingService dataFetchingService;
  private final DiscordTicketsService discordTicketsService;
  private final ProductivityService productivityService;
  private final SQLiteToMySQLService sQLiteToMySQLService;
  private final EmployeeDataService employeeDataService;
  private final SessionDurationService sessionDurationService;
  private final DailyPlaytimeService dailyPlaytimeService;
  private final AnnualyPlaytimeService annualyPlaytimeService;
  private final AveragePlaytimeOverallService averagePlaytimeOverallService;
  private final MinecraftTicketsService minecraftTicketsService;
  private final MinecraftTicketsComparedService minecraftTicketsComparedService;
  private final DiscordMessagesService discordMessagesService;
  private final DiscordMessagesComparedService discordMessagesComparedService;
  private final ComplaintsService complaintsService;
  private final RecommendationsService recommendationsService;
  private final FinalStatsService finalStatsService;

  public ScheduledTasksConfig(
      DataFetchingService dataFetchingService,
      DiscordTicketsService discordTicketsService,
      ProductivityService productivityService,
      SQLiteToMySQLService sQLiteToMySQLService,
      EmployeeDataService employeeDataService,
      SessionDurationService sessionDurationService,
      DailyPlaytimeService dailyPlaytimeService,
      AnnualyPlaytimeService annualyPlaytimeService,
      AveragePlaytimeOverallService averagePlaytimeOverallService,
      MinecraftTicketsService minecraftTicketsService,
      MinecraftTicketsComparedService minecraftTicketsComparedService,
      DiscordMessagesService discordMessagesService,
      DiscordMessagesComparedService discordMessagesComparedService,
      ComplaintsService complaintsService,
      RecommendationsService recommendationsService,
      FinalStatsService finalStatsService) {
    this.dataFetchingService = dataFetchingService;
    this.discordTicketsService = discordTicketsService;
    this.productivityService = productivityService;
    this.sQLiteToMySQLService = sQLiteToMySQLService;
    this.employeeDataService = employeeDataService;
    this.sessionDurationService = sessionDurationService;
    this.dailyPlaytimeService = dailyPlaytimeService;
    this.annualyPlaytimeService = annualyPlaytimeService;
    this.averagePlaytimeOverallService = averagePlaytimeOverallService;
    this.minecraftTicketsService = minecraftTicketsService;
    this.minecraftTicketsComparedService = minecraftTicketsComparedService;
    this.discordMessagesService = discordMessagesService;
    this.discordMessagesComparedService = discordMessagesComparedService;
    this.complaintsService = complaintsService;
    this.recommendationsService = recommendationsService;
    this.finalStatsService = finalStatsService;
  }

  @Scheduled(/* initialDelay = 1000, */ fixedRate = 86400000)
  @Transactional
  public void runScheduledTasks() {
    System.out.println("-----------------------------------------------");
    System.out.println("Started scheduled tasks at: " + getCurrentTimestamp());
    System.out.println("");

    System.out.println("Annual playtime calculations");
    sQLiteToMySQLService.initializeUsersDatabase();
    sQLiteToMySQLService.initializePlaytimeSessionsDatabase();
    employeeDataService.updateEmployeeCodes();
    sessionDurationService.processSessions();
    sessionDurationService.removeDuplicateSessionData();
    dailyPlaytimeService.handleDailyPlaytime();
    annualyPlaytimeService.handleAnnualPlaytime();

    System.out.println("");
    System.out.println("Average playtime per day calculations");
    averagePlaytimeOverallService.handleAveragePlaytime();

    System.out.println("");
    System.out.println("Average Minecraft tickets per day calculations");
    minecraftTicketsService.convertMinecraftTicketsAnswers();
    minecraftTicketsService.calculateAverageDailyMinecraftTicketsValues();

    System.out.println("");
    System.out.println("Average Minecraft tickets per playtime hour calculations");
    minecraftTicketsService.calculateAverageMinecraftTicketsPerPlaytime();

    System.out.println("");
    System.out.println("Average Minecraft tickets taking comparison per day calculations");
    minecraftTicketsComparedService.compareEachEmployeeDailyMcTicketsValues();

    System.out.println("");
    System.out.println("Average discord messages per day calculations");
    discordMessagesService.calculateAverageValueOfDailyDiscordMessages();

    System.out.println("");
    System.out.println("Average discord messages taking comparison per day calculation");
    discordMessagesComparedService.compareEachEmployeeDailyDiscordMessagesValues();

    System.out.println("");
    System.out.println("Complaints calculation");
    complaintsService.calculateComplaintsPerEachEmployee();

    System.out.println("");
    System.out.println("Productivity calculation");
    productivityService.calculateProductivity();

    System.out.println("");
    System.out.println("Recommendation evaluation");
    recommendationsService.evaluateRecommendations();

    System.out.println("");
    System.out.println("Final stats updating");
    finalStatsService.updateNewStatsData();

    System.out.println("");
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

  private String getCurrentTimestamp() {
    return LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
  }
}
