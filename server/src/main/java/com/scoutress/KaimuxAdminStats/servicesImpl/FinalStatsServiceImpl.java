package com.scoutress.KaimuxAdminStats.servicesImpl;

import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

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
  public void handleFinalStats() {
    List<Employee> rawEmployeesData = getRawEmployeesData();
    List<AnnualPlaytime> rawAnnualPlaytimeData = getRawAnnualPlaytimeData();
    List<AverageDailyMinecraftTickets> rawMinecraftTicketsData = getRawMinecraftTicketsData();
    List<AverageMinecraftTicketsCompared> rawMinecraftTicketsComparedData = getRawMinecraftTicketsComparedData();
    List<AverageDailyDiscordMessages> rawDiscordMessagesData = getRawDiscordMessagesData();
    List<AverageDiscordMessagesCompared> rawDiscordMessagesComparedData = getRawDiscordMessagesComparedData();
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
      double discordMessagesForThisEmployee = getDiscordMessagesForThisEmployee(
          rawDiscordMessagesData, employeeId);
      double discordMessagesComparedForThisEmployee = getDiscordMessagesComparedForThisEmployee(
          rawDiscordMessagesComparedData, employeeId);
      double playtimeForThisEmployee = getPlaytimeForThisEmployee(
          rawPlaytimeData, employeeId);
      double productivityForThisEmployee = getProductivityForThisEmployee(
          rawProductivityData, employeeId);
      String recommendationsForThisEmployee = getRecommendationsForThisEmployee(
          rawRecommendationsData, employeeId);

      if (employeeLevel.equals("Owner") || employeeLevel.equals("Operator")) {
        saveNewFinalStatsDataAsAdmin(employeeId, employeeLevel, employeeUsername,
            annualPlaytimeForThisEmployee, playtimeForThisEmployee);
      } else {
        saveNewFinalStatsDataAsModerator(employeeId, employeeLevel, employeeUsername, annualPlaytimeForThisEmployee,
            minecraftTicketsForThisEmployee, minecraftTicketsComparedForThisEmployee,
            discordMessagesForThisEmployee, discordMessagesComparedForThisEmployee,
            playtimeForThisEmployee, productivityForThisEmployee, recommendationsForThisEmployee);
      }
    }
  }

  private List<Employee> getRawEmployeesData() {
    return employeeRepository.findAll();
  }

  private List<AnnualPlaytime> getRawAnnualPlaytimeData() {
    return annualPlaytimeRepository.findAll();
  }

  private List<AverageDailyMinecraftTickets> getRawMinecraftTicketsData() {
    return averageDailyMinecraftTicketsRepository.findAll();
  }

  private List<AverageMinecraftTicketsCompared> getRawMinecraftTicketsComparedData() {
    return averageMinecraftTicketsComparedRepository.findAll();
  }

  private List<AverageDailyDiscordMessages> getRawDiscordMessagesData() {
    return averageDailyDiscordMessagesRepository.findAll();
  }

  private List<AverageDiscordMessagesCompared> getRawDiscordMessagesComparedData() {
    return averageDiscordMessagesComparedRepository.findAll();
  }

  private List<AveragePlaytimeOverall> getRawPlaytimeData() {
    return averagePlaytimeOverallRepository.findAll();
  }

  private List<Productivity> getRawProductivityData() {
    return productivityRepository.findAll();
  }

  private List<Recommendations> getRawRecommendationsData() {
    return recommendationsRepository.findAll();
  }

  private List<Short> getAllEmployeeIds(List<Employee> rawEmployeesData) {
    return rawEmployeesData
        .stream()
        .map(Employee::getId)
        .distinct()
        .sorted()
        .toList();
  }

  private String getEmployeeLevel(List<Employee> rawEmployeesData, Short employeeId) {
    return rawEmployeesData
        .stream()
        .filter(employee -> employee.getId().equals(employeeId))
        .map(Employee::getLevel)
        .findFirst()
        .orElse("Error");
  }

  private String getEmployeeUsername(List<Employee> rawEmployeesData, Short employeeId) {
    return rawEmployeesData
        .stream()
        .filter(employee -> employee.getId().equals(employeeId))
        .map(Employee::getUsername)
        .findFirst()
        .orElse("Error");
  }

  private double getAnnualPlaytimeForThisEmployee(List<AnnualPlaytime> rawAnnualPlaytimeData, Short employeeId) {
    return rawAnnualPlaytimeData
        .stream()
        .filter(employee -> employee.getEmployeeId().equals(employeeId))
        .map(AnnualPlaytime::getPlaytimeInHours)
        .findFirst()
        .orElse(0.0);
  }

  private double getMinecraftTicketsForThisEmployee(
      List<AverageDailyMinecraftTickets> rawMinecraftTicketsData, Short employeeId) {
    return rawMinecraftTicketsData
        .stream()
        .filter(employee -> employee.getEmployeeId().equals(employeeId))
        .map(AverageDailyMinecraftTickets::getTickets)
        .findFirst()
        .orElse(0.0);
  }

  private double getMinecraftTicketsComparedForThisEmployee(
      List<AverageMinecraftTicketsCompared> rawMinecraftTicketsComparedData, Short employeeId) {
    return rawMinecraftTicketsComparedData
        .stream()
        .filter(employee -> employee.getEmployeeId().equals(employeeId))
        .map(AverageMinecraftTicketsCompared::getValue)
        .findFirst()
        .orElse(0.0);
  }

  private double getDiscordMessagesForThisEmployee(
      List<AverageDailyDiscordMessages> rawDiscordMessagesData, Short employeeId) {
    return rawDiscordMessagesData
        .stream()
        .filter(employee -> employee.getEmployeeId().equals(employeeId))
        .map(AverageDailyDiscordMessages::getValue)
        .findFirst()
        .orElse(0.0);
  }

  private double getDiscordMessagesComparedForThisEmployee(
      List<AverageDiscordMessagesCompared> rawDiscordMessagesComparedData, Short employeeId) {
    return rawDiscordMessagesComparedData
        .stream()
        .filter(employee -> employee.getEmployeeId().equals(employeeId))
        .map(AverageDiscordMessagesCompared::getValue)
        .findFirst()
        .orElse(0.0);
  }

  private double getPlaytimeForThisEmployee(List<AveragePlaytimeOverall> rawPlaytimeData, Short employeeId) {
    return rawPlaytimeData
        .stream()
        .filter(employee -> employee.getEmployeeId().equals(employeeId))
        .map(AveragePlaytimeOverall::getPlaytime)
        .findFirst()
        .orElse(0.0);
  }

  private double getProductivityForThisEmployee(List<Productivity> rawProductivityData, Short employeeId) {
    return rawProductivityData
        .stream()
        .filter(employee -> employee.getEmployeeId().equals(employeeId))
        .map(Productivity::getValue)
        .findFirst()
        .orElse(0.0);
  }

  private String getRecommendationsForThisEmployee(List<Recommendations> rawRecommendationsData, Short employeeId) {
    return rawRecommendationsData
        .stream()
        .filter(employee -> employee.getEmployeeId().equals(employeeId))
        .map(Recommendations::getValue)
        .findFirst()
        .orElse("Error");
  }

  private void saveNewFinalStatsDataAsModerator(Short employeeId, String employeeLevel,
      String employeeUsername, double annualPlaytimeForThisEmployee,
      double minecraftTicketsForThisEmployee, double minecraftTicketsComparedForThisEmployee,
      double discordMessages, double discordMessagesCompared,
      double playtimeForThisEmployee, double productivityForThisEmployee, String recommendationsForThisEmployee) {

    FinalStats existingRecord = finalStatsRepository.findByEmployeeId(employeeId);

    if (existingRecord != null) {
      if (existingRecord.getEmployeeId().equals(employeeId)) {
        existingRecord.setLevel(employeeLevel);
        existingRecord.setUsername(employeeUsername);
        existingRecord.setAnnualPlaytime(annualPlaytimeForThisEmployee);
        existingRecord.setMinecraftTickets(minecraftTicketsForThisEmployee);
        existingRecord.setMinecraftTicketsCompared(minecraftTicketsComparedForThisEmployee);
        existingRecord.setDiscordMessages(discordMessages);
        existingRecord.setDiscordMessagesCompared(discordMessagesCompared);
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
      newRecord.setDiscordMessages(discordMessages);
      newRecord.setDiscordMessagesCompared(discordMessagesCompared);
      newRecord.setPlaytime(playtimeForThisEmployee);
      newRecord.setProductivity(productivityForThisEmployee);
      newRecord.setRecommendation(recommendationsForThisEmployee);

      finalStatsRepository.save(newRecord);
    }
  }

  private void saveNewFinalStatsDataAsAdmin(Short employeeId, String employeeLevel,
      String employeeUsername, double annualPlaytimeForThisEmployee, double playtimeForThisEmployee) {

    FinalStats existingRecord = finalStatsRepository.findByEmployeeId(employeeId);

    if (existingRecord != null) {
      if (existingRecord.getEmployeeId().equals(employeeId)) {
        existingRecord.setLevel(employeeLevel);
        existingRecord.setUsername(employeeUsername);
        existingRecord.setAnnualPlaytime(annualPlaytimeForThisEmployee);
        existingRecord.setMinecraftTickets(0.0);
        existingRecord.setMinecraftTicketsCompared(0.0);
        existingRecord.setDiscordMessages(0.0);
        existingRecord.setDiscordMessagesCompared(0.0);
        existingRecord.setPlaytime(playtimeForThisEmployee);
        existingRecord.setProductivity(0.0);
        existingRecord.setRecommendation("-");

        finalStatsRepository.save(existingRecord);
      }
    } else {
      FinalStats newRecord = new FinalStats();

      newRecord.setEmployeeId(employeeId);
      newRecord.setLevel(employeeLevel);
      newRecord.setUsername(employeeUsername);
      newRecord.setAnnualPlaytime(annualPlaytimeForThisEmployee);
      newRecord.setMinecraftTickets(0.0);
      newRecord.setMinecraftTicketsCompared(0.0);
      newRecord.setDiscordMessages(0.0);
      newRecord.setDiscordMessagesCompared(0.0);
      newRecord.setPlaytime(playtimeForThisEmployee);
      newRecord.setProductivity(0.0);
      newRecord.setRecommendation("-");

      finalStatsRepository.save(newRecord);
    }
  }

  @Override
  public List<FinalStats> getAllFinalStats() {
    return finalStatsRepository.findAll();
  }

  @Override
  public double getProductivity(Short employeeId) {
    FinalStats stats = finalStatsRepository.findByEmployeeId(employeeId);
    return stats.getProductivity();
  }

  @Override
  public Map<String, Object> getEmployeeRanking(Short employeeId) {
    List<FinalStats> productivityData = finalStatsRepository.findAll();
    productivityData.sort(Comparator.comparingDouble(FinalStats::getProductivity).reversed());
    int totalEmployees = productivityData.size();
    int rank = 1;
    for (FinalStats p : productivityData) {
      if (p.getEmployeeId().equals(employeeId)) {
        break;
      }
      rank++;
    }
    Map<String, Object> result = new HashMap<>();
    result.put("rank", rank);
    result.put("totalEmployees", totalEmployees);
    return result;
  }
}
