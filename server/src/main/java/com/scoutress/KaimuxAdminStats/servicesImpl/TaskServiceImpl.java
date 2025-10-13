package com.scoutress.KaimuxAdminStats.servicesImpl;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.scoutress.KaimuxAdminStats.services.TaskService;
import com.scoutress.KaimuxAdminStats.servicesImpl.complaints.ComplaintsServiceImpl;
import com.scoutress.KaimuxAdminStats.servicesImpl.discordMessages.DiscordMessagesHandlingServiceImpl;
import com.scoutress.KaimuxAdminStats.servicesImpl.minecraftTickets.MinecraftTicketsRawServiceImpl;
import com.scoutress.KaimuxAdminStats.servicesImpl.playtime.PlaytimeHandlingServiceImpl;
import com.scoutress.KaimuxAdminStats.servicesImpl.productivity.ProductivityServiceImpl;

import jakarta.annotation.PostConstruct;

@Service
public class TaskServiceImpl implements TaskService {

  private static final Logger log = LoggerFactory.getLogger(TaskServiceImpl.class);

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
  public void processCalculations() {
    log.info("-------------------------------------------------");
    log.info("🧮 Starting full calculations at: {}", LocalDateTime.now());

    Instant start = Instant.now();

    try {
      executeStep("Checking employee data", () -> {
        List<Short> missing = employeeDataServiceImpl.checkNessesaryEmployeeData();
        log.info("Employees missing data: {}", missing);
        employeeDataServiceImpl.removeNotEmployeesData();
      });

      executeStep("Handling Discord messages", discordMessagesHandlingServiceImpl::handleDiscordMessages);
      executeStep("Handling Playtime", playtimeHandlingServiceImpl::handlePlaytime);
      executeStep("Handling Minecraft tickets", minecraftTicketsRawServiceImpl::handleMinecraftTickets);
      executeStep("Handling Complaints", complaintsServiceImpl::handleComplaints);
      executeStep("Handling Productivity", productivityServiceImpl::handleProductivity);
      executeStep("Handling Recommendations", recommendationsServiceImpl::handleRecommendations);
      executeStep("Handling Final Stats", finalStatsServiceImpl::handleFinalStats);
      executeStep("Handling User Recommendations", recommendationUserServiceImpl::handleUserRecommendations);
      executeStep("Handling Latest Activity", latestActivityServiceImpl::calculateLatestActivity);

    } catch (Exception e) {
      log.error("🚨 Unexpected error in processCalculations: {}", e.getMessage(), e);
    }

    Duration total = Duration.between(start, Instant.now());
    log.info("✅ All calculations completed at: {} (took {} seconds)",
        LocalDateTime.now(), total.toSeconds());
    log.info("-------------------------------------------------");
  }

  @Transactional
  private void executeStep(String name, Runnable task) {
    log.info("➡️ {}", name);
    Instant stepStart = Instant.now();

    try {
      task.run();
      long seconds = Duration.between(stepStart, Instant.now()).toSeconds();
      log.info("✅ {} completed in {}s", name, seconds);
    } catch (Exception e) {
      log.error("❌ Error during {}: {}", name, e.getMessage(), e);
    }
  }
}
