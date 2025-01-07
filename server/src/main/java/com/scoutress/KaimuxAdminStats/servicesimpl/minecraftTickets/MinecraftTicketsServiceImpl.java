package com.scoutress.KaimuxAdminStats.servicesImpl.minecraftTickets;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.scoutress.KaimuxAdminStats.entity.employees.Employee;
import com.scoutress.KaimuxAdminStats.entity.employees.EmployeeCodes;
import com.scoutress.KaimuxAdminStats.entity.minecraftTickets.AverageDailyMinecraftTickets;
import com.scoutress.KaimuxAdminStats.entity.minecraftTickets.AverageMinecraftTicketsPerPlaytime;
import com.scoutress.KaimuxAdminStats.entity.minecraftTickets.DailyMinecraftTickets;
import com.scoutress.KaimuxAdminStats.entity.minecraftTickets.MinecraftTicketsAnswers;
import com.scoutress.KaimuxAdminStats.entity.playtime.DailyPlaytime;
import com.scoutress.KaimuxAdminStats.repositories.employees.EmployeeRepository;
import com.scoutress.KaimuxAdminStats.repositories.minecraftTickets.AverageDailyMinecraftTicketsRepository;
import com.scoutress.KaimuxAdminStats.repositories.minecraftTickets.AverageMinecraftTicketsPerPlaytimeRepository;
import com.scoutress.KaimuxAdminStats.repositories.minecraftTickets.DailyMinecraftTicketsRepository;
import com.scoutress.KaimuxAdminStats.repositories.playtime.DailyPlaytimeRepository;
import com.scoutress.KaimuxAdminStats.services.DataExtractingService;
import com.scoutress.KaimuxAdminStats.services.minecraftTickets.MinecraftTicketsService;

@Service
public class MinecraftTicketsServiceImpl implements MinecraftTicketsService {

  public final DataExtractingService dataExtractingService;
  public final EmployeeRepository employeeRepository;
  public final DailyMinecraftTicketsRepository dailyMinecraftTicketsRepository;
  public final AverageDailyMinecraftTicketsRepository averageDailyMinecraftTicketsRepository;
  public final DailyPlaytimeRepository dailyPlaytimeRepository;
  public final AverageMinecraftTicketsPerPlaytimeRepository averageMinecraftTicketsPerPlaytimeRepository;

  public MinecraftTicketsServiceImpl(
      DataExtractingService dataExtractingService,
      EmployeeRepository employeeRepository,
      DailyMinecraftTicketsRepository discordTicketsRepository,
      AverageDailyMinecraftTicketsRepository averageDailyMinecraftTicketsRepository,
      DailyPlaytimeRepository dailyPlaytimeRepository,
      AverageMinecraftTicketsPerPlaytimeRepository averageMinecraftTicketsPerPlaytimeRepository) {
    this.dataExtractingService = dataExtractingService;
    this.employeeRepository = employeeRepository;
    this.dailyMinecraftTicketsRepository = discordTicketsRepository;
    this.averageDailyMinecraftTicketsRepository = averageDailyMinecraftTicketsRepository;
    this.dailyPlaytimeRepository = dailyPlaytimeRepository;
    this.averageMinecraftTicketsPerPlaytimeRepository = averageMinecraftTicketsPerPlaytimeRepository;
  }

  @Override
  public void convertMinecraftTicketsAnswers() {
    List<EmployeeCodes> employeeCodes = extractEmployeeCodes();

    if (employeeCodes == null || employeeCodes.isEmpty()) {
      throw new RuntimeException("No employee codes found. Cannot proceed.");
    }

    List<MinecraftTicketsAnswers> ticketsAnswers = extractDataFromAnswersTable();

    if (ticketsAnswers == null || ticketsAnswers.isEmpty()) {
      throw new RuntimeException("No tickets answers found. Cannot proceed.");
    }

    List<MinecraftTicketsAnswers> reactionsWithEmployeeIds = mapMinecraftToEmployeeIds(ticketsAnswers, employeeCodes);

    if (reactionsWithEmployeeIds == null || reactionsWithEmployeeIds.isEmpty()) {
      throw new RuntimeException("Failed to map tickets answers to employee IDs. Cannot proceed.");
    }

    List<DailyMinecraftTickets> convertedData = convertData(reactionsWithEmployeeIds);
    if (convertedData == null || convertedData.isEmpty()) {
      throw new RuntimeException("Data conversion failed. Cannot proceed.");
    }

    saveDataToNewTable(convertedData);
  }

  public List<EmployeeCodes> extractEmployeeCodes() {
    List<EmployeeCodes> data = dataExtractingService.getAllEmployeeCodes();
    return data;
  }

