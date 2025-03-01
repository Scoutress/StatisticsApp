package com.scoutress.KaimuxAdminStats.servicesImpl;

import java.util.List;

import org.springframework.stereotype.Service;

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
  public void evaluateRecommendations() {
    List<Productivity> rawProductivityData = getAllProductivityData();
    List<AnnualPlaytime> rawAnnualPlaytimeData = getAllAnnualPlaytimeData();
    List<Employee> rawEmployeesData = getAllEmployeesData();
    List<Short> allEmployeeIds = getAllEmployeeIds(rawEmployeesData);
    String recommendation;

    for (Short employeeId : allEmployeeIds) {
      String levelForThisEmployee = getLevelForThisEmployee(rawEmployeesData, employeeId);
      double annualPlaytimeForThisEmployee = getAnnualPlaytimeForThisEmployee(rawAnnualPlaytimeData, employeeId);
      double productivityForThisEmployee = getProductivityForThisEmployee(rawProductivityData, employeeId);

      if (isValidEmployeeData(levelForThisEmployee, annualPlaytimeForThisEmployee, productivityForThisEmployee)) {
        if (levelForThisEmployee.equals("Owner") ||
            levelForThisEmployee.equals("Operator") ||
            levelForThisEmployee.equals("Organizer")) {

          recommendation = "-";
          saveRecommendationForThisEmployee(recommendation, employeeId);
        } else {

          if (annualPlaytimeForThisEmployee < MIN_ANNUAL_PLAYTIME) {
            recommendation = "Dismiss";

          } else {

            if (productivityForThisEmployee > PROMOTION_VALUE) {
              recommendation = "Promote";
            } else if (productivityForThisEmployee < DEMOTION_VALUE) {
              recommendation = "Demote";
            } else {
              recommendation = "-";
            }
          }
          saveRecommendationForThisEmployee(recommendation, employeeId);
        }

      } else {
        recommendation = "Error";
        saveRecommendationForThisEmployee(recommendation, employeeId);
      }
    }
  }

  public List<Productivity> getAllProductivityData() {
    return productivityRepository.findAll();
  }

  public List<AnnualPlaytime> getAllAnnualPlaytimeData() {
    return annualPlaytimeRepository.findAll();
  }

  public List<Employee> getAllEmployeesData() {
    return employeeRepository.findAll();
  }

  public List<Short> getAllEmployeeIds(List<Employee> rawEmployeesData) {
    return rawEmployeesData
        .stream()
        .map(Employee::getId)
        .distinct()
        .sorted()
        .toList();
  }

  public String getLevelForThisEmployee(List<Employee> rawEmployeesData, Short employeeId) {
    if (rawEmployeesData == null) {
      return "n/a";
    }

    return rawEmployeesData
        .stream()
        .filter(employee -> employee.getId().equals(employeeId))
        .map(Employee::getLevel)
        .findFirst()
        .orElse("n/a");
  }

  public double getAnnualPlaytimeForThisEmployee(List<AnnualPlaytime> rawAnnualPlaytimeData, Short employeeId) {
    if (rawAnnualPlaytimeData == null) {
      return 0.0;
    }

    return rawAnnualPlaytimeData
        .stream()
        .filter(playtime -> playtime.getEmployeeId().equals(employeeId))
        .map(AnnualPlaytime::getPlaytimeInHours)
        .findFirst()
        .orElse(0.0);
  }

  public double getProductivityForThisEmployee(List<Productivity> rawProductivityData, Short employeeId) {
    if (rawProductivityData == null) {
      return 0.0;
    }

    return rawProductivityData
        .stream()
        .filter(productivity -> productivity.getEmployeeId().equals(employeeId))
        .map(Productivity::getValue)
        .findFirst()
        .orElse(0.0);
  }

  public boolean isValidEmployeeData(String level, double playtime, double productivity) {
    return level != null && playtime >= 0.0 && productivity >= 0.0;
  }

  public void saveRecommendationForThisEmployee(String recommendation, Short employeeId) {
    if (recommendation == null) {
      return;
    }

    Recommendations existingRecord = recommendationsRepository.findByEmployeeId(employeeId);

    if (existingRecord != null) {
      if (!existingRecord.getValue().equals(recommendation)) {
        existingRecord.setValue(recommendation);
        recommendationsRepository.save(existingRecord);
      }
    } else {
      Recommendations newRecord = new Recommendations();
      newRecord.setEmployeeId(employeeId);
      newRecord.setValue(recommendation);
      recommendationsRepository.save(newRecord);
    }
  }
}
