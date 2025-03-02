package com.scoutress.KaimuxAdminStats.servicesImpl;

import org.springframework.stereotype.Service;

import com.scoutress.KaimuxAdminStats.services.FinalStatsService;
import com.scoutress.KaimuxAdminStats.services.RecommendationUserService;
import com.scoutress.KaimuxAdminStats.services.RecommendationsService;
import com.scoutress.KaimuxAdminStats.services.TaskService;
import com.scoutress.KaimuxAdminStats.services.complaints.ComplaintsService;
import com.scoutress.KaimuxAdminStats.services.discordMessages.DiscordMessagesHandlingService;
import com.scoutress.KaimuxAdminStats.services.minecraftTickets.MinecraftTicketsHandlingService;
import com.scoutress.KaimuxAdminStats.services.playtime.PlaytimeHandlingService;
import com.scoutress.KaimuxAdminStats.services.productivity.ProductivityService;

import jakarta.annotation.PostConstruct;
import jakarta.transaction.Transactional;

@Service
public class TaskServiceImpl implements TaskService {

  private final DiscordMessagesHandlingService discordMessagesHandlingService;
  private final ProductivityService productivityService;
  private final ComplaintsService complaintsService;
  private final RecommendationsService recommendationsService;
  private final FinalStatsService finalStatsService;
  private final RecommendationUserService recommendationUserService;
  private final MinecraftTicketsHandlingService minecraftTicketsHandlingService;
  private final PlaytimeHandlingService playtimeHandlingService;

  public TaskServiceImpl(
      DiscordMessagesHandlingService discordMessagesHandlingService,
      ProductivityService productivityService,
      ComplaintsService complaintsService,
      RecommendationsService recommendationsService,
      FinalStatsService finalStatsService,
      RecommendationUserService recommendationUserService,
      MinecraftTicketsHandlingService minecraftTicketsHandlingService,
      PlaytimeHandlingService playtimeHandlingService) {
    this.discordMessagesHandlingService = discordMessagesHandlingService;
    this.productivityService = productivityService;
    this.complaintsService = complaintsService;
    this.recommendationsService = recommendationsService;
    this.finalStatsService = finalStatsService;
    this.recommendationUserService = recommendationUserService;
    this.minecraftTicketsHandlingService = minecraftTicketsHandlingService;
    this.playtimeHandlingService = playtimeHandlingService;
  }

  @Override
  @PostConstruct
  @Transactional
  public void runScheduledTasks() {
    System.out.println("-----------------------------------------------");
    System.out.println("Started scheduled tasks at: " + getCurrentTimestamp());
    System.out.println("");

    System.out.println("Handling Discord messages");
    discordMessagesHandlingService.handleDiscordMessages();

    System.out.println("Handling Minecraft tickets");
    minecraftTicketsHandlingService.handleMinecraftTickets();

    System.out.println("Handling Playtime");
    playtimeHandlingService.handlePlaytime();

    System.out.println("Handling Complaints");
    complaintsService.handleComplaints();

    System.out.println("Handling productivity");
    productivityService.handleProductivity();

    System.out.println("Handling recommendation");
    recommendationsService.handleRecommendations();

    System.out.println("Handling final stats");
    finalStatsService.handleFinalStats();

    System.out.println("Handling user recommendation");
    recommendationUserService.handleUserRecommendations();

    System.out.println("");
    System.out.println("Scheduled tasks completed at: " + getCurrentTimestamp());
    System.out.println("-----------------------------------------------");
  }

  @Override
  @Transactional
  public void runBackupDataUploadingTasks() {
    // System.out.println("-----------------------------------------------");
    // System.out.println("Started scheduled tasks at: " + getCurrentTimestamp());
    // System.out.println("");

    // System.out.println("Annual playtime calculations");
    // sessionDurationService.processSessionsFromBackup();
    // dailyPlaytimeService.handleDailyPlaytime();
    // annualyPlaytimeService.handleAnnualPlaytime();

    // System.out.println("");
    // System.out.println("Average playtime per day calculations");
    // averagePlaytimeOverallService.handleAveragePlaytime();

    // System.out.println("");
    // System.out.println("Average Minecraft tickets per day calculations");
    // minecraftTicketsService.calculateAverageDailyMinecraftTicketsValues();

    // System.out.println("");
    // System.out.println("Average Minecraft tickets per playtime hour
    // calculations");
    // minecraftTicketsService.calculateAverageMinecraftTicketsPerPlaytime();

    // System.out.println("");
    // System.out.println("Average Minecraft tickets taking comparison per day
    // calculations");
    // minecraftTicketsComparedService.compareEachEmployeeDailyMcTicketsValues();

    // System.out.println("");
    // System.out.println("Total Minecraft tickets updating");
    // minecraftTicketsService.calculateTotalMinecraftTickets();

    // System.out.println("");
    // System.out.println("Complaints calculation");
    // complaintsService.calculateComplaintsPerEachEmployee();

    // System.out.println("");
    // System.out.println("Productivity calculation");
    // productivityService.calculateProductivity();

    // System.out.println("");
    // System.out.println("Recommendation evaluation");
    // recommendationsService.evaluateRecommendations();

    // System.out.println("");
    // System.out.println("Final stats updating");
    // finalStatsService.updateNewStatsData();

    // System.out.println("");
    // System.out.println("User recommendation updating");
    // recommendationUserService.checkAndSaveRecommendations();

    // System.out.println("");
    // System.out.println("Scheduled tasks completed at: " + getCurrentTimestamp());
    // System.out.println("-----------------------------------------------");
  }

  private String getCurrentTimestamp() {
    return java.time.LocalDateTime.now().toString();
  }
}
