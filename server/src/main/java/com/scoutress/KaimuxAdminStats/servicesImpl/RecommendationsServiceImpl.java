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
    log.info("📊 Starting recommendation calculation...");

    List<Employee> employees = employeeRepository
        .findAll();
    if (employees.isEmpty()) {
      log.warn("No employees found, skipping recommendations.");
      return;
    }

    Map<Short, Double> playtimeMap = annualPlaytimeRepository
        .findAll()
        .stream()
        .collect(Collectors.toMap(
            AnnualPlaytime::getEmployeeId,
            AnnualPlaytime::getPlaytimeInHours,
            (a, b) -> a));

    Map<Short, Double> productivityMap = productivityRepository
        .findAll()
        .stream()
        .collect(Collectors.toMap(
            Productivity::getEmployeeId,
            Productivity::getValue,
            (a, b) -> a));

    Map<Short, Recommendations> existing = recommendationsRepository
        .findAll()
        .stream()
        .collect(Collectors.toMap(
            Recommendations::getEmployeeId,
            r -> r,
            (a, b) -> a));

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
    }

    recommendationsRepository.saveAll(updated);
    long end = System.currentTimeMillis();
    log.info("✅ Recommendations calculated for {} employees in {} ms.", updated.size(), (end - start));
  }

  private String calculateRecommendation(String level, double playtime, double productivity) {
    if (level == null)
      return "Error";

    if (isAdmin(level)) {
      return "-";
    }

    if (playtime < MIN_ANNUAL_PLAYTIME) {
      return "Dismiss";
    }

    if (productivity > PROMOTION_VALUE) {
      return "Promote";
    }

    if (productivity < DEMOTION_VALUE) {
      return "Demote";
    }

    return "-";
  }

  private boolean isAdmin(String level) {
    return switch (level) {
      case "Owner", "Operator", "Organizer" -> true;
      default -> false;
    };
  }
}
