package com.scoutress.KaimuxAdminStats.servicesImpl;

import java.util.List;

import org.springframework.stereotype.Service;

import com.scoutress.KaimuxAdminStats.entity.FinalStats;
import com.scoutress.KaimuxAdminStats.entity.Recommendations;
import com.scoutress.KaimuxAdminStats.entity.employees.Employee;
import com.scoutress.KaimuxAdminStats.entity.minecraftTickets.AverageDailyMinecraftTickets;
import com.scoutress.KaimuxAdminStats.entity.minecraftTickets.AverageMinecraftTicketsCompared;
import com.scoutress.KaimuxAdminStats.entity.playtime.AnnualPlaytime;
import com.scoutress.KaimuxAdminStats.entity.playtime.AveragePlaytimeOverall;
import com.scoutress.KaimuxAdminStats.entity.productivity.Productivity;
import com.scoutress.KaimuxAdminStats.repositories.FinalStatsRepository;
import com.scoutress.KaimuxAdminStats.repositories.RecommendationsRepository;
import com.scoutress.KaimuxAdminStats.repositories.employees.EmployeeRepository;
import com.scoutress.KaimuxAdminStats.repositories.minecraftTickets.AverageDailyMinecraftTicketsRepository;
import com.scoutress.KaimuxAdminStats.repositories.minecraftTickets.AverageMinecraftTicketsComparedRepository;
import com.scoutress.KaimuxAdminStats.repositories.playtime.AnnualPlaytimeRepository;
import com.scoutress.KaimuxAdminStats.repositories.playtime.AveragePlaytimeOverallRepository;
import com.scoutress.KaimuxAdminStats.repositories.productivity.ProductivityRepository;
import com.scoutress.KaimuxAdminStats.services.FinalStatsService;

@Service
public class FinalStatsServiceImpl implements FinalStatsService {

  private final EmployeeRepository employeeRepository;
  private final AnnualPlaytimeRepository annualPlaytimeRepository;
  private final AverageDailyMinecraftTicketsRepository averageDailyMinecraftTicketsRepository;
  private final AverageMinecraftTicketsComparedRepository averageMinecraftTicketsComparedRepository;
  private final AveragePlaytimeOverallRepository averagePlaytimeOverallRepository;
  private final ProductivityRepository productivityRepository;
  private final RecommendationsRepository recommendationsRepository;
  private final FinalStatsRepository finalStatsRepository;

  public FinalStatsServiceImpl(
      EmployeeRepository employeeRepository,
      AnnualPlaytimeRepository annualPlaytimeRepository,
      AverageDailyMinecraftTicketsRepository averageDailyMinecraftTicketsRepository,
      AverageMinecraftTicketsComparedRepository averageMinecraftTicketsComparedRepository,
      AveragePlaytimeOverallRepository averagePlaytimeOverallRepository,
      ProductivityRepository productivityRepository,
      RecommendationsRepository recommendationsRepository,
      FinalStatsRepository finalStatsRepository) {
    this.employeeRepository = employeeRepository;
    this.annualPlaytimeRepository = annualPlaytimeRepository;
    this.averageDailyMinecraftTicketsRepository = averageDailyMinecraftTicketsRepository;
    this.averageMinecraftTicketsComparedRepository = averageMinecraftTicketsComparedRepository;
    this.averagePlaytimeOverallRepository = averagePlaytimeOverallRepository;
    this.productivityRepository = productivityRepository;
    this.recommendationsRepository = recommendationsRepository;
    this.finalStatsRepository = finalStatsRepository;
  }