  public List<MinecraftTicketsAnswers> extractDataFromAnswersTable() {
    List<MinecraftTicketsAnswers> data = dataExtractingService.getAllMcTicketsAnswers();
    return data;
  }

  public List<MinecraftTicketsAnswers> mapMinecraftToEmployeeIds(
      List<MinecraftTicketsAnswers> answers,
      List<EmployeeCodes> employeeCodes) {

    Map<Short, Short> minecraftToEmployeeMap = employeeCodes
        .stream()
        .filter(code -> code.getMinecraftId() != null)
        .filter(code -> code.getEmployeeId() != null)
        .collect(Collectors
            .toMap(
                EmployeeCodes::getMinecraftId,
                EmployeeCodes::getEmployeeId));

    return answers
        .stream()
        .map(reaction -> {
          Short employeeId = minecraftToEmployeeMap
              .get(reaction
                  .getMinecraftTicketId()
                  .shortValue());
          if (employeeId != null) {
            reaction
                .setMinecraftTicketId(employeeId
                    .longValue());
          }
          return reaction;
        })
        .collect(Collectors.toList());
  }

  public List<DailyMinecraftTickets> convertData(List<MinecraftTicketsAnswers> rawData) {
    Map<Long, Map<LocalDate, Long>> groupedData = rawData.stream()
        .collect(Collectors.groupingBy(
            MinecraftTicketsAnswers::getMinecraftTicketId,
            Collectors.groupingBy(
                answer -> answer.getDateTime().toLocalDate(),
                Collectors.counting())));

    return groupedData.entrySet().stream()
        .flatMap(ticketEntry -> ticketEntry.getValue().entrySet().stream()
            .map(dateEntry -> new DailyMinecraftTickets(
                null,
                ticketEntry.getKey().shortValue(),
                dateEntry.getValue().intValue(),
                dateEntry.getKey())))
        .collect(Collectors.toList());
  }

  private void saveDataToNewTable(List<DailyMinecraftTickets> convertedData) {
    convertedData.sort((a, b) -> a.getDate().compareTo(b.getDate()));
    convertedData.forEach(dailyMinecraftTicketsRepository::save);
  }

  @Override
  public void calculateAverageDailyMinecraftTicketsValues() {
    List<DailyMinecraftTickets> rawData = getDailyMinecraftTicketsData();
    List<Employee> rawEmployeesData = getEmployeesData();
    List<Short> allEmployeeIds = getAllEmployeesFromTheTable(rawData);

    LocalDate oldestDateFromData = checkForOldestDate(rawData);

    for (Short employeeId : allEmployeeIds) {
      LocalDate joinDateForThisEmployee = getJoinDateForThisEmployee(rawEmployeesData, employeeId);
      LocalDate oldestDate = getOldestDateForThisEmployeeFromTickets(oldestDateFromData, joinDateForThisEmployee);
      int daysCount = calculateDaysAfterOldestDate(oldestDate);
      int ticketsCountSinceOldestDate = calculateAllTicketsSinceOldestDate(rawData, employeeId, oldestDate);
      double averageValue = calculateAverageTicketsValue(ticketsCountSinceOldestDate, daysCount);
      saveAverageValueData(averageValue, employeeId);
    }
  }

  public List<DailyMinecraftTickets> getDailyMinecraftTicketsData() {
    return dailyMinecraftTicketsRepository.findAll();
  }

  public List<Employee> getEmployeesData() {
    return employeeRepository.findAll();
  }

  public List<Short> getAllEmployeesFromTheTable(List<DailyMinecraftTickets> rawData) {
    return rawData
        .stream()
        .map(DailyMinecraftTickets::getEmployeeId)
        .distinct()
        .sorted()
        .toList();
  }

  public LocalDate checkForOldestDate(List<DailyMinecraftTickets> rawData) {
    return rawData
        .stream()
        .map(DailyMinecraftTickets::getDate)
        .min(LocalDate::compareTo)
        .orElse(LocalDate.of(2100, 1, 1));
  }

  public LocalDate getOldestDateForThisEmployeeFromTickets(
      LocalDate oldestDateFromData, LocalDate joinDateForThisEmployee) {
    return joinDateForThisEmployee.isAfter(oldestDateFromData)
        ? joinDateForThisEmployee
        : oldestDateFromData;
  }

  public int calculateDaysAfterOldestDate(LocalDate oldestDate) {
    LocalDate today = LocalDate.now();
    long daysBetweenLong = ChronoUnit.DAYS.between(oldestDate, today);
    return (int) daysBetweenLong;
  }

