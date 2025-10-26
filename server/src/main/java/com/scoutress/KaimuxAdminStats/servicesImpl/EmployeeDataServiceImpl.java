package com.scoutress.KaimuxAdminStats.servicesImpl;

import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;

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
import com.scoutress.KaimuxAdminStats.services.HasEmployeeId;

import jakarta.transaction.Transactional;

@Service
public class EmployeeDataServiceImpl implements EmployeeDataService {

  private static final Logger log = LoggerFactory.getLogger(EmployeeDataServiceImpl.class);

  private final EmployeeRepository employeeRepository;
  private final EmployeeCodesRepository employeeCodesRepository;

  private final List<JpaRepository<? extends HasEmployeeId, ?>> repositories;

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
      RecommendationsRepository recommendationsRepository,
      SegmentCountAllServersRepository segmentCountAllServersRepository,
      SegmentCountByServerRepository segmentCountByServerRepository,
      SessionDurationRepository sessionDurationRepository,
      TimeOfDaySegmentsRepository timeOfDaySegmentsRepository,
      AverageMinecraftTicketsComparedRepository averageMinecraftTicketsComparedRepository,
      DailyPlaytimeRepository dailyPlaytimeRepository) {

    this.employeeRepository = employeeRepository;
    this.employeeCodesRepository = employeeCodesRepository;

    this.repositories = List.of(
        complaintsRepository,
        complaintsSumRepository,
        averageDailyDiscordMessagesRepository,
        averageDiscordMessagesComparedRepository,
        dailyDiscordMessagesComparedRepository,
        dailyDiscordMessagesRepository,
        dailyDiscordTicketsRepository,
        employeeCodesRepository,
        employeeLevelChangeRepository,
        averageDailyMinecraftTicketsRepository,
        averageMinecraftTicketsComparedRepository,
        averageMinecraftTicketsPerPlaytimeRepository,
        dailyMinecraftTicketsComparedRepository,
        dailyMinecraftTicketsRepository,
        mcTicketsLastCheckRepository,
        totalMinecraftTicketsRepository,
        totalOldMinecraftTicketsRepository,
        annualPlaytimeRepository,
        averagePlaytimeOverallRepository,
        dailyPlaytimeRepository,
        loginLogoutTimesRepository,
        playtimeSessionsRepository,
        sanitazedDataRepository,
        segmentCountAllServersRepository,
        segmentCountByServerRepository,
        sessionDurationRepository,
        timeOfDaySegmentsRepository,
        dailyProductivityRepository,
        productivityRepository,
        finalStatsRepository,
        latestActivityRepository,
        recommendationsRepository);
  }

  @Override
  public List<Short> checkNessesaryEmployeeData() {
    List<Employee> employees = employeeRepository.findAll();
    List<EmployeeCodes> codes = employeeCodesRepository.findAll();

    return employees.stream()
        .map(Employee::getId)
        .filter(id -> codes.stream()
            .noneMatch(code -> code.getEmployeeId().equals(id)
                && code.getKmxWebApi() != null
                && code.getDiscordUserId() != null))
        .toList();
  }

  @Override
  @Transactional
  public void removeNotEmployeesData() {
    long startTime = System.currentTimeMillis();
    List<Short> validEmployeeIds = employeeRepository.findAll()
        .stream()
        .map(Employee::getId)
        .toList();

    log.info("=== [START] Cleaning invalid employee data ===");
    log.info("🧾 Valid employees found: {}", validEmployeeIds.size());
    log.info("🧹 Total repositories to clean: {}", repositories.size());

    int totalDeleted = 0;
    int processed = 0;

    for (JpaRepository<? extends HasEmployeeId, ?> repo : repositories) {
      String repoName = getRepositoryName(repo);
      long repoStart = System.currentTimeMillis();

      try {
        int deleted = cleanInvalidData(repo, validEmployeeIds);
        totalDeleted += deleted;
        processed++;

        long elapsed = System.currentTimeMillis() - repoStart;
        log.info("✅ [{}/{}] {} cleaned — {} invalid records removed ({} ms)",
            processed, repositories.size(), repoName, deleted, elapsed);

        if (log.isDebugEnabled()) {
          log.debug("Repository {} finished cleanup with {} deletions in {} ms", repoName, deleted, elapsed);
        }

      } catch (Exception e) {
        processed++;
        log.error("❌ [{}/{}] Error cleaning repository {}: {}", processed, repositories.size(), repoName,
            e.getMessage());
        if (log.isDebugEnabled()) {
          log.debug("Stack trace for repository {} cleanup error:", repoName, e);
        }
      }
    }

    long totalTime = System.currentTimeMillis() - startTime;
    log.info("=== [DONE] Cleanup completed for {} repositories. Total invalid records removed: {} ({} ms) ===",
        repositories.size(), totalDeleted, totalTime);
  }

  private <T extends HasEmployeeId> int cleanInvalidData(
      JpaRepository<T, ?> repository,
      List<Short> validEmployeeIds) {

    int totalDeleted = 0;
    int page = 0;
    int pageSize = 1000;
    Page<T> pageData;

    do {
      pageData = repository.findAll(PageRequest.of(page, pageSize));
      List<T> invalidRecords = pageData
          .getContent()
          .stream()
          .filter(r -> r.getEmployeeId() == null || !validEmployeeIds.contains(r.getEmployeeId()))
          .collect(Collectors.toList());

      if (!invalidRecords.isEmpty()) {
        repository.deleteAllInBatch(invalidRecords);
        totalDeleted += invalidRecords.size();

        if (log.isTraceEnabled()) {
          log.trace("Deleted {} invalid entries in page {} from repository {}",
              invalidRecords.size(), page, getRepositoryName(repository));
        }
      }

      page++;
    } while (pageData.hasNext());

    return totalDeleted;
  }

  private String getRepositoryName(JpaRepository<?, ?> repo) {
    try {
      // Pirmiausia ieškome interfeiso iš tavo repo paketo
      for (Class<?> iface : repo.getClass().getInterfaces()) {
        String name = iface.getName();
        if (name.startsWith("com.scoutress.KaimuxAdminStats.repositories")) {
          return iface.getSimpleName();
        }
      }

      // Jei neradome – bandome ultimate target klasę
      Class<?> targetClass = org.springframework.aop.framework.AopProxyUtils.ultimateTargetClass(repo);
      if (targetClass != null && targetClass.getSimpleName().endsWith("Repository")) {
        return targetClass.getSimpleName();
      }

      // Fallback
      return repo.getClass().getSimpleName();
    } catch (Exception e) {
      log.warn("⚠️ Unable to resolve repository name for {}: {}", repo.getClass(), e.getMessage());
      return repo.getClass().getSimpleName();
    }
  }
}
