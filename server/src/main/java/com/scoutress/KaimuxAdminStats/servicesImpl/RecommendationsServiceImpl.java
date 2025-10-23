package com.scoutress.KaimuxAdminStats.servicesImpl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.scoutress.KaimuxAdminStats.constants.CalculationConstants;
import com.scoutress.KaimuxAdminStats.entity.Recommendations;
import com.scoutress.KaimuxAdminStats.entity.employees.Employee;
import com.scoutress.KaimuxAdminStats.entity.playtime.AnnualPlaytime;
import com.scoutress.KaimuxAdminStats.entity.productivity.Productivity;
import com.scoutress.KaimuxAdminStats.repositories.RecommendationsRepository;
import com.scoutress.KaimuxAdminStats.repositories.employees.EmployeeRepository;
import com.scoutress.KaimuxAdminStats.repositories.playtime.AnnualPlaytimeRepository;
import com.scoutress.KaimuxAdminStats.repositories.productivity.ProductivityRepository;
import com.scoutress.KaimuxAdminStats.services.RecommendationsService;

@Service
public class RecommendationsServiceImpl implements RecommendationsService {

  private static final Logger log = LoggerFactory.getLogger(RecommendationsServiceImpl.class);

  private final ProductivityRepository productivityRepository;
  private final EmployeeRepository employeeRepository;
  private final AnnualPlaytimeRepository annualPlaytimeRepository;
  private final RecommendationsRepository recommendationsRepository;

  private static final double MIN_ANNUAL_PLAYTIME = CalculationConstants.MIN_ANNUAL_PLAYTIME;
  private static final double PROMOTION_VALUE = CalculationConstants.PROMOTION_VALUE;
  private static final double DEMOTION_VALUE = CalculationConstants.DEMOTION_VALUE;

  public RecommendationsServiceImpl(
      ProductivityRepository productivityRepository,
      EmployeeRepository employeeRepository,
      AnnualPlaytimeRepository annualPlaytimeRepository,
      RecommendationsRepository recommendationsRepository) {
    this.productivityRepository = productivityRepository;
    this.employeeRepository = employeeRepository;
    this.annualPlaytimeRepository = annualPlaytimeRepository;
    this.recommendationsRepository = recommendationsRepository;
  }

  @Override
  @Transactional
  public void handleRecommendations() {
    long start = System.currentTimeMillis();
    log.info("🧮 [START] Starting recommendation calculation...");

    List<Employee> employees = employeeRepository.findAll();
    if (employees.isEmpty()) {
      log.warn("⚠ No employees found. Skipping recommendations.");
      return;
    }

    log.info("Found {} employees for recommendation processing.", employees.size());

    // === Load data sources ===
    Map<Short, Double> playtimeMap = annualPlaytimeRepository.findAll()
        .stream()
        .collect(Collectors.toMap(
            AnnualPlaytime::getEmployeeId,
            AnnualPlaytime::getPlaytimeInHours,
            (a, b) -> a));

    Map<Short, Double> productivityMap = productivityRepository.findAll()
        .stream()
        .collect(Collectors.toMap(
            Productivity::getEmployeeId,
            Productivity::getValue,
            (a, b) -> a));

    Map<Short, Recommendations> existing = recommendationsRepository.findAll()
        .stream()
        .collect(Collectors.toMap(
            Recommendations::getEmployeeId,
            r -> r,
            (a, b) -> a));

    log.debug("📊 Data loaded: {} playtime, {} productivity, {} existing recommendations.",
        playtimeMap.size(), productivityMap.size(), existing.size());

    List<Recommendations> updated = new ArrayList<>();

    for (Employee emp : employees) {
      short id = emp.getId();
      String level = emp.getLevel();
      double playtime = playtimeMap.getOrDefault(id, 0.0);
      double productivity = productivityMap.getOrDefault(id, 0.0);

      String newValue = calculateRecommendation(level, playtime, productivity);

      Recommendations record = existing.getOrDefault(id, new Recommendations());
      record.setEmployeeId(id);
      record.setValue(newValue);
      updated.add(record);

      if (log.isTraceEnabled()) {
        log.trace("🧾 Employee {} ({}) → Playtime: {}, Productivity: {}, Recommendation: {}",
            id, level, playtime, productivity, newValue);
      }
    }

    recommendationsRepository.saveAll(updated);
    long duration = System.currentTimeMillis() - start;
    log.info("✅ [DONE] Recommendations calculated for {} employees in {} ms.", updated.size(), duration);
  }

  private String calculateRecommendation(String level, double playtime, double productivity) {
    if (level == null) {
      log.warn("⚠ Employee level is null — returning 'Error'.");
      return "Error";
    }

    if (isAdmin(level)) {
      if (log.isTraceEnabled())
        log.trace("Level '{}' detected as admin — recommendation set to '-'", level);
      return "-";
    }

    if (playtime < MIN_ANNUAL_PLAYTIME) {
      if (log.isTraceEnabled())
        log.trace("Playtime {} < MIN_ANNUAL_PLAYTIME {} → Dismiss", playtime, MIN_ANNUAL_PLAYTIME);
      return "Dismiss";
    }

    if (productivity > PROMOTION_VALUE) {
      if (log.isTraceEnabled())
        log.trace("Productivity {} > PROMOTION_VALUE {} → Promote", productivity, PROMOTION_VALUE);
      return "Promote";
    }

    if (productivity < DEMOTION_VALUE) {
      if (log.isTraceEnabled())
        log.trace("Productivity {} < DEMOTION_VALUE {} → Demote", productivity, DEMOTION_VALUE);
      return "Demote";
    }

    if (log.isTraceEnabled())
      log.trace("Playtime={} and Productivity={} within neutral range → '-'", playtime, productivity);

    return "-";
  }

  private boolean isAdmin(String level) {
    boolean admin = switch (level) {
      case "Owner", "Operator", "Organizer" -> true;
      default -> false;
    };
    if (log.isTraceEnabled())
      log.trace("Checked level '{}' → isAdmin={}", level, admin);
    return admin;
  }
}
