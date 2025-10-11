package com.scoutress.KaimuxAdminStats.servicesImpl;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import com.scoutress.KaimuxAdminStats.entity.employees.Employee;
import com.scoutress.KaimuxAdminStats.entity.employees.EmployeeCodes;
import com.scoutress.KaimuxAdminStats.repositories.FinalStatsRepository;
import com.scoutress.KaimuxAdminStats.repositories.LatestActivityRepository;
import com.scoutress.KaimuxAdminStats.repositories.RecommendationsRepository;
import com.scoutress.KaimuxAdminStats.repositories.complaints.ComplaintsRepository;
import com.scoutress.KaimuxAdminStats.repositories.complaints.ComplaintsSumRepository;
import com.scoutress.KaimuxAdminStats.repositories.discordMessages.AverageDailyDiscordMessagesRepository;
import com.scoutress.KaimuxAdminStats.repositories.discordMessages.AverageDiscordMessagesComparedRepository;
import com.scoutress.KaimuxAdminStats.repositories.discordMessages.DailyDiscordMessagesComparedRepository;
import com.scoutress.KaimuxAdminStats.repositories.discordMessages.DailyDiscordMessagesRepository;
import com.scoutress.KaimuxAdminStats.repositories.discordTickets.DailyDiscordTicketsRepository;
import com.scoutress.KaimuxAdminStats.repositories.employees.EmployeeCodesRepository;
import com.scoutress.KaimuxAdminStats.repositories.employees.EmployeeLevelChangeRepository;
import com.scoutress.KaimuxAdminStats.repositories.employees.EmployeeRepository;
import com.scoutress.KaimuxAdminStats.repositories.minecraftTickets.AverageDailyMinecraftTicketsRepository;
import com.scoutress.KaimuxAdminStats.repositories.minecraftTickets.AverageMinecraftTicketsComparedRepository;
import com.scoutress.KaimuxAdminStats.repositories.minecraftTickets.AverageMinecraftTicketsPerPlaytimeRepository;
import com.scoutress.KaimuxAdminStats.repositories.minecraftTickets.DailyMinecraftTicketsComparedRepository;
import com.scoutress.KaimuxAdminStats.repositories.minecraftTickets.DailyMinecraftTicketsRepository;
import com.scoutress.KaimuxAdminStats.repositories.minecraftTickets.McTicketsLastCheckRepository;
import com.scoutress.KaimuxAdminStats.repositories.minecraftTickets.TotalMinecraftTicketsRepository;
import com.scoutress.KaimuxAdminStats.repositories.minecraftTickets.TotalOldMinecraftTicketsRepository;
import com.scoutress.KaimuxAdminStats.repositories.playtime.AnnualPlaytimeRepository;
import com.scoutress.KaimuxAdminStats.repositories.playtime.AveragePlaytimeOverallRepository;
import com.scoutress.KaimuxAdminStats.repositories.playtime.DailyPlaytimeRepository;
import com.scoutress.KaimuxAdminStats.repositories.playtime.LoginLogoutTimesRepository;
import com.scoutress.KaimuxAdminStats.repositories.playtime.PlaytimeSessionsRepository;
import com.scoutress.KaimuxAdminStats.repositories.playtime.SanitazedDataRepository;
import com.scoutress.KaimuxAdminStats.repositories.playtime.SegmentCountAllServersRepository;
import com.scoutress.KaimuxAdminStats.repositories.playtime.SegmentCountByServerRepository;
import com.scoutress.KaimuxAdminStats.repositories.playtime.SessionDurationRepository;
import com.scoutress.KaimuxAdminStats.repositories.playtime.TimeOfDaySegmentsRepository;
import com.scoutress.KaimuxAdminStats.repositories.productivity.DailyProductivityRepository;
import com.scoutress.KaimuxAdminStats.repositories.productivity.ProductivityRepository;
import com.scoutress.KaimuxAdminStats.services.EmployeeDataService;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;

@Service
public class EmployeeDataServiceImpl implements EmployeeDataService {