  public LocalDate getJoinDateForThisEmployee(List<Employee> rawEmployeesData, Short employee) {
    return rawEmployeesData
        .stream()
        .filter(employeeData -> employeeData.getId().equals(employee))
        .map(Employee::getJoinDate)
        .findFirst()
        .orElse(LocalDate.of(2100, 1, 1));
  }

  public int calculateAllTicketsSinceOldestDate(
      List<DailyMinecraftTickets> rawData, int employeeId, LocalDate oldestDate) {
    return rawData
        .stream()
        .filter(ticket -> ticket.getEmployeeId() == employeeId)
        .filter(ticket -> ticket.getDate().isAfter(oldestDate))
        .mapToInt(DailyMinecraftTickets::getTicketCount)
        .sum();
  }

  public double calculateAverageTicketsValue(int ticketsCountSinceOldestDate, int daysCount) {
    if (daysCount == 0) {
      return 0.0;
    }
    return ticketsCountSinceOldestDate / daysCount;
  }

  public void saveAverageValueData(double averageValue, Short employeeId) {
    AverageDailyMinecraftTickets existingRecord = averageDailyMinecraftTicketsRepository.findByEmployeeId(employeeId);

    if (existingRecord != null) {
      existingRecord.setTickets(averageValue);
      averageDailyMinecraftTicketsRepository.save(existingRecord);
    } else {
      AverageDailyMinecraftTickets newRecord = new AverageDailyMinecraftTickets();
      newRecord.setEmployeeId(employeeId);
      newRecord.setTickets(averageValue);
      averageDailyMinecraftTicketsRepository.save(newRecord);
    }
  }

  @Override
  public void calculateAverageMinecraftTicketsPerPlaytime() {
    List<DailyMinecraftTickets> rawData = getDailyMinecraftTicketsData();
    List<Employee> rawEmployeesData = getEmployeesData();
    List<Short> allEmployeeIds = getAllEmployeesFromTheTable(rawData);

    LocalDate oldestDateFromData = checkForOldestDate(rawData);

    for (Short employeeId : allEmployeeIds) {

      try {
        LocalDate joinDateForThisEmployee = getJoinDateForThisEmployee(rawEmployeesData, employeeId);
        LocalDate oldestDate = getOldestDateForThisEmployeeFromTickets(oldestDateFromData, joinDateForThisEmployee);
        int ticketsCountSinceOldestDate = calculateAllTicketsSinceOldestDate(rawData, employeeId, oldestDate);
        double playtimeSinceOldestDate = getAllPlaytimeSinceOldestDate(employeeId, oldestDateFromData);

        if (playtimeSinceOldestDate <= 0) {
          System.err.println("Invalid playtime for employee " + employeeId + ". Skipping.");
          continue;
        }

        double ticketsPerPlaytime = calculateTicketsPerPlaytime(ticketsCountSinceOldestDate, playtimeSinceOldestDate);
        saveTicketsPerPlaytime(ticketsPerPlaytime, employeeId);
      } catch (Exception e) {
        System.err.println("Error processing employee " + employeeId + ": " + e.getMessage());
      }
    }
  }

  public List<DailyPlaytime> extractDataFromDailyPlaytimeTable() {
    return dailyPlaytimeRepository.findAll();
  }

  public double getAllPlaytimeSinceOldestDate(Short targetEmployeeId, LocalDate oldestDate) {
    return dailyPlaytimeRepository
        .findAll()
        .stream()
        .filter(ticket -> ticket.getEmployeeId().equals(targetEmployeeId))
        .filter(ticket -> !ticket.getDate().isBefore(oldestDate))
        .mapToDouble(DailyPlaytime::getTimeInHours)
        .sum();
  }

  public double calculateTicketsPerPlaytime(int ticketsCount, double playtimeSinceOldestDate) {
    if (playtimeSinceOldestDate == 0) {
      return 0.0;
    }

    double playtimeInHours = playtimeSinceOldestDate;
    return ticketsCount / playtimeInHours;
  }

  public void saveTicketsPerPlaytime(double ticketsPerPlaytime, Short employeeId) {
    AverageMinecraftTicketsPerPlaytime existingRecord = averageMinecraftTicketsPerPlaytimeRepository
        .findByEmployeeId(employeeId);

    if (existingRecord != null) {
      existingRecord.setValue(ticketsPerPlaytime);
      averageMinecraftTicketsPerPlaytimeRepository.save(existingRecord);
    } else {
      AverageMinecraftTicketsPerPlaytime newRecord = new AverageMinecraftTicketsPerPlaytime();
      newRecord.setEmployeeId(employeeId);
      newRecord.setValue(ticketsPerPlaytime);
      averageMinecraftTicketsPerPlaytimeRepository.save(newRecord);
    }
  }
}
