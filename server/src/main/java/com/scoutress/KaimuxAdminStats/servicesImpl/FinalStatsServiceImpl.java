package com.scoutress.KaimuxAdminStats.servicesImpl;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.scoutress.KaimuxAdminStats.entity.FinalStats;
import com.scoutress.KaimuxAdminStats.entity.Recommendations;
import com.scoutress.KaimuxAdminStats.entity.discordMessages.AverageDailyDiscordMessages;
import com.scoutress.KaimuxAdminStats.entity.discordMessages.AverageDiscordMessagesCompared;
import com.scoutress.KaimuxAdminStats.entity.employees.Employee;
import com.scoutress.KaimuxAdminStats.entity.minecraftTickets.AverageDailyMinecraftTickets;
import com.scoutress.KaimuxAdminStats.entity.minecraftTickets.AverageMinecraftTicketsCompared;
import com.scoutress.KaimuxAdminStats.entity.playtime.AnnualPlaytime;
import com.scoutress.KaimuxAdminStats.entity.playtime.AveragePlaytimeOverall;
import com.scoutress.KaimuxAdminStats.entity.productivity.Productivity;
import com.scoutress.KaimuxAdminStats.repositories.FinalStatsRepository;
import com.scoutress.KaimuxAdminStats.repositories.RecommendationsRepository;
import com.scoutress.KaimuxAdminStats.repositories.discordMessages.AverageDailyDiscordMessagesRepository;
import com.scoutress.KaimuxAdminStats.repositories.discordMessages.AverageDiscordMessagesComparedRepository;
import com.scoutress.KaimuxAdminStats.repositories.employees.EmployeeRepository;
import com.scoutress.KaimuxAdminStats.repositories.minecraftTickets.AverageDailyMinecraftTicketsRepository;
import com.scoutress.KaimuxAdminStats.repositories.minecraftTickets.AverageMinecraftTicketsComparedRepository;
import com.scoutress.KaimuxAdminStats.repositories.playtime.AnnualPlaytimeRepository;
import com.scoutress.KaimuxAdminStats.repositories.playtime.AveragePlaytimeOverallRepository;
import com.scoutress.KaimuxAdminStats.repositories.productivity.ProductivityRepository;
import com.scoutress.KaimuxAdminStats.services.FinalStatsService;

@Service
public class FinalStatsServiceImpl implements FinalStatsService {

  private static final Logger log = LoggerFactory.getLogger(FinalStatsServiceImpl.class);

  private final EmployeeRepository employeeRepository;
  private final AnnualPlaytimeRepository annualPlaytimeRepository;
  private final AverageDailyMinecraftTicketsRepository averageDailyMinecraftTicketsRepository;
  private final AverageMinecraftTicketsComparedRepository averageMinecraftTicketsComparedRepository;
  private final AverageDailyDiscordMessagesRepository averageDailyDiscordMessagesRepository;
  private final AverageDiscordMessagesComparedRepository averageDiscordMessagesComparedRepository;
  private final AveragePlaytimeOverallRepository averagePlaytimeOverallRepository;
  private final ProductivityRepository productivityRepository;
  private final RecommendationsRepository recommendationsRepository;
  private final FinalStatsRepository finalStatsRepository;

  public FinalStatsServiceImpl(
      EmployeeRepository employeeRepository,
      AnnualPlaytimeRepository annualPlaytimeRepository,
      AverageDailyMinecraftTicketsRepository averageDailyMinecraftTicketsRepository,
      AverageMinecraftTicketsComparedRepository averageMinecraftTicketsComparedRepository,
      AverageDailyDiscordMessagesRepository averageDailyDiscordMessagesRepository,
      AverageDiscordMessagesComparedRepository averageDiscordMessagesComparedRepository,
      AveragePlaytimeOverallRepository averagePlaytimeOverallRepository,
      ProductivityRepository productivityRepository,
      RecommendationsRepository recommendationsRepository,
      FinalStatsRepository finalStatsRepository) {
    this.employeeRepository = employeeRepository;
    this.annualPlaytimeRepository = annualPlaytimeRepository;
    this.averageDailyMinecraftTicketsRepository = averageDailyMinecraftTicketsRepository;
    this.averageMinecraftTicketsComparedRepository = averageMinecraftTicketsComparedRepository;
    this.averageDailyDiscordMessagesRepository = averageDailyDiscordMessagesRepository;
    this.averageDiscordMessagesComparedRepository = averageDiscordMessagesComparedRepository;
    this.averagePlaytimeOverallRepository = averagePlaytimeOverallRepository;
    this.productivityRepository = productivityRepository;
    this.recommendationsRepository = recommendationsRepository;
    this.finalStatsRepository = finalStatsRepository;
  }