  private final EmployeeRepository employeeRepository;
  private final EmployeeCodesRepository employeeCodesRepository;
  private final AnnualPlaytimeRepository annualPlaytimeRepository;
  private final ComplaintsRepository complaintsRepository;
  private final ComplaintsSumRepository complaintsSumRepository;
  private final ProductivityRepository productivityRepository;
  private final SanitazedDataRepository sanitazedDataRepository;
  private final PlaytimeSessionsRepository playtimeSessionsRepository;
  private final AveragePlaytimeOverallRepository averagePlaytimeOverallRepository;
  private final DailyMinecraftTicketsComparedRepository dailyMinecraftTicketsComparedRepository;
  private final AverageMinecraftTicketsPerPlaytimeRepository averageMinecraftTicketsPerPlaytimeRepository;
  private final AverageDiscordMessagesComparedRepository averageDiscordMessagesComparedRepository;
  private final AverageDailyDiscordMessagesRepository averageDailyDiscordMessagesRepository;
  private final DailyDiscordMessagesComparedRepository dailyDiscordMessagesComparedRepository;
  private final DailyDiscordMessagesRepository dailyDiscordMessagesRepository;
  private final DailyMinecraftTicketsRepository dailyMinecraftTicketsRepository;
  private final DailyProductivityRepository dailyProductivityRepository;
  private final DailyDiscordTicketsRepository dailyDiscordTicketsRepository;
  private final EmployeeLevelChangeRepository employeeLevelChangeRepository;
  private final AverageDailyMinecraftTicketsRepository averageDailyMinecraftTicketsRepository;
  private final McTicketsLastCheckRepository mcTicketsLastCheckRepository;
  private final TotalMinecraftTicketsRepository totalMinecraftTicketsRepository;
  private final TotalOldMinecraftTicketsRepository totalOldMinecraftTicketsRepository;
  private final AverageMinecraftTicketsComparedRepository averageMinecraftTicketsComparedRepository;
  private final LoginLogoutTimesRepository loginLogoutTimesRepository;
  private final SegmentCountAllServersRepository segmentCountAllServersRepository;
  private final SegmentCountByServerRepository segmentCountByServerRepository;
  private final SessionDurationRepository sessionDurationRepository;
  private final TimeOfDaySegmentsRepository timeOfDaySegmentsRepository;
  private final FinalStatsRepository finalStatsRepository;
  private final LatestActivityRepository latestActivityRepository;
  private final RecommendationsRepository recommendationsRepository;
  private final DailyPlaytimeRepository dailyPlaytimeRepository;