  @Override
  public void updateNewStatsData() {
    List<Employee> rawEmployeesData = getRawEmployeesData();
    List<AnnualPlaytime> rawAnnualPlaytimeData = getRawAnnualPlaytimeData();
    List<AverageDailyMinecraftTickets> rawMinecraftTicketsData = getRawMinecraftTicketsData();
    List<AverageMinecraftTicketsCompared> rawMinecraftTicketsComparedData = getRawMinecraftTicketsComparedData();
    List<AveragePlaytimeOverall> rawPlaytimeData = getRawPlaytimeData();
    List<Productivity> rawProductivityData = getRawProductivityData();
    List<Recommendations> rawRecommendationsData = getRawRecommendationsData();

    List<Short> allEmployeeIds = getAllEmployeeIds(rawEmployeesData);

    for (Short employeeId : allEmployeeIds) {
      String employeeLevel = getEmployeeLevel(
          rawEmployeesData, employeeId);
      String employeeUsername = getEmployeeUsername(
          rawEmployeesData, employeeId);
      double annualPlaytimeForThisEmployee = getAnnualPlaytimeForThisEmployee(
          rawAnnualPlaytimeData, employeeId);
      double minecraftTicketsForThisEmployee = getMinecraftTicketsForThisEmployee(
          rawMinecraftTicketsData, employeeId);
      double minecraftTicketsComparedForThisEmployee = getMinecraftTicketsComparedForThisEmployee(
          rawMinecraftTicketsComparedData, employeeId);
      double playtimeForThisEmployee = getPlaytimeForThisEmployee(
          rawPlaytimeData, employeeId);
      double productivityForThisEmployee = getProductivityForThisEmployee(
          rawProductivityData, employeeId);
      String recommendationsForThisEmployee = getRecommendationsForThisEmployee(
          rawRecommendationsData, employeeId);

      saveNewFinalStatsData(employeeId, employeeLevel, employeeUsername, annualPlaytimeForThisEmployee,
          minecraftTicketsForThisEmployee, minecraftTicketsComparedForThisEmployee,
          playtimeForThisEmployee, productivityForThisEmployee, recommendationsForThisEmployee);
    }
  }

  public List<Employee> getRawEmployeesData() {
    return employeeRepository.findAll();
  }

  public List<AnnualPlaytime> getRawAnnualPlaytimeData() {
    return annualPlaytimeRepository.findAll();
  }

  public List<AverageDailyMinecraftTickets> getRawMinecraftTicketsData() {
    return averageDailyMinecraftTicketsRepository.findAll();
  }

  public List<AverageMinecraftTicketsCompared> getRawMinecraftTicketsComparedData() {
    return averageMinecraftTicketsComparedRepository.findAll();
  }

  public List<AveragePlaytimeOverall> getRawPlaytimeData() {
    return averagePlaytimeOverallRepository.findAll();
  }

  public List<Productivity> getRawProductivityData() {
    return productivityRepository.findAll();
  }

  public List<Recommendations> getRawRecommendationsData() {
    return recommendationsRepository.findAll();
  }

  public List<Short> getAllEmployeeIds(List<Employee> rawEmployeesData) {
    return rawEmployeesData
        .stream()
        .map(Employee::getId)
        .distinct()
        .sorted()
        .toList();
  }

  public String getEmployeeLevel(List<Employee> rawEmployeesData, Short employeeId) {
    return rawEmployeesData
        .stream()
        .filter(employee -> employee.getId().equals(employeeId))
        .map(Employee::getLevel)
        .findFirst()
        .orElse("Error");
  }

  public String getEmployeeUsername(List<Employee> rawEmployeesData, Short employeeId) {
    return rawEmployeesData
        .stream()
        .filter(employee -> employee.getId().equals(employeeId))
        .map(Employee::getUsername)
        .findFirst()
        .orElse("Error");
  }

  public double getAnnualPlaytimeForThisEmployee(List<AnnualPlaytime> rawAnnualPlaytimeData, Short employeeId) {
    return rawAnnualPlaytimeData
        .stream()
        .filter(employee -> employee.getEmployeeId().equals(employeeId))
        .map(AnnualPlaytime::getPlaytime)
        .findFirst()
        .orElse(0.0);
  }

