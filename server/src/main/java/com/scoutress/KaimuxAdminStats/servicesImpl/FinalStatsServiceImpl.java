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
    log.info("📊 Starting final stats calculation...");

    List<Employee> employees = employeeRepository.findAll();
    if (employees.isEmpty()) {
      log.warn("No employees found. Aborting.");
      return;
    }

    Map<Short, Double> annualPlaytime = mapOf(annualPlaytimeRepository.findAll(), AnnualPlaytime::getEmployeeId,
        AnnualPlaytime::getPlaytimeInHours);
    Map<Short, Double> avgMcTickets = mapOf(averageDailyMinecraftTicketsRepository.findAll(),
        AverageDailyMinecraftTickets::getEmployeeId, AverageDailyMinecraftTickets::getTickets);
    Map<Short, Double> avgMcTicketsCompared = mapOf(averageMinecraftTicketsComparedRepository.findAll(),
        AverageMinecraftTicketsCompared::getEmployeeId, AverageMinecraftTicketsCompared::getValue);
    Map<Short, Double> avgDcMessages = mapOf(averageDailyDiscordMessagesRepository.findAll(),
        AverageDailyDiscordMessages::getEmployeeId, AverageDailyDiscordMessages::getValue);
    Map<Short, Double> avgDcMessagesCompared = mapOf(averageDiscordMessagesComparedRepository.findAll(),
        AverageDiscordMessagesCompared::getEmployeeId, AverageDiscordMessagesCompared::getValue);
    Map<Short, Double> avgPlaytime = mapOf(averagePlaytimeOverallRepository.findAll(),
        AveragePlaytimeOverall::getEmployeeId, AveragePlaytimeOverall::getPlaytime);
    Map<Short, Double> productivity = mapOf(productivityRepository.findAll(), Productivity::getEmployeeId,
        Productivity::getValue);
    Map<Short, String> recommendations = mapOf(recommendationsRepository.findAll(), Recommendations::getEmployeeId,
        Recommendations::getValue);

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

      results.add(stats);
    }

    finalStatsRepository.saveAll(results);
    long end = System.currentTimeMillis();
    log.info("✅ Final stats recalculated for {} employees in {} ms.", results.size(), (end - start));
  }

  private boolean isAdmin(String level) {
    return level.equalsIgnoreCase("Owner") || level.equalsIgnoreCase("Operator");
  }

  private <T, K, V> Map<K, V> mapOf(List<T> list, java.util.function.Function<T, K> keyMapper,
      java.util.function.Function<T, V> valueMapper) {
    return list
        .stream()
        .filter(Objects::nonNull)
        .collect(Collectors.toMap(keyMapper, valueMapper, (a, b) -> a));
  }

  @Override
  public List<FinalStats> getAllFinalStats() {
    return finalStatsRepository.findAll();
  }

  @Override
  public double getProductivity(Short employeeId) {
    FinalStats stats = finalStatsRepository.findByEmployeeId(employeeId);
    return stats != null ? stats.getProductivity() : 0.0;
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
    return result;
  }
}