  public EmployeeDataServiceImpl(
      EmployeeRepository employeeRepository,
      EmployeeCodesRepository employeeCodesRepository,
      AnnualPlaytimeRepository annualPlaytimeRepository,
      ComplaintsRepository complaintsRepository,
      ComplaintsSumRepository complaintsSumRepository,
      AverageDailyDiscordMessagesRepository averageDailyDiscordMessagesRepository,
      AverageDiscordMessagesComparedRepository averageDiscordMessagesComparedRepository,
      AverageMinecraftTicketsPerPlaytimeRepository averageMinecraftTicketsPerPlaytimeRepository,
      DailyMinecraftTicketsComparedRepository dailyMinecraftTicketsComparedRepository,
      AveragePlaytimeOverallRepository averagePlaytimeOverallRepository,
      PlaytimeSessionsRepository playtimeSessionsRepository,
      SanitazedDataRepository sanitazedDataRepository,
      ProductivityRepository productivityRepository,
      DailyDiscordMessagesComparedRepository dailyDiscordMessagesComparedRepository,
      DailyDiscordMessagesRepository dailyDiscordMessagesRepository,
      DailyMinecraftTicketsRepository dailyMinecraftTicketsRepository,
      DailyDiscordTicketsRepository dailyDiscordTicketsRepository,
      DailyProductivityRepository dailyProductivityRepository,
      EmployeeLevelChangeRepository employeeLevelChangeRepository,
      AverageDailyMinecraftTicketsRepository averageDailyMinecraftTicketsRepository,
      McTicketsLastCheckRepository mcTicketsLastCheckRepository,
      TotalMinecraftTicketsRepository totalMinecraftTicketsRepository,
      TotalOldMinecraftTicketsRepository totalOldMinecraftTicketsRepository,
      FinalStatsRepository finalStatsRepository,
      LatestActivityRepository latestActivityRepository,
      LoginLogoutTimesRepository loginLogoutTimesRepository,
      PlaytimeSessionsRepository playtimeSessionsProcessedRepository,
      RecommendationsRepository recommendationsRepository,
      SegmentCountAllServersRepository segmentCountAllServersRepository,
      SegmentCountByServerRepository segmentCountByServerRepository,
      SessionDurationRepository sessionDurationRepository,
      TimeOfDaySegmentsRepository timeOfDaySegmentsRepository,
      AverageMinecraftTicketsComparedRepository averageMinecraftTicketsComparedRepository,
      DailyPlaytimeRepository dailyPlaytimeRepository) {
    this.employeeRepository = employeeRepository;
    this.employeeCodesRepository = employeeCodesRepository;
    this.annualPlaytimeRepository = annualPlaytimeRepository;
    this.complaintsRepository = complaintsRepository;
    this.complaintsSumRepository = complaintsSumRepository;
    this.averageDailyDiscordMessagesRepository = averageDailyDiscordMessagesRepository;
    this.averageDiscordMessagesComparedRepository = averageDiscordMessagesComparedRepository;
    this.averageMinecraftTicketsPerPlaytimeRepository = averageMinecraftTicketsPerPlaytimeRepository;
    this.dailyMinecraftTicketsComparedRepository = dailyMinecraftTicketsComparedRepository;
    this.averagePlaytimeOverallRepository = averagePlaytimeOverallRepository;
    this.playtimeSessionsRepository = playtimeSessionsRepository;
    this.sanitazedDataRepository = sanitazedDataRepository;
    this.productivityRepository = productivityRepository;
    this.dailyDiscordMessagesComparedRepository = dailyDiscordMessagesComparedRepository;
    this.dailyDiscordMessagesRepository = dailyDiscordMessagesRepository;
    this.dailyMinecraftTicketsRepository = dailyMinecraftTicketsRepository;
    this.dailyProductivityRepository = dailyProductivityRepository;
    this.dailyDiscordTicketsRepository = dailyDiscordTicketsRepository;
    this.employeeLevelChangeRepository = employeeLevelChangeRepository;
    this.averageDailyMinecraftTicketsRepository = averageDailyMinecraftTicketsRepository;
    this.mcTicketsLastCheckRepository = mcTicketsLastCheckRepository;
    this.totalMinecraftTicketsRepository = totalMinecraftTicketsRepository;
    this.totalOldMinecraftTicketsRepository = totalOldMinecraftTicketsRepository;
    this.averageMinecraftTicketsComparedRepository = averageMinecraftTicketsComparedRepository;
    this.loginLogoutTimesRepository = loginLogoutTimesRepository;
    this.segmentCountAllServersRepository = segmentCountAllServersRepository;
    this.segmentCountByServerRepository = segmentCountByServerRepository;
    this.sessionDurationRepository = sessionDurationRepository;
    this.timeOfDaySegmentsRepository = timeOfDaySegmentsRepository;
    this.finalStatsRepository = finalStatsRepository;
    this.latestActivityRepository = latestActivityRepository;
    this.recommendationsRepository = recommendationsRepository;
    this.dailyPlaytimeRepository = dailyPlaytimeRepository;
  }

  @Override
  public List<Short> checkNessesaryEmployeeData() {
    List<Short> employeeIds = getAllEmployeeIds();
    List<Short> employeeIdsWithoutData = new ArrayList<>();
    List<EmployeeCodes> employeeCodesData = getAllEmployeeCodesData();

    for (Short employeeId : employeeIds) {
      boolean employeeHasData = hasEmployeeData(employeeId, employeeCodesData);

      if (!employeeHasData) {
        employeeIdsWithoutData.add(employeeId);
      }
    }

    return employeeIdsWithoutData;
  }