  @Override
  @Transactional
  public void handleFinalStats() {
    long start = System.currentTimeMillis();
    log.info("📊 [START] Final stats calculation initiated...");

    List<Employee> employees = employeeRepository.findAll();
    if (employees.isEmpty()) {
      log.warn("⚠ No employees found. Aborting final stats calculation.");
      return;
    }

    log.info("Found {} employees for processing.", employees.size());

    // === Load all required data maps ===
    Map<Short, Double> annualPlaytime = mapOf("AnnualPlaytime", annualPlaytimeRepository.findAll(),
        AnnualPlaytime::getEmployeeId, AnnualPlaytime::getPlaytimeInHours);

    Map<Short, Double> avgMcTickets = mapOf("AvgDailyMcTickets", averageDailyMinecraftTicketsRepository.findAll(),
        AverageDailyMinecraftTickets::getEmployeeId, AverageDailyMinecraftTickets::getTickets);

    Map<Short, Double> avgMcTicketsCompared = mapOf("AvgMcTicketsCompared",
        averageMinecraftTicketsComparedRepository.findAll(),
        AverageMinecraftTicketsCompared::getEmployeeId, AverageMinecraftTicketsCompared::getValue);

    Map<Short, Double> avgDcMessages = mapOf("AvgDailyDcMessages", averageDailyDiscordMessagesRepository.findAll(),
        AverageDailyDiscordMessages::getEmployeeId, AverageDailyDiscordMessages::getValue);

    Map<Short, Double> avgDcMessagesCompared = mapOf("AvgDcMessagesCompared",
        averageDiscordMessagesComparedRepository.findAll(),
        AverageDiscordMessagesCompared::getEmployeeId, AverageDiscordMessagesCompared::getValue);

    Map<Short, Double> avgPlaytime = mapOf("AvgPlaytimeOverall", averagePlaytimeOverallRepository.findAll(),
        AveragePlaytimeOverall::getEmployeeId, AveragePlaytimeOverall::getPlaytime);

    Map<Short, Double> productivity = mapOf("Productivity", productivityRepository.findAll(),
        Productivity::getEmployeeId, Productivity::getValue);

    Map<Short, String> recommendations = mapOf("Recommendations", recommendationsRepository.findAll(),
        Recommendations::getEmployeeId, Recommendations::getValue);

    // === Calculate stats per employee ===
    List<FinalStats> results = new ArrayList<>();

    for (Employee e : employees) {
      short id = e.getId();
      String level = e.getLevel();
      String username = e.getUsername();

      FinalStats stats = finalStatsRepository.findByEmployeeId(id);
      if (stats == null)
        stats = new FinalStats();

      stats.setEmployeeId(id);
      stats.setLevel(level);
      stats.setUsername(username);

      stats.setAnnualPlaytime(annualPlaytime.getOrDefault(id, 0.0));
      stats.setPlaytime(avgPlaytime.getOrDefault(id, 0.0));

      if (isAdmin(level)) {
        if (log.isTraceEnabled())
          log.trace("Employee {} ({}) is admin — zeroing data fields.", username, level);
        stats.setMinecraftTickets(0.0);
        stats.setMinecraftTicketsCompared(0.0);
        stats.setDiscordMessages(0.0);
        stats.setDiscordMessagesCompared(0.0);
        stats.setProductivity(0.0);
        stats.setRecommendation("-");
      } else {
        stats.setMinecraftTickets(avgMcTickets.getOrDefault(id, 0.0));
        stats.setMinecraftTicketsCompared(avgMcTicketsCompared.getOrDefault(id, 0.0));
        stats.setDiscordMessages(avgDcMessages.getOrDefault(id, 0.0));
        stats.setDiscordMessagesCompared(avgDcMessagesCompared.getOrDefault(id, 0.0));
        stats.setProductivity(productivity.getOrDefault(id, 0.0));
        stats.setRecommendation(recommendations.getOrDefault(id, "-"));
      }

      if (log.isTraceEnabled()) {
        log.trace(
            "Finalized [{}] -> Playtime={}, McTickets={}, McTicketsComp={}, DcMsgs={}, DcMsgsComp={}, Prod={}, Rec={}",
            username, stats.getPlaytime(), stats.getMinecraftTickets(), stats.getMinecraftTicketsCompared(),
            stats.getDiscordMessages(), stats.getDiscordMessagesCompared(), stats.getProductivity(),
            stats.getRecommendation());
      }

      results.add(stats);
    }

    // === Persist and finish ===
    finalStatsRepository.saveAll(results);
    long duration = System.currentTimeMillis() - start;
    log.info("✅ [DONE] Final stats calculated and saved for {} employees in {} ms.", results.size(), duration);
  }

  private boolean isAdmin(String level) {
    boolean admin = level.equalsIgnoreCase("Owner") || level.equalsIgnoreCase("Operator");
    if (log.isTraceEnabled())
      log.trace("Checked if level '{}' is admin: {}", level, admin);
    return admin;
  }

  private <T, K, V> Map<K, V> mapOf(String name, List<T> list,
      java.util.function.Function<T, K> keyMapper,
      java.util.function.Function<T, V> valueMapper) {

    if (list == null || list.isEmpty()) {
      log.warn("⚠ Data source [{}] is empty.", name);
      return Map.of();
    }

    Map<K, V> map = list.stream()
        .filter(Objects::nonNull)
        .collect(Collectors.toMap(keyMapper, valueMapper, (a, b) -> a));

    log.debug("📥 [{}] Loaded {} records into memory.", name, map.size());
    return map;
  }

  @Override
  public List<FinalStats> getAllFinalStats() {
    List<FinalStats> stats = finalStatsRepository.findAll();
    log.info("Fetched {} FinalStats entries.", stats.size());
    return stats;
  }

  @Override
  public double getProductivity(Short employeeId) {
    FinalStats stats = finalStatsRepository.findByEmployeeId(employeeId);
    double value = stats != null ? stats.getProductivity() : 0.0;
    log.debug("Productivity for employee {} = {}", employeeId, value);
    return value;
  }

  @Override
  public Map<String, Object> getEmployeeRanking(Short employeeId) {
    List<FinalStats> all = finalStatsRepository.findAll();
    all.sort(Comparator.comparingDouble(FinalStats::getProductivity).reversed());
    int rank = 1;
    for (FinalStats s : all) {
      if (s.getEmployeeId().equals(employeeId))
        break;
      rank++;
    }
    Map<String, Object> result = new HashMap<>();
    result.put("rank", rank);
    result.put("totalEmployees", all.size());

    log.info("🏅 Employee {} ranking: {}/{}", employeeId, rank, all.size());
    return result;
  }
}
