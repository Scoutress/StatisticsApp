package com.scoutress.KaimuxAdminStats.servicesImpl;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.scoutress.KaimuxAdminStats.constants.CalculationConstants;
import com.scoutress.KaimuxAdminStats.constants.RecommendationUserTexts;
import com.scoutress.KaimuxAdminStats.entity.FinalStats;
import com.scoutress.KaimuxAdminStats.entity.RecommendationUser;
import com.scoutress.KaimuxAdminStats.entity.complaints.Complaints;
import com.scoutress.KaimuxAdminStats.entity.employees.Employee;
import com.scoutress.KaimuxAdminStats.repositories.FinalStatsRepository;
import com.scoutress.KaimuxAdminStats.repositories.RecommendationUserRepository;
import com.scoutress.KaimuxAdminStats.repositories.complaints.ComplaintsRepository;
import com.scoutress.KaimuxAdminStats.repositories.employees.EmployeeRepository;
import com.scoutress.KaimuxAdminStats.services.RecommendationUserService;

import jakarta.transaction.Transactional;

@Service
public class RecommendationUserServiceImpl implements RecommendationUserService {

  private final EmployeeRepository employeeRepository;
  private final FinalStatsRepository finalStatsRepository;
  private final ComplaintsRepository complaintsRepository;
  private final RecommendationUserRepository recommendationUserRepository;

  public RecommendationUserServiceImpl(
      EmployeeRepository employeeRepository,
      FinalStatsRepository finalStatsRepository,
      ComplaintsRepository complaintsRepository,
      RecommendationUserRepository recommendationUserRepository) {
    this.employeeRepository = employeeRepository;
    this.finalStatsRepository = finalStatsRepository;
    this.complaintsRepository = complaintsRepository;
    this.recommendationUserRepository = recommendationUserRepository;
  }

  @Transactional
  @Override
  public void handleUserRecommendations() {
    List<Employee> employeesData = getAllEmployeeData();
    List<FinalStats> finalStatsData = getFinalStatsData();
    List<Complaints> complaintsData = getComplaintsData();

    for (Short employeeId : getAllEmployeeIds(employeesData)) {
      String level = getLevelThisEmployee(finalStatsData, employeeId);
      int complaintsCount = getComplaintsThisEmployee(complaintsData, employeeId);
      int daysSinceJoin = getDaysSinceJoin(employeesData, employeeId);
      double annualPlaytime = getAnnualPlaytimeThisEmployee(finalStatsData, employeeId);
      double productivity = getProductivityThisEmployee(finalStatsData, employeeId);

      String recommendationText = generateRecommendation(
          level, complaintsCount, daysSinceJoin,
          annualPlaytime, productivity * 100);

      saveUserRecommendationData(employeeId, recommendationText);
    }
  }

  private String generateRecommendation(String level, int complaints, int daysSinceJoin, double annualPlaytime,
      double productivity) {
    if (annualPlaytime < 10) {
      return RecommendationUserTexts.LOW_ANNUAL_DISMISS;
    } else {
      if (complaints > 15) {
        return RecommendationUserTexts.COMPLAINTS;
      } else {
        switch (level) {
          case "Owner", "Operator" -> {
            return RecommendationUserTexts.GREAT_JOB;
          }
          case "Helper" -> {
            if (productivity < 15)
              return RecommendationUserTexts.LOW_PRODUCTIVITY_DISMISS;
            if (productivity > 85 && daysSinceJoin >= CalculationConstants.WORK_TIME_HELPER)
              return RecommendationUserTexts.PROMOTE_PERC;
            return RecommendationUserTexts.OKAY_STATS;
          }
          case "Support" -> {
            if (productivity < 15)
              return RecommendationUserTexts.LOW_PRODUCTIVITY_DEMOTE;
            if (productivity > 85 && daysSinceJoin >= CalculationConstants.WORK_TIME_SUPPORT)
              return RecommendationUserTexts.PROMOTE_PERC;
            return RecommendationUserTexts.OKAY_STATS;
          }
          case "ChatMod" -> {
            if (productivity < 15)
              return RecommendationUserTexts.LOW_PRODUCTIVITY_DEMOTE;
            if (productivity > 85 && daysSinceJoin >= CalculationConstants.WORK_TIME_CHATMOD)
              return RecommendationUserTexts.PROMOTE_PERC;
            return RecommendationUserTexts.OKAY_STATS;
          }
          case "Overseer" -> {
            if (productivity < 15)
              return RecommendationUserTexts.LOW_PRODUCTIVITY_DEMOTE;
            if (productivity > 85 && daysSinceJoin >= CalculationConstants.WORK_TIME_OVERSEER)
              return RecommendationUserTexts.PROMOTE_PERC;
            return RecommendationUserTexts.OKAY_STATS;
          }
          case "Manager" -> {
            if (productivity < 15)
              return RecommendationUserTexts.LOW_PRODUCTIVITY_DEMOTE;
            if (productivity > 85)
              return RecommendationUserTexts.GREAT_JOB;
            return RecommendationUserTexts.OKAY_STATS;
          }
          default -> {
            return RecommendationUserTexts.OKAY_STATS;
          }
        }
      }
    }
  }