  private List<Short> getAllEmployeeIds() {
    List<Employee> employeesData = getAllEmployeesData();
    return employeesData
        .stream()
        .map(Employee::getId)
        .distinct()
        .sorted()
        .toList();
  }

  private List<Employee> getAllEmployeesData() {
    return employeeRepository
        .findAll();
  }

  private List<EmployeeCodes> getAllEmployeeCodesData() {
    return employeeCodesRepository
        .findAll();
  }

  private boolean hasEmployeeData(Short employeeId, List<EmployeeCodes> employeeCodesData) {
    return employeeCodesData
        .stream()
        .filter(employee -> employee.getEmployeeId().equals(employeeId))
        .anyMatch(employee -> employee.getKmxWebApi() != null && employee.getDiscordUserId() != null);
  }

  @Override
  @Transactional
  public void removeNotEmployeesData() {
    List<Short> validEmployeeIds = getAllEmployeeIds();
    System.out.println("Valid employee IDs count: " + validEmployeeIds.size());

    System.out.println("Cleaning Complaints...");
    cleanInvalidData(
        complaintsRepository,
        validEmployeeIds,
        "complaints");

    System.out.println("Cleaning Complaints Sum...");
    cleanInvalidData(
        complaintsSumRepository,
        validEmployeeIds,
        "complaints_summary");

    System.out.println("Cleaning Avg Daily Dc Msgs...");
    cleanInvalidData(
        averageDailyDiscordMessagesRepository,
        validEmployeeIds,
        "average_daily_discord_messages");

    System.out.println("Cleaning Avg Dc Msgs Comp...");
    cleanInvalidData(
        averageDiscordMessagesComparedRepository,
        validEmployeeIds,
        "average_discord_messages_compared");

    System.out.println("Cleaning Daily Dc Msgs Comp...");
    cleanInvalidData(
        dailyDiscordMessagesComparedRepository,
        validEmployeeIds,
        "daily_discord_messages_compared");

    System.out.println("Cleaning Daily Dc Msgs...");
    cleanInvalidData(
        dailyDiscordMessagesRepository,
        validEmployeeIds,
        "daily_discord_messages");

    System.out.println("Cleaning Daily Dc Tickets...");
    cleanInvalidData(
        dailyDiscordTicketsRepository,
        validEmployeeIds,
        "daily_discord_tickets");

    System.out.println("Cleaning Employee Codes...");
    cleanInvalidData(
        employeeCodesRepository,
        validEmployeeIds,
        "employee_codes");

    System.out.println("Cleaning Employee level change...");
    cleanInvalidData(
        employeeLevelChangeRepository,
        validEmployeeIds,
        "employee_level_change");

    System.out.println("Cleaning Avg Daily Mc Tickets...");
    cleanInvalidData(
        averageDailyMinecraftTicketsRepository,
        validEmployeeIds,
        "average_daily_minecraft_tickets");

    System.out.println("Cleaning Avg Mc Tickets Comp...");
    cleanInvalidData(
        averageMinecraftTicketsComparedRepository,
        validEmployeeIds,
        "average_minecraft_tickets_compared");

    System.out.println("Cleaning Avg Mc Tickets per playtime...");
    cleanInvalidData(
        averageMinecraftTicketsPerPlaytimeRepository,
        validEmployeeIds,
        "average_minecraft_tickets_per_playtime");

    System.out.println("Cleaning Daily Mc tickets comp...");
    cleanInvalidData(
        dailyMinecraftTicketsComparedRepository,
        validEmployeeIds,
        "daily_minecraft_tickets_compared");

    System.out.println("Cleaning Daily mc tickets...");
    cleanInvalidData(
        dailyMinecraftTicketsRepository,
        validEmployeeIds,
        "daily_minecraft_tickets");

    System.out.println("Cleaning Mc tickets last check...");
    cleanInvalidData(
        mcTicketsLastCheckRepository,
        validEmployeeIds,
        "mc_tickets_last_check");

    System.out.println("Cleaning Total mc tickets...");
    cleanInvalidData(
        totalMinecraftTicketsRepository,
        validEmployeeIds,
        "total_minecraft_tickets");

    System.out.println("Cleaning Total old mc tickets...");
    cleanInvalidData(
        totalOldMinecraftTicketsRepository,
        validEmployeeIds,
        "total_old_minecraft_tickets");

    System.out.println("Cleaning Annual Playtime...");
    cleanInvalidData(
        annualPlaytimeRepository,
        validEmployeeIds,
        "annual_playtime");

    System.out.println("Cleaning Avg playtime overall...");
    cleanInvalidData(
        averagePlaytimeOverallRepository,
        validEmployeeIds,
        "average_playtime_overall");

    System.out.println("Cleaning Daily Playtime...");
    cleanInvalidData(
        dailyPlaytimeRepository,
        validEmployeeIds,
        "daily_playtime");

    System.out.println("Cleaning login logout times...");
    cleanInvalidData(
        loginLogoutTimesRepository,
        validEmployeeIds,
        "login_logout_times");

    System.out.println("Cleaning Playtime sessions...");
    cleanInvalidData(
        playtimeSessionsRepository,
        validEmployeeIds,
        "playtime_sessions");

    System.out.println("Cleaning Processed playtime sessions...");
    cleanInvalidData(
        playtimeSessionsRepository,
        validEmployeeIds,
        "playtime_sessions_processed");

    System.out.println("Cleaning Sanitized data...");
    cleanInvalidData(
        sanitazedDataRepository,
        validEmployeeIds,
        "sanitized_data");

    System.out.println("Cleaning Segment count all servers...");
    cleanInvalidData(
        segmentCountAllServersRepository,
        validEmployeeIds,
        "segment_count_all_servers");

    System.out.println("Cleaning Segment count by server...");
    cleanInvalidData(
        segmentCountByServerRepository,
        validEmployeeIds,
        "segment_count_by_server");

    System.out.println("Cleaning session duration...");
    cleanInvalidData(
        sessionDurationRepository,
        validEmployeeIds,
        "session_duration");

    System.out.println("Cleaning Time of day segments...");
    cleanInvalidData(
        timeOfDaySegmentsRepository,
        validEmployeeIds,
        "time_of_day_segments");

    System.out.println("Cleaning Daily productivity...");
    cleanInvalidData(
        dailyProductivityRepository,
        validEmployeeIds,
        "daily_productivity");

    System.out.println("Cleaning Productivity...");
    cleanInvalidData(
        productivityRepository,
        validEmployeeIds,
        "productivity");

    System.out.println("Cleaning Final stats...");
    cleanInvalidData(
        finalStatsRepository,
        validEmployeeIds,
        "final_stats");

    System.out.println("Cleaning Latest activity...");
    cleanInvalidData(
        latestActivityRepository,
        validEmployeeIds,
        "latest_activity");

    System.out.println("Cleaning Recommendations...");
    cleanInvalidData(
        recommendationsRepository,
        validEmployeeIds,
        "recommendations");

    System.out.println("✅ Cleanup completed for all repositories!");
  }

  private <T extends HasEmployeeId> void cleanInvalidData(
      JpaRepository<T, ?> repository,
      List<Short> validEmployeeIds,
      String repoName) {
    int page = 0;
    int pageSize = 1000;

    Page<T> pageData;

    do {
      pageData = repository.findAll(PageRequest.of(page, pageSize));
      List<T> invalidRecords = pageData
          .getContent()
          .stream()
          .filter(data -> !validEmployeeIds.contains(data.getEmployeeId()))
          .collect(Collectors.toList());

      if (!invalidRecords.isEmpty()) {
        repository.deleteAllInBatch(invalidRecords);
        System.out.println("🗑 Deleted " + invalidRecords.size() + " invalid records from " + repoName + ".");
      }

      page++;
    } while (pageData.hasNext());
  }

  public interface HasEmployeeId {
    Short getEmployeeId();
  }
}