  public double getMinecraftTicketsForThisEmployee(
      List<AverageDailyMinecraftTickets> rawMinecraftTicketsData, Short employeeId) {
    return rawMinecraftTicketsData
        .stream()
        .filter(employee -> employee.getEmployeeId().equals(employeeId))
        .map(AverageDailyMinecraftTickets::getTickets)
        .findFirst()
        .orElse(0.0);
  }

  public double getMinecraftTicketsComparedForThisEmployee(
      List<AverageMinecraftTicketsCompared> rawMinecraftTicketsComparedData, Short employeeId) {
    return rawMinecraftTicketsComparedData
        .stream()
        .filter(employee -> employee.getEmployeeId().equals(employeeId))
        .map(AverageMinecraftTicketsCompared::getValue)
        .findFirst()
        .orElse(0.0);
  }

  public double getPlaytimeForThisEmployee(List<AveragePlaytimeOverall> rawPlaytimeData, Short employeeId) {
    return rawPlaytimeData
        .stream()
        .filter(employee -> employee.getEmployeeId().equals(employeeId))
        .map(AveragePlaytimeOverall::getPlaytime)
        .findFirst()
        .orElse(0.0);
  }

  public double getProductivityForThisEmployee(List<Productivity> rawProductivityData, Short employeeId) {
    return rawProductivityData
        .stream()
        .filter(employee -> employee.getEmployeeId().equals(employeeId))
        .map(Productivity::getValue)
        .findFirst()
        .orElse(0.0);
  }

  public String getRecommendationsForThisEmployee(List<Recommendations> rawRecommendationsData, Short employeeId) {
    return rawRecommendationsData
        .stream()
        .filter(employee -> employee.getEmployeeId().equals(employeeId))
        .map(Recommendations::getValue)
        .findFirst()
        .orElse("Error");
  }

  public void saveNewFinalStatsData(Short employeeId, String employeeLevel,
      String employeeUsername, double annualPlaytimeForThisEmployee,
      double minecraftTicketsForThisEmployee, double minecraftTicketsComparedForThisEmployee,
      double playtimeForThisEmployee, double productivityForThisEmployee, String recommendationsForThisEmployee) {

    FinalStats existingRecord = finalStatsRepository.findByEmployeeId(employeeId);

    if (existingRecord != null) {
      if (existingRecord.getEmployeeId().equals(employeeId)) {
        existingRecord.setLevel(employeeLevel);
        existingRecord.setUsername(employeeUsername);
        existingRecord.setAnnualPlaytime(annualPlaytimeForThisEmployee);
        existingRecord.setMinecraftTickets(minecraftTicketsForThisEmployee);
        existingRecord.setMinecraftTicketsCompared(minecraftTicketsComparedForThisEmployee);
        existingRecord.setPlaytime(playtimeForThisEmployee);
        existingRecord.setProductivity(productivityForThisEmployee);
        existingRecord.setRecommendation(recommendationsForThisEmployee);

        finalStatsRepository.save(existingRecord);
      }
    } else {
      FinalStats newRecord = new FinalStats();

      newRecord.setEmployeeId(employeeId);
      newRecord.setLevel(employeeLevel);
      newRecord.setUsername(employeeUsername);
      newRecord.setAnnualPlaytime(annualPlaytimeForThisEmployee);
      newRecord.setMinecraftTickets(minecraftTicketsForThisEmployee);
      newRecord.setMinecraftTicketsCompared(minecraftTicketsComparedForThisEmployee);
      newRecord.setPlaytime(playtimeForThisEmployee);
      newRecord.setProductivity(productivityForThisEmployee);
      newRecord.setRecommendation(recommendationsForThisEmployee);

      finalStatsRepository.save(newRecord);
    }
  }

  @Override
  public List<FinalStats> getAllFinalStats() {
    return finalStatsRepository.findAll();
  }
}