  private List<Employee> getAllEmployeeData() {
    return employeeRepository.findAll();
  }

  private List<FinalStats> getFinalStatsData() {
    return finalStatsRepository.findAll();
  }

  private List<Complaints> getComplaintsData() {
    return complaintsRepository.findAll();
  }

  private List<Short> getAllEmployeeIds(List<Employee> employeesData) {
    return employeesData.stream()
        .map(Employee::getId)
        .distinct()
        .collect(Collectors.toList());
  }

  private String getLevelThisEmployee(List<FinalStats> finalStatsData, Short employeeId) {
    return finalStatsData.stream()
        .filter(finalStats -> finalStats.getEmployeeId().equals(employeeId))
        .map(FinalStats::getLevel)
        .findFirst()
        .orElse("Level not found");
  }

  private int getComplaintsThisEmployee(List<Complaints> complaintsData, Short employeeId) {
    return (int) complaintsData.stream()
        .filter(complaints -> complaints.getEmployeeId().equals(employeeId))
        .count();
  }

  private int getDaysSinceJoin(List<Employee> employeesData, Short employeeId) {
    return employeesData.stream()
        .filter(employee -> employee.getId().equals(employeeId))
        .map(employee -> (int) ChronoUnit.DAYS.between(employee.getJoinDate(), LocalDate.now()))
        .findFirst()
        .orElse(0);
  }

  private double getAnnualPlaytimeThisEmployee(List<FinalStats> finalStatsData, Short employeeId) {
    return finalStatsData.stream()
        .filter(finalStats -> finalStats.getEmployeeId().equals(employeeId))
        .mapToDouble(FinalStats::getAnnualPlaytime)
        .sum();
  }

  private double getProductivityThisEmployee(List<FinalStats> finalStatsData, Short employeeId) {
    return finalStatsData.stream()
        .filter(finalStats -> finalStats.getEmployeeId().equals(employeeId))
        .mapToDouble(FinalStats::getProductivity)
        .average()
        .orElse(0.0);
  }

  private void saveUserRecommendationData(Short employeeId, String recommendationText) {
    if (employeeId == null || recommendationText == null) {
      return;
    }

    RecommendationUser existingRecord = recommendationUserRepository.findByEmployeeId(employeeId);

    if (existingRecord != null) {
      if (!existingRecord.getText().equals(recommendationText)) {
        existingRecord.setText(recommendationText);
        recommendationUserRepository.save(existingRecord);
      }
    } else {
      RecommendationUser newRecord = new RecommendationUser();
      newRecord.setEmployeeId(employeeId);
      newRecord.setText(recommendationText);
      recommendationUserRepository.save(newRecord);
    }
  }

  @Override
  public Map<String, Object> getEmployeeRecommendation(Short employeeId) {
    RecommendationUser employeeRecommendation = recommendationUserRepository.findByEmployeeId(employeeId);
    Map<String, Object> result = new HashMap<>();
    result.put("text", employeeRecommendation.getText());

    return result;
  }
}
