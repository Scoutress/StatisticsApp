package com.scoutress.KaimuxAdminStats.servicesImpl;

import java.util.List;

import org.springframework.stereotype.Service;

import com.scoutress.KaimuxAdminStats.services.TaskService;
import com.scoutress.KaimuxAdminStats.servicesImpl.complaints.ComplaintsServiceImpl;
import com.scoutress.KaimuxAdminStats.servicesImpl.discordMessages.DiscordMessagesHandlingServiceImpl;
import com.scoutress.KaimuxAdminStats.servicesImpl.minecraftTickets.MinecraftTicketsRawServiceImpl;
import com.scoutress.KaimuxAdminStats.servicesImpl.playtime.PlaytimeHandlingServiceImpl;
import com.scoutress.KaimuxAdminStats.servicesImpl.productivity.ProductivityServiceImpl;

import jakarta.annotation.PostConstruct;
import jakarta.transaction.Transactional;

@Service
public class TaskServiceImpl implements TaskService {

  private final EmployeeDataServiceImpl employeeDataServiceImpl;
  private final DiscordMessagesHandlingServiceImpl discordMessagesHandlingServiceImpl;
  private final MinecraftTicketsRawServiceImpl minecraftTicketsRawServiceImpl;
  private final ProductivityServiceImpl productivityServiceImpl;
  private final ComplaintsServiceImpl complaintsServiceImpl;
  private final RecommendationsServiceImpl recommendationsServiceImpl;
  private final FinalStatsServiceImpl finalStatsServiceImpl;
  private final RecommendationUserServiceImpl recommendationUserServiceImpl;
  private final PlaytimeHandlingServiceImpl playtimeHandlingServiceImpl;
  private final LatestActivityServiceImpl latestActivityServiceImpl;

  public TaskServiceImpl(
      EmployeeDataServiceImpl employeeDataServiceImpl,
      DiscordMessagesHandlingServiceImpl discordMessagesHandlingServiceImpl,
      MinecraftTicketsRawServiceImpl minecraftTicketsRawServiceImpl,
      ProductivityServiceImpl productivityServiceImpl,
      ComplaintsServiceImpl complaintsServiceImpl,
      RecommendationsServiceImpl recommendationsServiceImpl,
      FinalStatsServiceImpl finalStatsServiceImpl,
      RecommendationUserServiceImpl recommendationUserServiceImpl,
      PlaytimeHandlingServiceImpl playtimeHandlingServiceImpl,
      LatestActivityServiceImpl latestActivityServiceImpl) {
    this.employeeDataServiceImpl = employeeDataServiceImpl;
    this.discordMessagesHandlingServiceImpl = discordMessagesHandlingServiceImpl;
    this.minecraftTicketsRawServiceImpl = minecraftTicketsRawServiceImpl;
    this.productivityServiceImpl = productivityServiceImpl;
    this.complaintsServiceImpl = complaintsServiceImpl;
    this.recommendationsServiceImpl = recommendationsServiceImpl;
    this.finalStatsServiceImpl = finalStatsServiceImpl;
    this.recommendationUserServiceImpl = recommendationUserServiceImpl;
    this.playtimeHandlingServiceImpl = playtimeHandlingServiceImpl;
    this.latestActivityServiceImpl = latestActivityServiceImpl;
  }

  @Override
  @PostConstruct
  @Transactional
  public void processCalculations() {
    System.out.println("-----------------------------------------------");
    System.out.println("Started calculations at: " + getCurrentTimestamp());
    System.out.println("");

    try {
      System.out.println("Checking necessary employee data");
      List<Short> employeeIdsWithoutData = employeeDataServiceImpl.checkNessesaryEmployeeData();
      System.out.println("Employee IDs without data: " + employeeIdsWithoutData);

      System.out.println("Handling Discord messages");
      discordMessagesHandlingServiceImpl.handleDiscordMessages();

      System.out.println("Handling Playtime");
      playtimeHandlingServiceImpl.handlePlaytime();

      System.out.println("Handling Minecraft tickets");
      minecraftTicketsRawServiceImpl.handleMinecraftTickets();

      System.out.println("Handling Complaints");
      complaintsServiceImpl.handleComplaints();

      System.out.println("Handling productivity");
      productivityServiceImpl.handleProductivity();

      System.out.println("Handling recommendation");
      recommendationsServiceImpl.handleRecommendations();

      System.out.println("Handling final stats");
      finalStatsServiceImpl.handleFinalStats();

      System.out.println("Handling user recommendation");
      recommendationUserServiceImpl.handleUserRecommendations();

      System.out.println("Handling latest activity");
      latestActivityServiceImpl.calculateLatestActivity();

    } catch (Exception e) {
      System.out.println("ALERT: Error during processCalculations at startup: " + e.getMessage());
      e.printStackTrace(System.out);
    }

    System.out.println("");
    System.out.println("Calculations completed at: " + getCurrentTimestamp());
    System.out.println("-----------------------------------------------");
  }

  private String getCurrentTimestamp() {
    return java.time.LocalDateTime.now().toString();
  }
}
